package com.enrique.AdministradorCapinteria.domain.ports.out;

import com.enrique.AdministradorCapinteria.domain.model.Encargo;
import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoEncargo;
import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoPago;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EncargoRepositoryPort {
    Encargo save(Encargo encargo);
    Optional<Encargo> findById(Long id);
    List<Encargo> findAll();
    void delete(Encargo encargo);

    //Busqueda por Estado
    List<Encargo> findByEstado(EstadoEncargo estado);
    List<Encargo> findByEstadoPago(EstadoPago estadoPago);

    //Facturacion
    List<Encargo> findByEstadoAndEstadoPago(EstadoEncargo estado, EstadoPago estadoPago);

    //Por cliente
    List<Encargo> findByClienteId(Long clienteId);

    //Por fechas
    List<Encargo> findByFechaEntregaBetween(LocalDate inicio, LocalDate fin);

    //
}
