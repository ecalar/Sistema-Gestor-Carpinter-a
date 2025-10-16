package com.enrique.AdministradorCapinteria.domain.service;

import com.enrique.AdministradorCapinteria.domain.model.Cliente;
import com.enrique.AdministradorCapinteria.domain.ports.in.ClienteServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.out.ClienteRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService implements ClienteServicePort {
    private final ClienteRepositoryPort clienteRepository;


    public ClienteService(ClienteRepositoryPort clienteRepository) {
        this.clienteRepository = clienteRepository;
    }
    @Override
    public Cliente crearCliente(Cliente cliente){
        //Validar campos obligatorios
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }
        //Validar que no haya tlfn repetidos
        List<Cliente> clientesConMismoTelefono = clienteRepository.findByTelefonoContaining(cliente.getTelefono());
        if (!clientesConMismoTelefono.isEmpty()) {
            throw new IllegalArgumentException("Ya existe un cliente con este teléfono");
        }
        //Cliente activo al crearlo
        cliente.setActivo(true);
        //Guardar cliente
        return clienteRepository.save(cliente);
    }
    @Override
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }
    @Override
    public List<Cliente> buscarTodos(){
        return clienteRepository.findByActivoTrue();
    }
    @Override
    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }
    @Override
    public void eliminarCliente(Long id) {
        Optional<Cliente> cliente = clienteRepository.findById(id);
        if(cliente.isPresent()) {
            Cliente clienteExistente = cliente.get();
            clienteExistente.setActivo(false);
            clienteRepository.save(clienteExistente);
        }
    }
    @Override
    public Cliente actualizarCliente(Long id, Cliente clienteActualizado) {
        //Buscar cliente
        Optional<Cliente> clienteExistenteOpt = clienteRepository.findById(id);
        if (clienteExistenteOpt.isEmpty()) {
            throw new IllegalArgumentException("Cliente no encontrado con ID:" + id);
        }
        Cliente clienteExistente = clienteExistenteOpt.get();

        //Validar campos obligatorios
        if (clienteActualizado.getNombre() == null || clienteActualizado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (clienteActualizado.getTelefono() == null || clienteActualizado.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono es obligatorio");
        }
        //Validar unico teléfono
        List<Cliente> clienteConMismoTelefono = clienteRepository.findByTelefonoContaining(clienteActualizado.getTelefono());
        boolean telefonoDuplicado = clienteConMismoTelefono.stream().anyMatch(c -> !c.getId().equals(id));

        if (telefonoDuplicado) {
            throw new IllegalArgumentException("Ya existe otro cliente con este teléfono");
        }
        //Actualiar campos permitidos (Todos menos id y activo)
        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setApellido1(clienteActualizado.getApellido1());
        clienteExistente.setApellido2(clienteActualizado.getApellido2());
        clienteExistente.setDireccion(clienteActualizado.getDireccion());
        clienteExistente.setLocalidad(clienteActualizado.getLocalidad());
        clienteExistente.setTelefono(clienteActualizado.getTelefono());

        //Guardar
        return clienteRepository.save(clienteExistente);
    }
    @Override
    public List<Cliente> buscarClientesActivos() {
        return clienteRepository.findByActivoTrue();
    }
}
