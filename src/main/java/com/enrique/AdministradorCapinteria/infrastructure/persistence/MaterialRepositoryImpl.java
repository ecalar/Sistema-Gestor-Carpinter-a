package com.enrique.AdministradorCapinteria.infrastructure.persistence;

import com.enrique.AdministradorCapinteria.domain.model.Material;
import com.enrique.AdministradorCapinteria.domain.ports.out.MaterialRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaterialRepositoryImpl extends JpaRepository<Material, Long>, MaterialRepositoryPort {
}
