package com.enrique.AdministradorCapinteria.ui;

import com.enrique.AdministradorCapinteria.domain.model.Encargo;
import com.enrique.AdministradorCapinteria.domain.ports.in.ClienteServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.in.EncargoServicePort;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class EncargosPanel extends JPanel {
    private final EncargoServicePort encargoService;
    private final ClienteServicePort clienteService;
    private JTable tablaEncargos;
    private JButton btnAgregar, btnEditar, btnEliminar, btnActualizar;
    private DefaultTableModel tableModel;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public EncargosPanel(EncargoServicePort encargoService, ClienteServicePort clienteService) {
        this.encargoService = encargoService;
        this.clienteService = clienteService;
        initializeUI();
        cargarEncargos();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        Estilos.aplicarEstiloPanelMOderno(this);

        //Titulo
        JLabel titulo = new JLabel("GESTIÓN DE ENCARGOS", SwingConstants.CENTER);
        Estilos.aplicarEstiloTitulo(titulo);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titulo, BorderLayout.NORTH);

        //Tabla
        String[] columnNames = {"Nº", "ID", "CLIENTE", "DESCRIPCIÓN", "TIPO MUEBLE", "PRECIO", "ESTADO", "PAGO", "FECHA ENCARGO"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaEncargos = new JTable(tableModel);
        Estilos.aplicarEstiloTabla(tablaEncargos);

        //Agrandar celdas con contenido largo
        tablaEncargos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (column == 3 && value != null) {
                    String texto = value.toString();
                    if (texto.length() < 30) {
                        setToolTipText(texto);
                    }else {
                        setToolTipText(null);
                    }
                    }else {
                        setToolTipText(null);
                    }
                    return c;
                }
            });

        tablaEncargos.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = tablaEncargos.rowAtPoint(evt.getPoint());
                    int col = tablaEncargos.columnAtPoint(evt.getPoint());

                    if (row >= 0 && col == 3) {
                        String descripcion = (String) tableModel.getValueAt(row, col);
                        if (descripcion != null && !descripcion.trim().isEmpty()) {
                            JTextArea textArea = new JTextArea(descripcion);
                            textArea.setWrapStyleWord(true);
                            textArea.setLineWrap(true);
                            textArea.setEditable(false);
                            textArea.setBackground(UIManager.getColor("OptionPane.background"));

                            JScrollPane scrollPane = new JScrollPane(textArea);
                            scrollPane.setPreferredSize(new Dimension(400, 200));

                            JOptionPane.showMessageDialog(EncargosPanel.this, scrollPane, "Descripción completa del encargo", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });


        //Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelBotones.setBackground(Estilos.COLOR_FONDO);

        btnAgregar = new JButton("Agregar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnActualizar = new JButton("Actualizar");

        Estilos.aplicarEstiloBotonModerno(btnAgregar);
        Estilos.aplicarEstiloBotonModerno(btnEditar);
        Estilos.aplicarEstiloBotonModerno(btnEliminar);
        Estilos.aplicarEstiloBotonModerno(btnActualizar);

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);

        //Tabla scroll
        JScrollPane scrollPane = new JScrollPane(tablaEncargos);

        //Panel central con tabla y botones
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(Estilos.COLOR_FONDO);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        panelCentral.add(panelBotones, BorderLayout.SOUTH);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(panelCentral, BorderLayout.CENTER);

        //Eventos
        btnActualizar.addActionListener(e -> cargarEncargos());
        btnAgregar.addActionListener(e -> abrirFormularioAgregar());
        btnEditar.addActionListener(e -> abrirFormularioEditar());
        btnEliminar.addActionListener(e -> eliminarEncargo());
    }

    private void cargarEncargos() {
        tableModel.setRowCount(0);
        try {
            List<Encargo> encargos = encargoService.buscarTodos();
            int numero = 1;
            for (Encargo encargo : encargos) {
                Object[] rowData = {
                        numero++,
                        encargo.getId(),
                        encargo.getCliente() != null ? encargo.getCliente().getNombre() + " " + encargo.getCliente().getApellido1() : "N/A",
                        encargo.getDescripcion(),
                        encargo.getTipoMueble(),
                        String.format("€%.2f", encargo.getPrecio()),
                        encargo.getEstado(),
                        encargo.getEstadoPago(),
                        encargo.getFechaEncargo() != null ? encargo.getFechaEncargo().format(dateFormatter) : "N/A"
                };
                tableModel.addRow(rowData);
            }
            System.out.println("✓ " + encargos.size() + " encargos cargados");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar encargos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirFormularioAgregar() {
        EncargoFormDialog dialog = new EncargoFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null, encargoService, clienteService);
        dialog.setVisible(true);
        cargarEncargos();

        Encargo nuevoEncargo = dialog.getEncargo();
        if (nuevoEncargo != null) {
            System.out.println("Nuevo Encago creado: " + nuevoEncargo.getDescripcion());
            cargarEncargos();
        }
    }

    private void abrirFormularioEditar() {
        int selectedRow = tablaEncargos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un encargo para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long encargoId = (Long) tableModel.getValueAt(selectedRow, 1);
        try {
            Optional<Encargo> encargoOpt =encargoService.buscarPorId(encargoId);
            if (encargoOpt.isPresent()) {
            EncargoFormDialog dialog = new EncargoFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), encargoOpt.get(), encargoService, clienteService);
            dialog.setVisible(true);

            Encargo encargoActualizado = dialog.getEncargo();
            if (encargoActualizado != null) {
                cargarEncargos();
            }
            }else {
                JOptionPane.showMessageDialog(this, "Encargo no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar encargo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarEncargo() {
        int selectedRow = tablaEncargos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un encargo para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long encargoId = (Long) tableModel.getValueAt(selectedRow, 1);
        String descripcion = (String) tableModel.getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro que quieres eliminar el encargo: " + descripcion + "?\n" +
                "Esta accion no se puede deshacer.", "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        {
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    encargoService.eliminarEncargo(encargoId);
                    cargarEncargos();
                    JOptionPane.showMessageDialog(this, "Encargo eliminado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Error al eliminar encargo: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                cargarEncargos();
            }
        }
    }
}
