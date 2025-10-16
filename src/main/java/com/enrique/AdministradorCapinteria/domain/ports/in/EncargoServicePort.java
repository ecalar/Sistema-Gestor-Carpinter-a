package com.enrique.AdministradorCapinteria.domain.ports.in;

import com.enrique.AdministradorCapinteria.domain.model.Encargo;
import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoEncargo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EncargoServicePort {

    //Crear nuevo encargo PRESUPUESTADO
    Encargo crearEncargo(Encargo encargo, Long clienteId);

    //Buscar encargo por ID
    Optional<Encargo> buscarPorId(Long id);

    //Obtener todos los encargos
    List<Encargo> buscarTodos();

    //Buscar encargos por estado
    List<Encargo> buscarPorEstado(EstadoEncargo estado);

    //Buscar por cliente
    List<Encargo> buscarPorCliente(Long clienteId);

    //Cambiar el estado del encargo
    Encargo cambiarEstado(Long encargoId, EstadoEncargo nuevoEstado);

    //Marcar como pagado(estadoPago = PAGADO y fechaPago = hoy)
    Encargo marcarComoPagado(Long encargoId, LocalDate fechaPago);

    //Buscar encargos terminados pero no pagados
    List<Encargo> buscarParaFacturacion();

    //Actualizar encargo
    Encargo actualizarEncargo(Long id, Encargo encargoActualizado);

    //Eliminar Encargo
    void  eliminarEncargo(Long id);

}
