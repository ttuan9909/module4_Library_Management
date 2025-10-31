package com.example.library.service.role;

import com.example.library.entity.Role;
import com.example.library.repository.role.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoleService implements IRoleService {

    private final RoleRepository roleRepository;

    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public Role createRole(Role role) {
        if (roleRepository.existsByRoleName(role.getRoleName())) {
            throw new IllegalArgumentException("Tên vai trò đã tồn tại!");
        }
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(Long id, Role updatedRole) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy vai trò!"));
        existingRole.setRoleName(updatedRole.getRoleName());
        return roleRepository.save(existingRole);
    }

    @Override
    public void deleteRole(Long id) {
        if (!roleRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy vai trò!");
        }
        roleRepository.deleteById(id);
    }
}
