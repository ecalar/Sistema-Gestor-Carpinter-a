package com.enrique.AdministradorCapinteria.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public String nombre;
    public String apellido1;
    public String apellido2;
    public String direccion;
    public String localidad;
    public String telefono;
    public Boolean activo = true;

}
