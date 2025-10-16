package com.enrique.AdministradorCapinteria.infrastructure.persistence;

import com.enrique.AdministradorCapinteria.domain.model.Encargo;
import com.enrique.AdministradorCapinteria.domain.ports.out.EncargoRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EncargoRepositoryImpl extends JpaRepository<Encargo, Long>, EncargoRepositoryPort {
}
