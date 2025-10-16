package com.enrique.AdministradorCapinteria.domain.ports.out;

import com.enrique.AdministradorCapinteria.domain.model.Material;
import com.enrique.AdministradorCapinteria.domain.model.enums.TipoMaterial;

import java.util.List;
import java.util.Optional;

public interface MaterialRepositoryPort {
    Material save(Material material);
    Optional<Material> findById(Long id);
    List<Material> findAll();
    void delete(Material material);
    void deleteById(Long id);

    //Busquedas
    List<Material> findByNombreContainingIgnoreCase(String nombre);
    List<Material> findByTipo(TipoMaterial tipo);
}
