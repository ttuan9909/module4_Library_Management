package com.example.library.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
@Entity
@Builder
public class Role {
    @Id
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;
}
