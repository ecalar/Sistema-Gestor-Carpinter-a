package com.enrique.AdministradorCapinteria.ui;

import com.enrique.AdministradorCapinteria.domain.model.Cliente;
import com.enrique.AdministradorCapinteria.domain.ports.in.ClienteServicePort;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class ClientesPanel extends JPanel {
    private final ClienteServicePort clienteService;
    private JTable tablaClientes;
    private JButton btnAgregar, btnEditar, btnEliminar, btnActualizar;
    private DefaultTableModel tableModel;


    public ClientesPanel(ClienteServicePort clienteService) {
        this.clienteService = clienteService;

        initializeUI();
        iniciarCargaClientes();

    }
    private void initializeUI() {
        setLayout(new BorderLayout());
        Estilos.aplicarEstiloPanelMOderno(this);

        //Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Estilos.COLOR_FONDO);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        //Título
        JLabel titulo = new JLabel("GESTIÓN DE CLIENTES", SwingConstants.CENTER);
        Estilos.aplicarEstiloTitulo(titulo);
        panelSuperior.add(titulo, BorderLayout.NORTH);

        add(panelSuperior, BorderLayout.NORTH);

        //Crear Tabla
        String[] columNames = {"Nª", "ID", "NOMBRE", "APELLIDOS", "TELÉFONO","CALLE", "LOCALIDAD", "ESTADO"};
        tableModel = new DefaultTableModel(columNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;//hacer tabla no editable
            }
        };
        tablaClientes = new JTable(tableModel);
        Estilos.aplicarEstiloTabla(tablaClientes);

        //Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(Estilos.COLOR_FONDO);




        btnAgregar = new JButton("Agregar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnActualizar = new JButton("Actualizar");

        //Tooltips
        btnAgregar.setToolTipText("Agregar nuevo cliente al sistema");
        btnEditar.setToolTipText("Editar cliente seleccionado");
        btnEditar.setToolTipText("Selecciona un cliente de la tabla para editar");
        btnEliminar.setToolTipText("Eliminar cliente seleccionado permanentemente");
        btnActualizar.setToolTipText("Refrescar lista de clientes");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);

        Estilos.aplicarEstiloBotonModerno(btnAgregar);
        Estilos.aplicarEstiloBotonModerno(btnEditar);
        Estilos.aplicarEstiloBotonModerno(btnEliminar);
        Estilos.aplicarEstiloBotonModerno(btnActualizar);

        //Panel tabla con scroll
        JScrollPane scrollPane = new JScrollPane(tablaClientes);


        //Panel central con tabla y botones
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(Estilos.COLOR_FONDO);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        panelCentral.add(panelBotones, BorderLayout.SOUTH);

        //Margenes del panel central
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(panelCentral, BorderLayout.CENTER);

        btnActualizar.addActionListener(e -> iniciarCargaClientes());
        btnAgregar.addActionListener(e -> abrirFormularioAgregar());
        btnEditar.addActionListener(e -> abrirFormularioEditar());
        btnEliminar.addActionListener(e -> eliminarCliente());
    }
    private void setControlesEnabled(boolean enabled) {
        btnAgregar.setEnabled(enabled);
        btnEditar.setEnabled(enabled);
        btnEliminar.setEnabled(enabled);
        btnActualizar.setEnabled(enabled);
    }
    private void iniciarCargaClientes() {
        setControlesEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tableModel.setRowCount(0);

        SwingWorker<List<Cliente>, Void> worker = new SwingWorker<List<Cliente>, Void>() {
            @Override
            protected List<Cliente> doInBackground() throws Exception {
                return clienteService.buscarClientesActivos();
            }

            @Override
            protected void done() {

                try {
                    //Obtener clientes servicio
                    List<Cliente> clientes = get();
                    int numero = 1;
                    //Llenar tabla
                    for (Cliente cliente : clientes) {
                        Object[] rowData = {
                                numero++,
                                cliente.getId(),
                                cliente.getNombre(),
                                cliente.getApellido1() + " " + (cliente.getApellido2() != null ? cliente.getApellido2() : ""),
                                cliente.getTelefono(),
                                cliente.getDireccion() != null ? cliente.getDireccion() : "",
                                cliente.getLocalidad() != null ? cliente.getLocalidad() : "",
                                cliente.getActivo() ? "ACTIVO" : "INACTIVO"
                        };
                        tableModel.addRow(rowData);
                    }
                    System.out.println("✓ " + clientes.size() + "clientes cargados");
                } catch (InterruptedException | ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(ClientesPanel.this, "Error al cargar clientes: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    setControlesEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }
   private void abrirFormularioAgregar() {
        ClienteFormDialog dialog = new ClienteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null, clienteService);
        dialog.setVisible(true);

        Cliente nuevoCliente = dialog.getCliente();
        if (nuevoCliente != null){
            System.out.println("Nuevo Cliente: " + nuevoCliente.getNombre());
            iniciarCargaClientes();
        }
   }
   private void abrirFormularioEditar() {
        int selectedRow = tablaClientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un cliente para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long clienteId = (Long) tableModel.getValueAt(selectedRow, 1);
        try {
            Optional<Cliente> clienteOpt = clienteService.buscarPorId(clienteId);

            if (clienteOpt.isPresent()) {
                Cliente cliente = clienteOpt.get();
                ClienteFormDialog dialog = new ClienteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), cliente, clienteService);
                dialog.setVisible(true);

                Cliente clienteActualizado = dialog.getCliente();

                if (clienteActualizado != null) {
                    iniciarCargaClientes();
                }
            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
   }
   private void eliminarCliente() {
        int selectedRow = tablaClientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un cliente para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Estás seguro de que quieres eliminar este cliente?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Long clienteId = (Long) tableModel.getValueAt(selectedRow, 1);
            try {
                clienteService.eliminarCliente(clienteId);
                JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                iniciarCargaClientes();
            }catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar cliente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
   }
}
