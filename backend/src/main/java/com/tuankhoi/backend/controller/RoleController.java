package com.tuankhoi.backend.controller;

import com.tuankhoi.backend.dto.RoleDTO;
import com.tuankhoi.backend.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleDTO> findByID(@PathVariable final Integer id) {
        return ResponseEntity.ok(roleService.findByID(id));
    }

    @GetMapping("")
    public ResponseEntity<List<RoleDTO>> findAll() {
        return ResponseEntity.ok(roleService.findAll());
    }

    @PostMapping
    public ResponseEntity<RoleDTO> create(@RequestBody RoleDTO roleDTO) {
        return new ResponseEntity<>(roleService.create(roleDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleDTO> update(@PathVariable int id, @RequestBody RoleDTO roleDTO) {
        return new ResponseEntity<>(roleService.update(id, roleDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id){
        roleService.deleteByID(id);
    }
}
