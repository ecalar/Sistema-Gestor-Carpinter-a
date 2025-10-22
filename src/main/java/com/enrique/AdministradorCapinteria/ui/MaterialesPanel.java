package com.enrique.AdministradorCapinteria.ui;

import com.enrique.AdministradorCapinteria.domain.model.Material;
import com.enrique.AdministradorCapinteria.domain.ports.in.MaterialServicePort;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class MaterialesPanel extends JPanel {
    private final MaterialServicePort materialService;
    private JTable tablaMateriales;
    private JButton btnAgregar, btnEditar, btnEliminar, btnActualizar, btnBuscar;
    private DefaultTableModel tableModel;
    private JTextField txtBusqueda;

    public MaterialesPanel(MaterialServicePort materialService) {
        this.materialService = materialService;
        initializeUI();
        iniciarCargaMateriales();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        Estilos.aplicarEstiloPanelMOderno(this);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Estilos.COLOR_FONDO);

        //Titulo
        JLabel titulo = new JLabel("GESTIÓN DE INVENTARIO", SwingConstants.CENTER);
        Estilos.aplicarEstiloTitulo(titulo);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        panelSuperior.add(titulo, BorderLayout.NORTH);

        JPanel panelControles = new JPanel(new BorderLayout());
        panelControles.setBackground(Estilos.COLOR_FONDO);
        panelControles.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        //Panelde busqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBusqueda.setBackground(Estilos.COLOR_FONDO);
        panelBusqueda.add(new JLabel("Buscar:"));
        txtBusqueda = new JTextField(20);
        Estilos.aplicarEstiloTextField(txtBusqueda);
        panelBusqueda.add(txtBusqueda);
        btnBuscar = new JButton("Buscar");
        Estilos.aplicarEstiloBotonModerno(btnBuscar);
        panelBusqueda.add(btnBuscar);

        //Panel botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
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

        panelControles.add(panelBusqueda, BorderLayout.WEST);
        panelControles.add(panelBotones, BorderLayout.EAST);

        //Panel botones superior
        panelSuperior.add(panelControles, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        //Crear Tabla
        String[] columnNames = {"Nº", "ID", "NOMBRE", "TIPO", "STOCK", "UNIDAD"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaMateriales = new JTable(tableModel);
        Estilos.aplicarEstiloTabla(tablaMateriales);

        //Panel tabla scroll
        JScrollPane scrollPane = new JScrollPane(tablaMateriales);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(scrollPane, BorderLayout.CENTER);

        //Eventos
        btnAgregar.addActionListener(e -> abrirFormularioAgregar());
        btnEditar.addActionListener(e -> abrirFormularioEditar());
        btnEliminar.addActionListener(e -> eliminarMaterial());
        btnActualizar.addActionListener(e -> iniciarCargaMateriales());
        btnBuscar.addActionListener(e -> iniciarBusquedaMateriales());
        txtBusqueda.addActionListener(e -> iniciarBusquedaMateriales());
    }

    //Metodo para habilitar/deshabilitar coontroles
    private void setControlesEnabled(boolean enabled) {
        btnAgregar.setEnabled(enabled);
        btnEditar.setEnabled(enabled);
        btnEliminar.setEnabled(enabled);
        btnActualizar.setEnabled(enabled);
        btnBuscar.setEnabled(enabled);
        txtBusqueda.setEnabled(enabled);
    }
    private void iniciarCargaMateriales() {
        setControlesEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tableModel.setRowCount(0);

        SwingWorker<List<Material>, Void> worker = new SwingWorker<List<Material>, Void>() {
            @Override
            protected List<Material> doInBackground() throws Exception {
                return materialService.buscarTodos();
            }
            @Override
            protected void done () {
                try {
                    List<Material> materials = get();
                    cargarMaterialesEnTabla(materials);
                }catch (InterruptedException | ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(MaterialesPanel.this, "Error al cargar materiales " + cause.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }finally {
                    setControlesEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void abrirFormularioAgregar() {
        MaterialFormDialog dialog = new MaterialFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null, materialService);
        dialog.setVisible(true);

        Material nuevoMaterial = dialog.getMaterial();
        if (nuevoMaterial != null) {
            System.out.println("Nuevo Material: " + nuevoMaterial.getNombre());
            iniciarCargaMateriales();
        }
    }

    private void abrirFormularioEditar() {
        int selectedRow = tablaMateriales.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,"Por favor, selecciona un material para editar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long materialId = (Long) tableModel.getValueAt(selectedRow, 1);
        try {
            Optional<Material> materialOpt = materialService.buscarPorId(materialId);
            if (materialOpt.isPresent()) {
                MaterialFormDialog dialog = new MaterialFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), materialOpt.get(), materialService);
                dialog.setVisible(true);

                Material materialActualizado = dialog.getMaterial();
                if (materialActualizado != null) {
                    iniciarCargaMateriales();
                }
            }else {
                JOptionPane.showMessageDialog(this, "Material no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar material: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarMaterial() {
        int selectedRow = tablaMateriales.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un material para eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Long materialId = (Long) tableModel.getValueAt(selectedRow, 1);
        String nombreMaterial = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Estás seguro de que quieres eliminar el material: " + nombreMaterial + "?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                materialService.eliminarMaterial(materialId);
                iniciarCargaMateriales();
                JOptionPane.showMessageDialog(this, "Material eliminado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            }catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar material: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void iniciarBusquedaMateriales() {
        String textoBusqueda = txtBusqueda.getText().trim().toLowerCase();

        if (textoBusqueda.isEmpty()) {
            iniciarCargaMateriales();
            return;
        }

        setControlesEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        tableModel.setRowCount(0);

        SwingWorker<List<Material>, Void> worker = new SwingWorker<List<Material>, Void>() {
            @Override
            protected List<Material> doInBackground() throws Exception {
                List<Material> todosMateriales = materialService.buscarTodos();
                List<Material> materialesFiltrados = new ArrayList<>();

                for (Material material : todosMateriales) {
                    boolean coincideNombre = false;
                    boolean coincideTipo = false;
                    boolean coincideUnidad = false;
                    boolean coincideId = false;

                    //Buscar en nombre
                    if (material.getNombre() != null) {
                        coincideNombre = material.getNombre().toLowerCase().contains(textoBusqueda);
                    }
                    //Buscar tipo
                    if (material.getTipo() != null) {
                        coincideTipo = material.getTipo().toString().toLowerCase().contains(textoBusqueda);
                    }
                    //Buscar unidad medida
                    if (material.getUnidadMedida() != null) {
                        coincideUnidad = material.getUnidadMedida().toString().toLowerCase().contains(textoBusqueda);
                    }
                    //Buscar Id
                    coincideId = String.valueOf(material.getId()).contains(textoBusqueda);
                    //Coincide si cumple uno de los criterios
                    boolean coincideTotal = coincideNombre
                            || coincideTipo
                            || coincideUnidad
                            || coincideId;
                    if (coincideTotal) {
                        materialesFiltrados.add(material);
                    }
                }
                return materialesFiltrados;
            }

            @Override
            protected void done() {
                try {
                    List<Material> materialesFiltrados = get();
                    cargarMaterialesEnTabla(materialesFiltrados);
                } catch (InterruptedException | ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(MaterialesPanel.this, "Error en la búsqueda: " + cause.getMessage() + "Error" + JOptionPane.ERROR_MESSAGE);
                } finally {
                    setControlesEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void cargarMaterialesEnTabla(List<Material> materiales) {
        tableModel.setRowCount(0);
        try {
            int numero = 1;
            for (Material material : materiales) {
                Object[] rowData = {
                        numero++,
                        material.getId(),
                        material.getNombre(),
                        material.getTipo(),
                        material.getStock(),
                        material.getUnidadMedida()
                };
                tableModel.addRow(rowData);
            }
            System.out.println("✓ " + materiales.size() + "materiales mostrados");
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar materiales: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
}
