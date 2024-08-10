package com.tuankhoi.backend.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuankhoi.backend.dto.request.RoleRequest;
import com.tuankhoi.backend.entity.*;
import com.tuankhoi.backend.mapper.RoleMapper;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Configuration
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    RoleMapper roleMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    SubCategoryRepository subCategoryRepository;

    @NonFinal
    @Value("${avatar.admin.image.path}")
    private String avatarAdminImagePath;

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            initializePermissions();
            initializeRoles();
            createAdminUser();
            this.initializeCategories();
        };
    }

    private void initializePermissions() {
        if (permissionRepository.count() == 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<Permission>> typeReference = new TypeReference<>() {
            };
            try {
                ClassPathResource resource = new ClassPathResource("data/permission.json");
                InputStream inputStream = resource.getInputStream();
                List<Permission> permissionsList = objectMapper.readValue(inputStream, typeReference);
                permissionRepository.saveAll(permissionsList);
                log.info("Permissions saved to database");
            } catch (IOException e) {
                log.error("Unable to save permission: {}", e.getMessage(), e);
            }
        } else {
            log.info("Permission table already has data, skipping data initialization.");
        }
    }

    private void initializeRoles() {
        if (roleRepository.count() == 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<RoleRequest>> typeReference = new TypeReference<>() {
            };
            try {
                ClassPathResource resource = new ClassPathResource("data/role.json");
                InputStream inputStream = resource.getInputStream();
                List<RoleRequest> roleRequestList = objectMapper.readValue(inputStream, typeReference);

                for (RoleRequest roleRequest : roleRequestList) {
                    Role role = roleMapper.toRole(roleRequest);
                    Set<Permission> permissions = new HashSet<>();
                    for (Integer permissionId : roleRequest.getPermissions()) {
                        Optional<Permission> permission = permissionRepository.findById(permissionId);
                        permission.ifPresent(permissions::add);
                    }
//                    role.setPermissions(permissions);
                    roleRepository.save(role);
                }

                log.info("Roles saved to database");
            } catch (IOException e) {
                log.error("Unable to save roles: {}", e.getMessage(), e);
            }
        } else {
            log.info("Role table already has data, skipping data initialization.");
        }
    }

    private void initializeCategories() {
        if (subCategoryRepository.count() == 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource("data/category.json");

            try {
                List<Map<String, Object>> data = objectMapper.readValue(resource.getInputStream(), List.class);

                for (Map<String, Object> categoryMap : data) {
                    Category category = new Category();
                    category.setTitle((String) categoryMap.get("title"));
                    category.setDescription((String) categoryMap.get("description"));
                    category = categoryRepository.save(category);

                    List<Map<String, String>> subCategories = (List<Map<String, String>>) categoryMap.get("subCategories");
                    for (Map<String, String> subCategoryMap : subCategories) {
                        SubCategory subCategory = new SubCategory();
                        subCategory.setTitle(subCategoryMap.get("title"));
                        subCategory.setDescription(subCategoryMap.get("description"));
                        subCategory.setCoverImage(ImageUtil.getImageAsBase64(subCategoryMap.get("cover_image")));
                        subCategory.setCategory(category);
                        subCategoryRepository.save(subCategory);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            log.info("Category table already has data, skipping data initialization.");
        }
    }



    private void createAdminUser() throws IOException {
        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);
        if (adminRole == null) {
            log.warn("Admin role not found, cannot create admin user");
            return;
        }
        if (userRepository.findByUserName("admin1").isEmpty()) {
            User user = User.builder()
                    .email("admin@gmail.com")
                    .userName("admin")
                    .password(passwordEncoder.encode("admin"))
                    .fullName("Admin")
                    .image(ImageUtil.getImageAsBase64(avatarAdminImagePath))
                    .role(adminRole)
                    .active(true)
                    .build();
            User user2 = User.builder()
                    .email("admin1@gmail.com")
                    .userName("admin1")
                    .password(passwordEncoder.encode("admin"))
                    .fullName("Admin1")
                    .image(ImageUtil.getImageAsBase64(avatarAdminImagePath))
                    .role(adminRole)
                    .active(true)
                    .build();
            userRepository.save(user2);
            log.info("Admin user has been created with default Email: admin@gmail.com and Password: admin");
        } else {
            log.info("Admin user already exists, skipping creation.");
        }
    }
}