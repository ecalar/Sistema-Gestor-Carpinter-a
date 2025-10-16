package com.enrique.AdministradorCapinteria.ui;

import com.enrique.AdministradorCapinteria.domain.model.Material;
import com.enrique.AdministradorCapinteria.domain.model.enums.TipoMaterial;
import com.enrique.AdministradorCapinteria.domain.model.enums.UnidadMedida;
import com.enrique.AdministradorCapinteria.domain.ports.in.MaterialServicePort;
import javax.swing.*;
import java.awt.*;


public class MaterialFormDialog extends JDialog{
    private JTextField txtNombre, txtStock, txtAncho, txtAlto, txtGrueso;
    private JComboBox<TipoMaterial> comboTipo;
    private JComboBox<UnidadMedida> comboUnidad;
    private JButton btnGuardar, btnCancelar;
    private Material material;
    private boolean guardado = false;
    private MaterialServicePort materialService;
    private JPanel panelMedidas;

    public MaterialFormDialog(JFrame parent, Material material, MaterialServicePort materialService) {
        super(parent, true);
        this.material = material;
        this.materialService = materialService;
        initializeUI();
    }
    private void initializeUI() {
        setTitle(material == null ? "Agregar Material" : "Editar Material");
        setSize(400, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelCampos = new JPanel(new GridLayout(4, 2, 10, 10));
        //Campos del formulario
        panelCampos.add(new JLabel("Nombre:"));
        txtNombre = new JTextField();
        panelCampos.add(txtNombre);

        panelCampos.add(new JLabel("Tipo:"));
        comboTipo = new JComboBox<>(TipoMaterial.values());
        panelCampos.add(comboTipo);

        panelCampos.add(new JLabel("Stock"));
        txtStock = new JTextField();
        panelCampos.add(txtStock);

        panelCampos.add(new JLabel("Unidad:"));
        comboUnidad = new JComboBox<>(UnidadMedida.values());
        comboUnidad.setEnabled(false);
        panelCampos.add(comboUnidad);

        //Panel medidas para MADERA
        panelMedidas = new JPanel(new GridLayout(3, 2, 5, 5));
        panelMedidas.setBorder(BorderFactory.createTitledBorder("Medidas de la madera (cm)"));

        panelMedidas.add(new JLabel("Ancho (cm):"));
        txtAncho = new JTextField();
        panelMedidas.add(txtAncho);

        panelMedidas.add(new JLabel("Alto (cm):"));
        txtAlto = new JTextField();
        panelMedidas.add(txtAlto);

        panelMedidas.add(new JLabel("Grosor (cm):"));
        txtGrueso = new JTextField();
        panelMedidas.add(txtGrueso);

        //Ocultar panel medidas al inicio
        panelMedidas.setVisible(false);

        //Botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton("Guardar");
        btnCancelar = new JButton("Cancelar");
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);

        panelPrincipal.add(panelCampos);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(panelMedidas);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));
        panelPrincipal.add(panelBotones);

        //Si se edita, cargar datos
        if (material != null) {
            cargarDatosMaterial();
        }

        //Cambiar unidad automaticamente al elegir tipo
        comboTipo.addActionListener(e -> actualizarUnidadPorTipo());
        actualizarUnidadPorTipo();
        //Eventos
        btnGuardar.addActionListener(e -> guardarMAterial());
        btnCancelar.addActionListener(e -> dispose());

        setLayout(new BorderLayout());
        add(panelPrincipal, BorderLayout.CENTER);
        add(panelBotones, BorderLayout.SOUTH);
    }
    private void cargarDatosMaterial() {
        txtNombre.setText(material.getNombre());
        comboTipo.setSelectedItem(material.getTipo());
        txtStock.setText(String.valueOf(material.getStock()));
        comboUnidad.setSelectedItem(material.getUnidadMedida());

        //Cargar medidas si existen
        if (material.getAnchoCm() != null) {
            txtAncho.setText(String.valueOf(material.getAnchoCm()));
        }
        if (material.getAltoCm() != null) {
            txtAlto.setText(String.valueOf(material.getAltoCm()));
        }
        if (material.getGruesoCm() != null) {
            txtGrueso.setText(String.valueOf(material.getGruesoCm()));
        }
        actualizarUnidadPorTipo();
    }
    private void guardarMaterial() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double stock = Double.parseDouble(txtStock.getText().trim());
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "El stock no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (material == null) {
                material = new Material();
            }
            material.setNombre(txtNombre.getText().trim());
            material.setTipo((TipoMaterial) comboTipo.getSelectedItem());
            material.setStock(stock);
            material.setUnidadMedida((UnidadMedida) comboUnidad.getSelectedItem());

            if (material.getTipo() == TipoMaterial.MADERA) {
                if (!txtAncho.getText().trim().isEmpty()) {
                    material.setAnchoCm(Double.parseDouble(txtAncho.getText().trim()));
                }
                if (!txtAlto.getText().trim().isEmpty()) {
                    material.setAltoCm(Double.parseDouble(txtAlto.getText().trim()));
                }
                if (!txtGrueso.getText().trim().isEmpty()) {
                    material.setGruesoCm(Double.parseDouble(txtGrueso.getText().trim()));
                }
            } else {
                material.setAnchoCm(null);
                material.setAltoCm(null);
                material.setGruesoCm(null);
            }
            if (material.getId() == null) {
                materialService.crearMaterial(material);
            }else{
                materialService.actualizarMaterial(material.getId(), material);
            }

            guardado = true;
            dispose();
        }catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Los campos numéricos deben contener valores válidos", "Error", JOptionPane.ERROR_MESSAGE);
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar material: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void guardarMAterial() {
        //Validaciones
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            double stock = Double.parseDouble(txtStock.getText().trim());
            if (stock < 0) {
                JOptionPane.showMessageDialog(this, "El stock no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Crear o actualizar material
            if (material == null) {
                material = new Material();
            }

            material.setNombre(txtNombre.getText().trim());
            material.setTipo((TipoMaterial) comboTipo.getSelectedItem());
            material.setStock(stock);
            material.setUnidadMedida((UnidadMedida) comboUnidad.getSelectedItem());

            //Guardar en BD
            if (material.getId() == null) {
                materialService.crearMaterial(material);
            }else {
                materialService.actualizarMaterial(material.getId(), material);
            }

            guardado = true;
            dispose();
        }catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El stock debe ser un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar material: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public Material getMaterial() {
        return guardado ? material : null;
    }
    private void actualizarUnidadPorTipo() {
        if (comboTipo.getSelectedItem() == null) return;
        TipoMaterial tipoSeleccionado = (TipoMaterial) comboTipo.getSelectedItem();

        //Asignar unidadMedida segun Tipo
        switch (tipoSeleccionado) {
            case MADERA:
                comboUnidad.setSelectedItem(UnidadMedida.CM);
                panelMedidas.setVisible(true);
                break;
            case PINTURA:
                comboUnidad.setSelectedItem(UnidadMedida.LITROS);
                panelMedidas.setVisible(false);
                break;
            case TORNILLERIA:
                comboUnidad.setSelectedItem(UnidadMedida.UNIDADES);
                panelMedidas.setVisible(false);
                break;
            case HERRAJE:
                comboUnidad.setSelectedItem(UnidadMedida.UNIDADES);
                panelMedidas.setVisible(false);
                break;
            case ADHESIVO:
                comboUnidad.setSelectedItem(UnidadMedida.LITROS);
                panelMedidas.setVisible(false);
                break;
            default:
                panelMedidas.setVisible(false);
        }
        panelMedidas.revalidate();
        panelMedidas.repaint();
        getContentPane().revalidate();
        getContentPane().repaint();
    }

}
