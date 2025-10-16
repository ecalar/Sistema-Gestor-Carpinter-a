package com.enrique.AdministradorCapinteria.ui;

import com.enrique.AdministradorCapinteria.domain.model.Material;
import com.enrique.AdministradorCapinteria.domain.ports.in.MaterialServicePort;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MaterialesPanel extends JPanel {
    private final MaterialServicePort materialService;
    private JTable tablaMateriales;
    private JButton btnAgregar, btnEditar, btnEliminar, btnActualizar, btnBuscar;
    private DefaultTableModel tableModel;
    private JTextField txtBusqueda;

    public MaterialesPanel(MaterialServicePort materialService) {
        this.materialService = materialService;
        initializeUI();
        cargarMateriales();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        Estilos.aplicarEstiloPanelMOderno(this);

        //Titulo
        JLabel titulo = new JLabel("GESTIÓN DE INVENTARIO", SwingConstants.CENTER);
        Estilos.aplicarEstiloTitulo(titulo);
        add(titulo, BorderLayout.NORTH);

        //Panelde busqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBusqueda.setBackground(Estilos.COLOR_FONDO);
        panelBusqueda.add(new JLabel("Buscar:"));
        txtBusqueda = new JTextField(20);
        Estilos.aplicarEstiloTextField(txtBusqueda);
        panelBusqueda.add(txtBusqueda);
        btnBuscar = new JButton("Buscar");
        Estilos.aplicarEstiloBotonSecundario(btnBuscar);
        panelBusqueda.add(btnBuscar);

        //Panel botones superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Estilos.COLOR_FONDO);
        panelSuperior.add(panelBusqueda, BorderLayout.WEST);
        add(panelSuperior, BorderLayout.NORTH);

        //Panel botones
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

        panelSuperior.add(panelBotones,BorderLayout.CENTER);


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

        //Panel central con tabla y botones
        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(Estilos.COLOR_FONDO);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        panelCentral.add(panelBotones, BorderLayout.SOUTH);
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(panelCentral, BorderLayout.CENTER);

        //Eventos
        btnAgregar.addActionListener(e -> abrirFormularioAgregar());
        btnEditar.addActionListener(e -> abrirFormularioEditar());
        btnEliminar.addActionListener(e -> eliminarMaterial());
        btnActualizar.addActionListener(e -> cargarMateriales());
        btnBuscar.addActionListener(e -> buscarMateriales());
        txtBusqueda.addActionListener(e -> buscarMateriales());
    }

    private void cargarMateriales() {
        tableModel.setRowCount(0);
        try {
            List<Material> materiales = materialService.buscarTodos();
            cargarMaterialesEnTabla(materiales);
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar materiales: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirFormularioAgregar() {
        MaterialFormDialog dialog = new MaterialFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null, materialService);
        dialog.setVisible(true);

        Material nuevoMaterial = dialog.getMaterial();
        if (nuevoMaterial != null) {
            System.out.println("Nuevo Material: " + nuevoMaterial.getNombre());
            cargarMateriales();
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
                    cargarMateriales();
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
                cargarMateriales();
                JOptionPane.showMessageDialog(this, "Material eliminado correctamente", "Exito", JOptionPane.INFORMATION_MESSAGE);
            }catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar material: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void buscarMateriales() {
        String textoBusqueda = txtBusqueda.getText().trim().toLowerCase();

        if (textoBusqueda.isEmpty()) {
            cargarMateriales();
            return;
        }
        try {
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
                //Buscar Tipo
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
                        ||coincideId;
                if (coincideTotal) {
                    materialesFiltrados.add(material);
                }
            }
            cargarMaterialesEnTabla(materialesFiltrados);
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error en la búsqueda: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
