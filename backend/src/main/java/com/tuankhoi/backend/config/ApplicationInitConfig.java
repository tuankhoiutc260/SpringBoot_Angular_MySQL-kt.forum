package com.tuankhoi.backend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuankhoi.backend.entity.*;
import com.tuankhoi.backend.exception.AppException;
import com.tuankhoi.backend.exception.ErrorCode;
import com.tuankhoi.backend.repository.*;
import com.tuankhoi.backend.untils.ImageUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    SubCategoryRepository subCategoryRepository;
    PostRepository postRepository;
    CommentRepository commentRepository;
    ObjectMapper objectMapper;


    @NonFinal
    @Value("${avatar.admin.image.path}")
    private String avatarAdminImagePath;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            initializeData("data/permission.json", new TypeReference<List<Permission>>() {
            }, permissionRepository);
            initializeData("data/role.json", new TypeReference<List<Role>>() {
            }, roleRepository);

            initializeUsers();
            createAdminUser();
            initializeCategories();
            initializePosts();
            initializeComments();
        };
    }

    private String encodeImage(String imagePath) {
        try {
            return ImageUtil.getImageAsBase64(imagePath);
        } catch (IOException e) {
            log.error("Unable to encode image {}: {}", imagePath, e.getMessage());
            return null;
        }
    }

    private <T> void initializeData(String resourcePath, TypeReference<List<T>> typeReference, JpaRepository<T, ?> repository) {
        if (repository.count() == 0) {
            try (InputStream inputStream = new ClassPathResource(resourcePath).getInputStream()) {
                List<T> dataList = objectMapper.readValue(inputStream, typeReference);
                repository.saveAll(dataList);
                log.info("Data from {} has been successfully initialized", resourcePath);
            } catch (IOException e) {
                throw new AppException(ErrorCode.DATA_INITIALIZATION_FAILED, "Failed to initialize data from " + resourcePath, e);
            }
        } else {
            log.info("Data for {} already exists, skipping initialization.", resourcePath);
        }
    }

    private void initializeUsers() {
        if (userRepository.count() == 0) {
            try (InputStream inputStream = new ClassPathResource("data/user.json").getInputStream()) {
                List<User> userList = objectMapper.readValue(inputStream, new TypeReference<List<User>>() {
                });

                userList.forEach(user -> {
                    user.setPassword(passwordEncoder.encode(user.getPassword()));
                    user.setImage(encodeImage(user.getImage()));
                });

                userRepository.saveAll(userList);
                log.info("Virtual data for Users created and saved to database");
            } catch (IOException e) {
                throw new AppException(ErrorCode.DATA_INITIALIZATION_FAILED, "Unable to save Users", e);
            }
        } else {
            log.info("User table already has data, skipping data initialization.");
        }
    }

    private void initializeCategories() {
        if (subCategoryRepository.count() == 0) {
            try (InputStream inputStream = new ClassPathResource("data/category.json").getInputStream()) {
                List<Map<String, Object>> data = objectMapper.readValue(inputStream, new TypeReference<List<Map<String, Object>>>() {
                });

                data.forEach(categoryMap -> {
                    Category category = categoryRepository.save(Category.builder()
                            .title((String) categoryMap.get("title"))
                            .description((String) categoryMap.get("description"))
                            .build());

                    List<Map<String, String>> subCategories = (List<Map<String, String>>) categoryMap.get("subCategories");
                    subCategories.forEach(subCategoryMap -> {
                        SubCategory subCategory = SubCategory.builder()
                                .title(subCategoryMap.get("title"))
                                .description(subCategoryMap.get("description"))
                                .coverImage(encodeImage(subCategoryMap.get("coverImage")))
                                .category(category)
                                .build();
                        subCategoryRepository.save(subCategory);
                    });
                });

                log.info("Virtual data for Categories and SubCategories created and saved to database");
            } catch (IOException e) {
                throw new AppException(ErrorCode.DATA_INITIALIZATION_FAILED, "Unable to save Categories", e);
            }
        } else {
            log.info("Category table already has data, skipping data initialization.");
        }
    }

    private void initializePosts() {
        if (postRepository.count() == 0) {
            try (InputStream inputStream = new ClassPathResource("data/post.json").getInputStream()) {
                List<Post> postList = objectMapper.readValue(inputStream, new TypeReference<List<Post>>() {
                });

                Map<String, User> userMap = userRepository.findAll().stream()
                        .collect(Collectors.toMap(User::getUserName, Function.identity()));
                Map<String, SubCategory> subCategoryMap = subCategoryRepository.findAll().stream()
                        .collect(Collectors.toMap(SubCategory::getTitle, Function.identity()));

                postList.forEach(post -> {
                    User existingUser = userMap.get(post.getCreatedBy());
                    SubCategory existingSubCategory = subCategoryMap.get(post.getSubCategory().getTitle());
                    post.setCreatedBy(existingUser.getId());
                    post.setLastModifiedBy(existingUser.getId());
                    post.setSubCategory(existingSubCategory);
                });

                postRepository.saveAll(postList);
                log.info("Virtual data for Posts created and saved to database");
            } catch (IOException e) {
                throw new AppException(ErrorCode.DATA_INITIALIZATION_FAILED, "Unable to save Posts", e);
            }
        } else {
            log.info("Post table already has data, skipping data initialization.");
        }
    }

    private void initializeComments() {
        if (commentRepository.count() == 0) {
            try (InputStream inputStream = new ClassPathResource("data/comment.json").getInputStream()) {
                List<Comment> commentList = objectMapper.readValue(inputStream, new TypeReference<List<Comment>>() {
                });

                Map<String, User> userMap = userRepository.findAll().stream()
                        .collect(Collectors.toMap(User::getUserName, Function.identity()));
                Map<String, Post> postMap = postRepository.findAll().stream()
                        .collect(Collectors.toMap(Post::getTitle, Function.identity()));

                commentList.forEach(comment -> {
                    User existingUser = userMap.get(comment.getCreatedBy());
                    Post existingPost = postMap.get(comment.getPost().getTitle());
                    comment.setCreatedBy(existingUser.getId());
                    comment.setPost(existingPost);
                });

                commentRepository.saveAll(commentList);
                log.info("Virtual data for Comments created and saved to database");
            } catch (IOException e) {
                throw new AppException(ErrorCode.DATA_INITIALIZATION_FAILED, "Unable to save Comments", e);
            }
        } else {
            log.info("Comment table already has data, skipping data initialization.");
        }
    }

    private void createAdminUser() {
        roleRepository.findByName("ADMIN").ifPresentOrElse(
                adminRole -> {
                    if (userRepository.findByUserName("admin").isEmpty()) {
                        User user = User.builder()
                                .email("admin@gmail.com")
                                .userName("admin")
                                .password(passwordEncoder.encode("admin"))
                                .fullName("Admin")
                                .image(encodeImage(avatarAdminImagePath))
                                .role(adminRole)
                                .active(true)
                                .build();
                        userRepository.save(user);
                        log.info("Admin user has been created with default Email: admin@gmail.com and Password: admin");
                    } else {
                        log.info("Admin user already exists, skipping creation.");
                    }
                },
                () -> log.warn("Admin role not found, cannot create admin user")
        );
    }
}