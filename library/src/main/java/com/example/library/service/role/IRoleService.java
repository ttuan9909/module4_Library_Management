package com.example.library.service.role;

import com.example.library.entity.Role;

import java.util.List;
import java.util.Optional;

public interface IRoleService {
    List<Role> getAllRoles();
    Optional<Role> getRoleById(Long id);
    Role createRole(Role role);
    Role updateRole(Long id, Role role);
    void deleteRole(Long id);
}
