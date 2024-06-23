package com.tuankhoi.backend.initializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuankhoi.backend.model.Role;
import com.tuankhoi.backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


@Component
public class RoleDataInitializer implements CommandLineRunner {
    private final RoleRepository roleRepository;

    @Autowired
    public RoleDataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() == 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<List<Role>> typeReference = new TypeReference<List<Role>>() {
            };

            try {
                ClassPathResource resource = new ClassPathResource("data/role.json");
                InputStream inputStream = resource.getInputStream();

                List<Role> cities = objectMapper.readValue(inputStream, typeReference);
                roleRepository.saveAll(cities);
                System.out.println("Roles saved to database");
            } catch (IOException e) {
                System.out.println("Unable to save roles: " + e.getMessage());
            }
        } else {
            System.out.println("Role table already has data, skipping data initialization.");
        }
    }
}
