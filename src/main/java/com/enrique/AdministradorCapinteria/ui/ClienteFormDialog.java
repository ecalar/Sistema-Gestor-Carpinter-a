package com.enrique.AdministradorCapinteria.ui;

import com.enrique.AdministradorCapinteria.domain.model.Cliente;
import com.enrique.AdministradorCapinteria.domain.ports.in.ClienteServicePort;

import javax.swing.*;
import java.awt.*;

public class ClienteFormDialog extends JDialog {
    private JTextField txtNombre, txtApellido1, txtApellido2, txtTelefono, txtDireccion, txtLocalidad;
    private JButton btnGuardar, btnCancelar;
    private Cliente cliente;
    private boolean guardado = false;
    private ClienteServicePort clienteService;

    public ClienteFormDialog(JFrame parent, Cliente cliente, ClienteServicePort clienteService) {
        super(parent, true);
        this.cliente = cliente;
        this.clienteService = clienteService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle(cliente == null ? "Agregar Cliente" : "Editar Cliente");
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Campos del formulario
        panel.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panel.add(txtNombre);

        panel.add(new JLabel("Apellido 1:"));
        txtApellido1 = new JTextField();
        panel.add(txtApellido1);

        panel.add(new JLabel("Apellido 2:"));
        txtApellido2 = new JTextField();
        panel.add(txtApellido2);

        panel.add(new JLabel("Teléfono:"));
        txtTelefono = new JTextField();
        panel.add(txtTelefono);

        panel.add(new JLabel("Dirección:"));
        txtDireccion = new JTextField();
        panel.add(txtDireccion);

        panel.add(new JLabel("Localidad:"));
        txtLocalidad = new JTextField();
        panel.add(txtLocalidad);

        //Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        //Si se edita, cargar datos existentes
        if (cliente != null) {
            cargarDatosCliente();
        }

        //Eventos
        btnGuardar.addActionListener(e -> guardarCliente());
        btnCancelar.addActionListener(e -> dispose());

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarDatosCliente() {
        txtNombre.setText(cliente.getNombre());
        txtApellido1.setText(cliente.getApellido1());
        txtApellido2.setText(cliente.getApellido2());
        txtTelefono.setText(cliente.getTelefono());
        txtDireccion.setText(cliente.getDireccion());
        txtLocalidad.setText(cliente.getLocalidad());
    }

    private void guardarCliente() {
        //Validaciones
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (txtTelefono.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El teléfono es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //Crear o actualizar cliente
        try {
            if (cliente == null) {
                cliente = new Cliente();


                cliente.setNombre(txtNombre.getText().trim());
                cliente.setApellido1(txtApellido1.getText().trim());
                cliente.setApellido2(txtApellido2.getText().trim());
                cliente.setTelefono(txtTelefono.getText().trim());
                cliente.setDireccion(txtDireccion.getText().trim());
                cliente.setLocalidad(txtLocalidad.getText().trim());
                cliente.setActivo(true);
                clienteService.crearCliente(cliente);
            } else {
                cliente.setNombre(txtNombre.getText().trim());
                cliente.setApellido1(txtApellido1.getText().trim());
                cliente.setApellido2(txtApellido2.getText().trim());
                cliente.setTelefono(txtTelefono.getText().trim());
                cliente.setDireccion(txtDireccion.getText().trim());
                cliente.setLocalidad(txtLocalidad.getText().trim());

                clienteService.actualizarCliente(cliente.getId(), cliente);
            }

            guardado = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public Cliente getCliente() {
        return guardado ? cliente : null;
    }
}
