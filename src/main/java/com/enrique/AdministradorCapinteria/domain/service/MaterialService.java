package com.enrique.AdministradorCapinteria.domain.service;

import com.enrique.AdministradorCapinteria.domain.model.Material;
import com.enrique.AdministradorCapinteria.domain.model.enums.TipoMaterial;
import com.enrique.AdministradorCapinteria.domain.ports.in.MaterialServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.out.MaterialRepositoryPort;
import jakarta.transaction.Transactional;
import javafx.beans.property.ObjectProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MaterialService implements MaterialServicePort {
    private final MaterialRepositoryPort materialRepository;

    public MaterialService(MaterialRepositoryPort materialRepository) {
        this.materialRepository = materialRepository;
    }

    @Override
    public Material crearMaterial(Material material) {
        //Validaciones básicas
        if (material.getNombre() == null || material.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del material es obligatorio");
        }
        if (material.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        return materialRepository.save(material);
    }
    @Override
    public List<Material> buscarTodos () {
        return materialRepository.findAll();
    }
    @Override
    public Optional<Material> buscarPorId(Long id) {
        return materialRepository.findById(id);
    }
    @Override
    public List<Material> buscarPorNombre(String nombre) {
        return materialRepository.findByNombreContainingIgnoreCase(nombre);
    }
    @Override
    public List<Material> buscarPorTipo(TipoMaterial tipo) {
        return materialRepository.findByTipo(tipo);
    }
    @Override
    public Material actualizarMaterial(Long id, Material materialActualizado) {
        Optional<Material> materialExistenteOpt = materialRepository.findById(id);

        if (materialExistenteOpt.isEmpty()) {
            throw new IllegalArgumentException("Material no encontrado");
        }
        Material materialExistente =materialExistenteOpt.get();

        //Validaciones
        if (materialActualizado.getNombre() == null || materialActualizado.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del material es obligatorio");
        }
        if (materialActualizado.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }

        //Actualizar campos
        materialExistente.setNombre(materialActualizado.getNombre());
        materialExistente.setTipo(materialActualizado.getTipo());
        materialActualizado.setStock(materialActualizado.getStock());
        materialExistente.setUnidadMedida(materialActualizado.getUnidadMedida());

        return materialRepository.save(materialExistente);
    }
    @Override
    public void eliminarMaterial(Long id) {
        //Eliminacion física
        materialRepository.deleteById(id);
    }


}
