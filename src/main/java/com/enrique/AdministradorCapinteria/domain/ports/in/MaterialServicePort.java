package com.enrique.AdministradorCapinteria.domain.ports.in;

import com.enrique.AdministradorCapinteria.domain.model.Material;
import com.enrique.AdministradorCapinteria.domain.model.enums.TipoMaterial;

import java.util.List;
import java.util.Optional;

public interface MaterialServicePort {
    Material crearMaterial(Material material);
    Optional<Material> buscarPorId(Long id);
    List<Material> buscarTodos();
    List<Material> buscarPorNombre(String nombre);
    List<Material> buscarPorTipo(TipoMaterial tipo);
    Material actualizarMaterial(Long id, Material materialActualizado);
    void eliminarMaterial(Long id);
}
