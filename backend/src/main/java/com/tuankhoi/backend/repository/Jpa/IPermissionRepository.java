package com.tuankhoi.backend.repository.Jpa;

import com.tuankhoi.backend.model.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPermissionRepository extends JpaRepository<Permission, Integer> {
    Optional<Permission> findByName(String name);
}
