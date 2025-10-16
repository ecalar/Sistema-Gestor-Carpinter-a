package com.enrique.AdministradorCapinteria.domain.model;

import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoEncargo;
import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoPago;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Encargo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;
    public String tipoMueble;
    public double anchoCm;
    public double altoCm;
    public double fondoCm;
    public String tipoMadera;
    public double precio;

    @Enumerated(EnumType.STRING)
    public EstadoEncargo estado = EstadoEncargo.PRESUPUESTADO;

    @Enumerated(EnumType.STRING)
    public EstadoPago estadoPago = EstadoPago.PENDIENTE;

    public LocalDate fechaEncargo;
    public LocalDate fechaEntrega;
    public LocalDate fechaPago;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
}
