package com.ecommerce.project.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer roleId;

	@ToString.Exclude
	@Enumerated(EnumType.STRING)
	@Column(name = "role_name", length = 50)
	private AppRole appRole;
}
