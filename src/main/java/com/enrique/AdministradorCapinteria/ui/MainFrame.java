package com.enrique.AdministradorCapinteria.ui;

import com.enrique.AdministradorCapinteria.domain.ports.in.ClienteServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.in.EncargoServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.in.MaterialServicePort;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private final ClienteServicePort clienteService;
    private final EncargoServicePort encargoService;
    private final MaterialServicePort materialService;
    private JTabbedPane tabbedPane;

    //Constructor que recibe los service
    public MainFrame(ClienteServicePort clienteService,
                     EncargoServicePort encargoService,
                     MaterialServicePort materialService) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        }catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
        this.clienteService = clienteService;
        this.encargoService = encargoService;
        this.materialService = materialService;
        initializeUI();
    }
    private void initializeUI() {
        setTitle("Administrador de Carpintería");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLocationRelativeTo(null);
        crearPestanias();
    }

    private void crearPestanias() {
        tabbedPane = new JTabbedPane();

        //Pestaña Dashboard
        DashboardPanel dashboardPanel = new DashboardPanel(clienteService, encargoService, materialService);
        tabbedPane.addTab("Inicio", dashboardPanel);

        //Pestaña calendario
        CalendarioPanel calendarioPanel = new CalendarioPanel(encargoService);
        tabbedPane.addTab("Calendario", calendarioPanel);

        //Pestaña Clientes
        ClientesPanel clientesPanel = new ClientesPanel(clienteService);
        tabbedPane.addTab("Clientes" , clientesPanel);

        //Pestaña Encargos
        EncargosPanel encargosPanel = new EncargosPanel(encargoService, clienteService);
        tabbedPane.addTab("Encargos", encargosPanel);

        //Pestaña Facturación
        BusquedaPanel busquedaPanel = new BusquedaPanel(encargoService);
        tabbedPane.addTab("Búsqueda", busquedaPanel);

        //Pestaña Materiales (temporal)
        MaterialesPanel materialesPanel = new MaterialesPanel(materialService);
        tabbedPane.addTab("Inventario", materialesPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}
