package com.enrique.AdministradorCapinteria.ui;

import com.enrique.AdministradorCapinteria.domain.model.Encargo;
import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoEncargo;
import com.enrique.AdministradorCapinteria.domain.ports.in.EncargoServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.Local;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Slf4j
public class CalendarioPanel extends JPanel {
    private final EncargoServicePort encargoService;
    private JPanel panelCalendario;
    private JLabel lblMesAnio;
    private YearMonth mesActual;
    private JButton btnAnterior, btnSiguiente, btnHoy;

    public CalendarioPanel(EncargoServicePort encargoService) {
        this.encargoService = encargoService;
        this.mesActual = YearMonth.now();
        initializeUI();
        actualizarCalendario();
    }
    private void initializeUI() {
        setLayout(new BorderLayout());
        Estilos.aplicarEstiloPanelMOderno(this);

        //Titulo
        JLabel titulo = new JLabel("CALENDARIO DE ENTREGAS", SwingConstants.CENTER);
        Estilos.aplicarEstiloTitulo(titulo);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        //Panel controles (Mes y Año)
        JPanel panelControles = new JPanel(new BorderLayout());
        panelControles.setBackground(Estilos.COLOR_FONDO);
        panelControles.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Botones de navegación
        JPanel panelNavegacion = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelNavegacion.setBackground(Estilos.COLOR_FONDO);
        btnAnterior = new JButton("<-Anterior");
        btnHoy = new JButton("Hoy");
        btnSiguiente = new JButton("Siguiente->");

        Estilos.aplicarEstiloBotonSecundario(btnAnterior);
        Estilos.aplicarEstiloBotonModerno(btnHoy);
        Estilos.aplicarEstiloBotonSecundario(btnSiguiente);

        panelNavegacion.add(btnAnterior);
        panelNavegacion.add(btnHoy);
        panelNavegacion.add(btnSiguiente);

        //Label del mes y año
        lblMesAnio = new JLabel("", SwingConstants.CENTER);
        lblMesAnio.setFont(new Font("Arial", Font.BOLD, 16));
        lblMesAnio.setForeground(Estilos.COLOR_PRIMARIO);

        panelControles.add(panelNavegacion, BorderLayout.WEST);
        panelControles.add(lblMesAnio, BorderLayout.CENTER);
        add(panelControles, BorderLayout.NORTH);

        //Panel calendario
        panelCalendario = new JPanel(new GridLayout(0, 7, 2, 2));
        panelCalendario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(panelCalendario);
        add(scrollPane, BorderLayout.CENTER);

        //Eventos
        btnAnterior.addActionListener(e -> {
            mesActual = mesActual.minusMonths(1);
            actualizarCalendario();
        });
        btnSiguiente.addActionListener(e -> {
            mesActual = mesActual.plusMonths(1);
            actualizarCalendario();
        });
        btnHoy.addActionListener(e -> {
            mesActual = YearMonth.now();
            actualizarCalendario();
        });
    }
    private void actualizarCalendario() {
        String[] meses = {"ENERO", "FEBRERO", "MARZO", "ABRIL", "MAYO", "JUNIO", "JULIO", "AGOSTO", "SEPTIEMBRE", "OCTUBRE", "NOVIEMBRE", "DICIEMBRE"};
        lblMesAnio.setText(meses[mesActual.getMonthValue() - 1]+" "+mesActual.getYear());

        //Limpiar calendario
        panelCalendario.removeAll();

        //Agregar headers dias de la semana
        String[] diaSemana = {"LUN", "MAR", "MIE", "JUE", "VIE", "SAB", "DOM"};
                for (String dia : diaSemana) {
                    JLabel lblDia = new JLabel(dia, SwingConstants.CENTER);
                    lblDia.setFont(new Font("Arial", Font.BOLD, 12));
                    lblDia.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                    lblDia.setBackground(new Color(220, 220, 220));
                    lblDia.setOpaque(true);
                    panelCalendario.add(lblDia);
                }
                //Obtener encargos para mes actual
        List<Encargo> encargos = encargoService.buscarTodos();
        LocalDate primerDia = mesActual.atDay(1);
        LocalDate ultimoDia = mesActual.atEndOfMonth();
        //Espacios en blanco para dias anteriores al primer dia del mes
        int diaSemanaInicio = primerDia.getDayOfWeek().getValue() - 1;
        for (int i = 0; i < diaSemanaInicio; i++) {
            panelCalendario.add(new JLabel(""));
        }
        //Dias del mes
        for (int dia = 1; dia <= mesActual.lengthOfMonth(); dia++) {
            LocalDate fecha = mesActual.atDay(dia);
            JPanel panelDia = crearPanelDia(fecha, dia, encargos);
            panelCalendario.add(panelDia);
        }

        //Actualizar interfaz
        panelCalendario.revalidate();
        panelCalendario.repaint();
    }
    private JPanel crearPanelDia(LocalDate fecha, int dia, List<Encargo>  encargos) {
        JPanel panelDia = new JPanel(new BorderLayout());
        panelDia.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        panelDia.setBackground(Color.WHITE);

        //Label del numero del dia
        JLabel lblNumero = new JLabel(String.valueOf(dia), SwingConstants.CENTER);
        lblNumero.setFont(new Font("Arial", Font.PLAIN, 12));

        //Verificar si es hoy
        if (fecha.equals(LocalDate.now())) {
            panelDia.setBackground(new Color(220, 240, 255));
            lblNumero.setFont(new Font("Arial", Font.BOLD, 14));
        }
        //Contar encargos para el dia
        Long encargosHoy = encargos.stream()
                .filter(e -> e.getFechaEntrega() != null && e.getFechaEntrega().equals(fecha))
                .count();

        //Panel para los encargos
        JPanel panelEncargos = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 1));
        panelEncargos.setOpaque(false);
        if (encargosHoy > 0) {
            JLabel lblEncargos = new JLabel("Encargos" + encargosHoy);
            lblEncargos.setFont(new Font("Arial", Font.PLAIN, 10));
            lblEncargos.setForeground(Color.RED);
            panelEncargos.add(lblEncargos);
        }

        //Verificar encargos atrasados (fecha pasada y no entregado)
        long atrasados = encargos.stream()
                .filter(e -> e.getFechaEntrega() != null && e.getFechaEntrega().isBefore(LocalDate.now()) && e.getEstado() != EstadoEncargo.ENTREGADO)
                .count();
        if (atrasados > 0 && fecha.equals(LocalDate.now())) {
            JLabel lblAtrasados = new JLabel("Atrasados" + atrasados);
            lblAtrasados.setFont(new Font("Arial", Font.PLAIN, 10));
            lblAtrasados.setForeground(Color.RED);
            panelEncargos.add(lblAtrasados);
        }

        panelDia.add(lblNumero, BorderLayout.NORTH);
        panelDia.add(panelEncargos, BorderLayout.CENTER);

        //Tooltip con detalles
        if (encargosHoy > 0) {
            StringBuilder tooltip = new StringBuilder("Entregas para " + fecha + ":\n");
            encargos.stream().filter(e -> e.getFechaEntrega() != null && e.getFechaEntrega().equals(fecha))
                    .forEach(e -> tooltip.append("- ").append(e.getDescripcion()).append("\n"));
            panelDia.setToolTipText(tooltip.toString());
        }
        return panelDia;
    }
}
