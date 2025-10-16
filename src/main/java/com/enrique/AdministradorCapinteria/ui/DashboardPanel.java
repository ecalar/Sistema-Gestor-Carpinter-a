package com.enrique.AdministradorCapinteria.ui;

import com.enrique.AdministradorCapinteria.domain.model.Encargo;
import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoEncargo;
import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoPago;
import com.enrique.AdministradorCapinteria.domain.ports.in.ClienteServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.in.EncargoServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.in.MaterialServicePort;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private final ClienteServicePort clienteService;
    private final EncargoServicePort encargoService;
    private final MaterialServicePort materialService;
    private JButton btnActualizar;

    public DashboardPanel(ClienteServicePort clienteService, EncargoServicePort encargoService, MaterialServicePort materialService) {
        this.clienteService = clienteService;
        this.encargoService = encargoService;
        this.materialService = materialService;
        initializeUI();
        cargarEstadisticas();
    }
    private void initializeUI() {
        setLayout(new BorderLayout());
        Estilos.aplicarEstiloPanelMOderno(this);

        //Titulo
        JLabel titulo = new JLabel("VISTA GENERAL", SwingConstants.CENTER);
        Estilos.aplicarEstiloTitulo(titulo);
        add(titulo, BorderLayout.NORTH);

        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titulo, BorderLayout.NORTH);

        //Panel de estadisticas
        JPanel panelStats = new JPanel(new GridLayout(2, 3, 15, 15));
        panelStats.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        //Crear paneles de estadísticas
        panelStats.add(crearPanelEstadistica("CLIENTES ACTIVOS", "0", Estilos.COLOR_PRIMARIO));
        panelStats.add(crearPanelEstadistica("ENCARGOS ACTIVOS", "0", Estilos.COLOR_EXITO));//Verde
        panelStats.add(crearPanelEstadistica("ENCARGOS TERMINADOS", "0", Estilos.COLOR_ADVERTENCIA));
        panelStats.add(crearPanelEstadistica("PENDIENTES DE PAGO", "0", Estilos.COLOR_PELIGRO));
        panelStats.add(crearPanelEstadistica("MATERIALES EN STOCK", "0", new Color(128, 0, 128)));//Morado
        panelStats.add(crearPanelEstadistica("STOCK BAJO", "0", new Color(230, 126, 34)));

        add(panelStats, BorderLayout.CENTER);

        //Boton actualizar
        btnActualizar = new JButton("Actualizar Estadísticas");
        Estilos.aplicarEstiloBotonModerno(btnActualizar);
        JPanel panelBoton = new JPanel(new FlowLayout());
        panelBoton.add(btnActualizar);
        add(panelBoton, BorderLayout.SOUTH);

        btnActualizar.addActionListener(e -> cargarEstadisticas());


    }
    private JPanel crearPanelEstadistica(String titulo, String valor, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(color, 2),BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.setBackground(new Color(240, 240, 240));

        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        lblTitulo.setForeground(color);

        JLabel lblValor = new JLabel(valor, SwingConstants.CENTER);
        lblValor.setFont(new Font("Arial", Font.BOLD, 24));
        lblValor.setForeground(color);

        panel.add(lblTitulo, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);

        return panel;
    }

    private void cargarEstadisticas() {
        try {
            //Clientes activos
            int clientesActivos = clienteService.buscarClientesActivos().size();
            //Encargos Activos(No entregados)
            List<Encargo> todosEncargos = encargoService.buscarTodos();
            int encargosActivos = (int) todosEncargos.stream().
                    filter(e -> e.getEstado() != EstadoEncargo.ENTREGADO).count();
            //Encargos terminados
            int encargosTerminados = (int) todosEncargos.stream()
                    .filter(e -> e.getEstado() == EstadoEncargo.TERMINADO)
                    .count();
            //Pendientes de pago
            int pendientesPago = (int) todosEncargos.stream()
                    .filter(e -> e.getEstadoPago() == EstadoPago.PENDIENTE)
                    .count();
            //Total materiales en stock
            int totalMateriales = materialService.buscarTodos().size();
            //Materiales con stock bajo(<10)
            int stockBajo = (int) materialService.buscarTodos().stream()
                    .filter(m -> m.getStock() < 10)
                    .count();
            //Actualizar la interfaz
            actualizarPanelEstadistica(0, String.valueOf(clientesActivos));
            actualizarPanelEstadistica(1, String.valueOf(encargosActivos));
            actualizarPanelEstadistica(2, String.valueOf(encargosTerminados));
            actualizarPanelEstadistica(3, String.valueOf(pendientesPago));
            actualizarPanelEstadistica(4, String.valueOf(totalMateriales));
            actualizarPanelEstadistica(5, String.valueOf(stockBajo));

            System.out.println("✓ Dashboard actualizado");
        }catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar estadísticas: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarPanelEstadistica(int indice, String valor) {
        JPanel panelStats = (JPanel) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        JPanel panelEstadistica = (JPanel) panelStats.getComponent(indice);
        JLabel lblValor = (JLabel) ((BorderLayout) panelEstadistica.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        lblValor.setText(valor);
    }
}
