package com.enrique.AdministradorCapinteria.infrastructure.persistence;

import com.enrique.AdministradorCapinteria.domain.model.Cliente;
import com.enrique.AdministradorCapinteria.domain.ports.out.ClienteRepositoryPort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepositoryImpl extends JpaRepository<Cliente, Long>, ClienteRepositoryPort {
}
