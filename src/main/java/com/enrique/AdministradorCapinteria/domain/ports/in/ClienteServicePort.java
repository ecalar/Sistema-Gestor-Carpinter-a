package com.enrique.AdministradorCapinteria.domain.ports.in;

import com.enrique.AdministradorCapinteria.domain.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClienteServicePort {
    //Crear Cliente
    Cliente crearCliente(Cliente cliente);

    //Buscar Cliente por Id
    Optional<Cliente> buscarPorId(Long id);

    //Buscar por nombre
    List<Cliente> buscarPorNombre(String nombre);

    //Eliminar Cliente
    void eliminarCliente(Long id);

    //Actualizar Datos Cliente
    Cliente actualizarCliente(Long id, Cliente clienteActualizado);

    List<Cliente> buscarTodos();

    List<Cliente> buscarClientesActivos();
}
