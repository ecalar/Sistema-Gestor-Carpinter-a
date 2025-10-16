package com.enrique.AdministradorCapinteria.domain.service;

import com.enrique.AdministradorCapinteria.domain.model.Cliente;
import com.enrique.AdministradorCapinteria.domain.model.Encargo;
import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoEncargo;
import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoPago;
import com.enrique.AdministradorCapinteria.domain.ports.in.ClienteServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.in.EncargoServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.out.ClienteRepositoryPort;
import com.enrique.AdministradorCapinteria.domain.ports.out.EncargoRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EncargoService implements EncargoServicePort {

    private final EncargoRepositoryPort encargoRepository;
    private final ClienteRepositoryPort clienteRepository;

    public EncargoService(EncargoRepositoryPort encargoRepository, ClienteRepositoryPort clienteRepository) {
        this.encargoRepository = encargoRepository;
        this.clienteRepository = clienteRepository;
    }
    @Override
    public Encargo crearEncargo(Encargo encargo, Long clienteId) {
        //Validar si el cliente existe y es activo
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty() || !clienteOpt.get().getActivo()) {
            throw new IllegalArgumentException("Cliente no encontrado o inactivo");
        }
        Cliente cliente = clienteOpt.get();

        //Validar campos obligatorios
        if (encargo.getDescripcion() == null || encargo.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripcion del encargo es obligatoria");
        }
        if (encargo.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (encargo.getFechaEncargo() == null) {
            throw new IllegalArgumentException("La fecha de encargo es obligatoria");
        }
        if (encargo.getFechaEntrega() !=null && encargo.getFechaEntrega().isBefore(encargo.getFechaEncargo())) {
            throw new IllegalArgumentException("La fecha de entrega no puede ser anterior a la fecha de encargo");
        }

        //Establecer valores por defecto
        encargo.setEstado(EstadoEncargo.PRESUPUESTADO);
        encargo.setEstadoPago(EstadoPago.PENDIENTE);
        encargo.setCliente(cliente);

        //Guardar encargo
        return encargoRepository.save(encargo);
    }
    @Override
    public Optional<Encargo> buscarPorId(Long id) {
        return encargoRepository.findById(id);
    }
    @Override
    public List<Encargo> buscarTodos()  {
        return encargoRepository.findAll();
    }
    @Override
    public List<Encargo> buscarPorEstado(EstadoEncargo estado) {
        return encargoRepository.findByEstado(estado);
    }
    @Override
    public List<Encargo> buscarPorCliente(Long clienteId) {
        return encargoRepository.findByClienteId(clienteId);
    }
    private boolean esTransicionValida(EstadoEncargo estadoActual, EstadoEncargo nuevoEstado) {
        return switch (estadoActual) {
            case PRESUPUESTADO -> nuevoEstado == EstadoEncargo.ACEPTADO;
            case ACEPTADO -> nuevoEstado == EstadoEncargo.FABRICACION;
            case FABRICACION -> nuevoEstado == EstadoEncargo.TERMINADO;
            case TERMINADO -> nuevoEstado == EstadoEncargo.ENTREGADO;
            case ENTREGADO -> false;
        };
    }
    @Override
    public Encargo cambiarEstado(Long encargoId, EstadoEncargo nuevoEstado) {
        //Buscar encargo
        Optional<Encargo> encargoOpt = encargoRepository.findById(encargoId);
        if (encargoOpt.isEmpty()) {
            throw new IllegalArgumentException("Encargo no encontrado");
        }

        Encargo encargo = encargoOpt.get();
        EstadoEncargo estadoActual = encargo.getEstado();

        //Validar estado duplicado
        if (estadoActual == nuevoEstado) {
            return encargo;
        }

        //validar transicion permitida
        if(!esTransicionValida(estadoActual, nuevoEstado)) {
            throw new IllegalArgumentException("No se puede cambiar de " + estadoActual + " a " + nuevoEstado);
        }

        //Aplicar nuevo estado
        encargo.setEstado(nuevoEstado);
        return encargoRepository.save(encargo);
    }
    @Override
    public Encargo marcarComoPagado(Long encargoId, LocalDate fechaPago) {
        //Buscar encargo
        Optional<Encargo> encagoOpt = encargoRepository.findById(encargoId);
        if (encagoOpt.isEmpty()) {
            throw new IllegalArgumentException("Encargo no encontrado");
        }

        Encargo encargo = encagoOpt.get();

        //Validar fecha de pago
        if (fechaPago.isBefore(encargo.getFechaEncargo())) {
            throw new IllegalArgumentException("La fecha de pago no puede ser anterior a la fecha de encargo");
        }

        //Marcar como pagado
        encargo.setEstadoPago(EstadoPago.PAGADO);
        encargo.setFechaPago(fechaPago);

        return encargoRepository.save(encargo);
    }
    @Override
    public List<Encargo> buscarParaFacturacion() {
        return encargoRepository.findByEstadoAndEstadoPago(EstadoEncargo.TERMINADO, EstadoPago.PENDIENTE);
    }
    @Override
    public Encargo actualizarEncargo(Long id, Encargo encargoActualizado) {
        //Buscar encargo existente
        Optional<Encargo> encargoExistenteOpt = encargoRepository.findById(id);
        if (encargoExistenteOpt.isEmpty()) {
            throw new IllegalArgumentException("Encargo no encontrado con ID: " + id);
        }
        Encargo encargoExistente = encargoExistenteOpt.get();

        //Validaciones
        if (encargoActualizado.getDescripcion() == null || encargoActualizado.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria");
        }
        if (encargoActualizado.getCliente() == null) {
            throw new IllegalArgumentException("El cliente es obligatorio");
        }

        //Verificar si cliente existe y es activo
        Optional<Cliente> clienteOpt = clienteRepository.findById(encargoActualizado.getCliente().getId());
        if (clienteOpt.isEmpty() || !clienteOpt.get().getActivo()) {
            throw new IllegalArgumentException("Cliente no encontrado o inactivo");
        }

        //Validar fechas
        if (encargoActualizado.getFechaEntrega() != null && encargoActualizado.getFechaEntrega().isBefore(encargoActualizado.getFechaEncargo())) {
            throw new IllegalArgumentException("La fecha de entrega no puede ser anterior a la fecha de encargo");
        }

        //Actualizar campos
        encargoExistente.setDescripcion(encargoActualizado.getDescripcion());
        encargoExistente.setTipoMueble(encargoActualizado.getTipoMueble());
        encargoExistente.setAnchoCm(encargoActualizado.getAnchoCm());
        encargoExistente.setAltoCm(encargoActualizado.getAltoCm());
        encargoExistente.setFondoCm(encargoActualizado.getFondoCm());
        encargoExistente.setTipoMadera(encargoActualizado.getTipoMadera());
        encargoExistente.setPrecio(encargoActualizado.getPrecio());
        encargoExistente.setEstado(encargoActualizado.getEstado());
        //encargoExistente.setEstadoPago(encargoActualizado.getEstadoPago());
        encargoExistente.setCliente(encargoActualizado.getCliente());
        encargoExistente.setFechaEncargo(encargoActualizado.getFechaEncargo());
        encargoExistente.setFechaEntrega(encargoActualizado.getFechaEntrega());
        //encargoExistente.setFechaPago(encargoActualizado.getFechaPago());

        //Cambiar fecha de entrega si no tiene
        if (encargoActualizado.getEstado() == EstadoEncargo.ENTREGADO && encargoExistente.getFechaEntrega() == null) {
            encargoExistente.setFechaEntrega(LocalDate.now());
        }

        //cambiar fecha de pago si no tiene
        if (encargoActualizado.getEstadoPago() == EstadoPago.PAGADO && encargoExistente.getFechaEncargo() == null) {
            encargoExistente.setFechaPago(LocalDate.now());
        }
        return encargoRepository.save(encargoExistente);
    }
    @Override
    public void eliminarEncargo(Long id) {
        Optional<Encargo> encargoOpt = encargoRepository.findById(id);
        if (encargoOpt.isPresent()) {
            encargoRepository.delete(encargoOpt.get());
        }else {
            throw new IllegalArgumentException("Encargo no encontrado con ID: " + id);
        }
    }




}
