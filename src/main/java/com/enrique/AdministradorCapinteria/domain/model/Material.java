package com.enrique.AdministradorCapinteria.domain.model;

import com.enrique.AdministradorCapinteria.domain.model.enums.TipoMaterial;
import com.enrique.AdministradorCapinteria.domain.model.enums.UnidadMedida;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public String nombre;

    @Enumerated(EnumType.STRING)
    public TipoMaterial tipo;

    public double stock;

    @Enumerated(EnumType.STRING)
    public UnidadMedida unidadMedida;

    //Campos medidas para MADERA
    private Double anchoCm;
    private Double altoCm;
    private Double gruesoCm;
}
