package com.enrique.AdministradorCapinteria.ui;

import com.enrique.AdministradorCapinteria.domain.model.Cliente;
import com.enrique.AdministradorCapinteria.domain.model.Encargo;
import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoEncargo;
import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoPago;
import com.enrique.AdministradorCapinteria.domain.ports.in.ClienteServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.in.EncargoServicePort;

import javax.swing.*;
import java.util.List;
import java.awt.*;
import java.time.LocalDate;

public class EncargoFormDialog extends JDialog {

    private JTextField txtDescripcion, txtTipoMueble, txtAncho, txtAlto, txtFondo, txtTipoMadera, txtPrecio;
    private JComboBox<Cliente> comboClientes;
    private JComboBox<EstadoEncargo> comboEstado;
    private JComboBox<EstadoPago> comboEstadoPago;
    private JButton btnGuardar, btnCancelar;
    private Encargo encargo;
    private boolean guardado = false;
    private EncargoServicePort encargoService;
    private ClienteServicePort clienteService;

    public EncargoFormDialog(JFrame parent, Encargo encargo, EncargoServicePort encargoService, ClienteServicePort clienteService) {
        super(parent, true);
        this.encargo = encargo;
        this.encargoService = encargoService;
        this.clienteService = clienteService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle(encargo == null ? "Agregar Encargo" : "Editar Encargo");
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panel = new JPanel(new GridLayout(12, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Cliente (Combobox)
        panel.add(new JLabel("Cliente:"));
        comboClientes = new JComboBox<>();
        cargarClientesActivos();
        panel.add(comboClientes);

        //Descripción
        panel.add(new JLabel("Descripción:"));
        txtDescripcion = new JTextField();
        panel.add(txtDescripcion);

        //TipoMueble
        panel.add(new JLabel("Tipo de Mueble:"));
        txtTipoMueble = new JTextField();
        panel.add(txtTipoMueble);

        //Medidas
        panel.add(new JLabel("Ancho (cm):"));
        txtAncho = new JTextField();
        panel.add(txtAncho);

        panel.add(new JLabel("Alto (cm):"));
        txtAlto = new JTextField();
        panel.add(txtAlto);

        panel.add(new JLabel("Fondo (cm):"));
        txtFondo = new JTextField();
        panel.add(txtFondo);

        //TipoMadera
        panel.add(new JLabel("Tipo de Madera:"));
        txtTipoMadera = new JTextField();
        panel.add(txtTipoMadera);

        //Precio
        panel.add(new JLabel("Precio (€):"));
        txtPrecio = new JTextField();
        panel.add(txtPrecio);

        //Estado del encargo
        panel.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>(EstadoEncargo.values());
        panel.add(comboEstado);

        //EstadoPago
        panel.add(new JLabel("Estado Pago:"));
        comboEstadoPago = new JComboBox<>(EstadoPago.values());
        comboEstadoPago.setEnabled(false);
        comboEstadoPago.setSelectedItem(EstadoPago.PENDIENTE);
        panel.add(comboEstadoPago);

        //Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        //Cargar datos si se edita
        if (encargo != null) {
            cargarDatosEncargo();
        }

        //Eventos
        btnGuardar.addActionListener(e -> guardarEncargo());
        btnCancelar.addActionListener(e -> dispose());

        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private void cargarClientesActivos() {
        try {
            //Hacer metodo para obtener clientes activos, de momento los busca todos
            List<Cliente> clientes = clienteService.buscarClientesActivos();

            for (Cliente cliente : clientes) {
                if (cliente.getActivo()) {
                    comboClientes.addItem(cliente);
                }
            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
    private void cargarDatosEncargo() {
        txtDescripcion.setText(encargo.getDescripcion());
        txtTipoMueble.setText(encargo.getTipoMueble());
        txtAncho.setText(String.valueOf(encargo.getAnchoCm()));
        txtAlto.setText(String.valueOf(encargo.getAltoCm()));
        txtFondo.setText(String.valueOf(encargo.getFondoCm()));
        txtTipoMadera.setText(encargo.getTipoMadera());
        txtPrecio.setText(String.valueOf(encargo.getPrecio()));
        comboEstado.setSelectedItem(encargo.getEstado());
        comboEstadoPago.setSelectedItem(EstadoPago.PENDIENTE);

        //Seleccionar cliente en comoBox
        if (encargo.getCliente() != null) {
            for (int i = 0; i <comboClientes.getItemCount(); i++) {
                if (comboClientes.getItemAt(i).getId().equals(encargo.getCliente().getId())) {
                    comboClientes.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    private void guardarEncargo() {
        //Validaciones
        if (txtDescripcion.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "La descripción es obligatoria", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (comboClientes.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un cliente", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            //Crear o actualizar encargo
            if (encargo == null) {
                encargo = new Encargo();
                encargo.setFechaEncargo(LocalDate.now());
            }
            //Asignar valores
            encargo.setDescripcion(txtDescripcion.getText().trim());
            encargo.setTipoMueble(txtTipoMueble.getText().trim());
            encargo.setAnchoCm(Double.parseDouble(txtAncho.getText().trim()));
            encargo.setAltoCm(Double.parseDouble(txtAlto.getText().trim()));
            encargo.setFondoCm(Double.parseDouble(txtFondo.getText().trim()));
            encargo.setTipoMadera(txtTipoMadera.getText().trim());
            encargo.setPrecio(Double.parseDouble(txtPrecio.getText().trim()));
            encargo.setEstado((EstadoEncargo) comboEstado.getSelectedItem());
            encargo.setEstadoPago((EstadoPago.PENDIENTE));
            encargo.setCliente((Cliente) comboClientes.getSelectedItem());

            //guardar en BBDD
            if (encargo.getId() == null) {
                encargoService.crearEncargo(encargo, encargo.getCliente().getId());
            }else {
                encargoService.actualizarEncargo(encargo.getId(), encargo);
                JOptionPane.showMessageDialog(this, "Encargo Actualizado:");
            }
            guardado = true;
            dispose();
        }catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error en formato numérico: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar encargo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public Encargo getEncargo() {
        return guardado ? encargo : null;
    }
}
