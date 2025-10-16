package com.enrique.AdministradorCapinteria.domain.ports.out;

import com.enrique.AdministradorCapinteria.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepositoryPort {
    //CRUD
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(Long id);
    List<Cliente> findAll();
    void delete(Cliente cliente);

    //Busquedas específicas
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
    List<Cliente> findByApellido1ContainingIgnoreCase(String apellido);
    List<Cliente> findByTelefonoContaining(String telefono);
    List<Cliente> findByLocalidad(String localidad);

    //Borrado Lógico
    List<Cliente> findByActivoTrue();
    List<Cliente> findByActivoFalse();
}
