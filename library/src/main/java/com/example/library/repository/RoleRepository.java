package com.example.library.repository;

import com.example.library.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Tìm role theo tên (ví dụ: "ADMIN", "USER")
    Optional<Role> findByRoleName(String roleName);

    // Kiểm tra role có tồn tại chưa
   boolean existsByRoleName(String roleName);

    @Override
    Optional<Role> findById(Long aLong);

}
