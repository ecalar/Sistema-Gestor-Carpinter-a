package com.enrique.AdministradorCapinteria.ui;

import com.enrique.AdministradorCapinteria.domain.model.Encargo;
import com.enrique.AdministradorCapinteria.domain.model.enums.EstadoEncargo;
import com.enrique.AdministradorCapinteria.domain.ports.in.EncargoServicePort;
import lombok.extern.slf4j.Slf4j;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
public class CalendarioPanel extends JPanel {
    private final EncargoServicePort encargoService;
    private JPanel panelCalendario;
    private JLabel lblMesAnio;
    private YearMonth mesActual;
    private JButton btnAnterior, btnSiguiente, btnHoy;

    private static class CalendarioData {
        final List<Encargo> encargos;
        final long totalAtrasados;
        CalendarioData(List<Encargo> encargos, long totalAtrasados) {
            this.encargos = encargos;
            this.totalAtrasados = totalAtrasados;
        }
    }
    public CalendarioPanel(EncargoServicePort encargoService) {
        this.encargoService = encargoService;
        this.mesActual = YearMonth.now();
        initializeUI();
        iniciarActualizacionCalendario();
    }
    private void initializeUI() {
        setLayout(new BorderLayout());
        Estilos.aplicarEstiloPanelMOderno(this);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Estilos.COLOR_FONDO);

        //Titulo
        JLabel titulo = new JLabel("CALENDARIO DE ENTREGAS", SwingConstants.CENTER);
        Estilos.aplicarEstiloTitulo(titulo);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        panelSuperior.add(titulo, BorderLayout.NORTH);

        //Panel controles (Mes y Año)
        JPanel panelControles = new JPanel(new BorderLayout());
        panelControles.setBackground(Estilos.COLOR_FONDO);
        panelControles.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        //Botones de navegación
        JPanel panelNavegacion = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelNavegacion.setBackground(Estilos.COLOR_FONDO);
        btnAnterior = new JButton("<-Anterior");
        btnHoy = new JButton("Hoy");
        btnSiguiente = new JButton("Siguiente->");

        panelNavegacion.add(btnAnterior);
        panelNavegacion.add(btnHoy);
        panelNavegacion.add(btnSiguiente);

        Estilos.aplicarEstiloBotonModerno(btnAnterior);
        Estilos.aplicarEstiloBotonModerno(btnHoy);
        Estilos.aplicarEstiloBotonModerno(btnSiguiente);

        //Label del mes y año
        lblMesAnio = new JLabel("", SwingConstants.CENTER);
        lblMesAnio.setFont(new Font("Arial", Font.BOLD, 16));
        lblMesAnio.setForeground(Estilos.COLOR_PRIMARIO);

        panelControles.add(panelNavegacion, BorderLayout.WEST);
        panelControles.add(lblMesAnio, BorderLayout.CENTER);

        panelSuperior.add(panelControles, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);

        //Panel calendario
        panelCalendario = new JPanel(new GridLayout(0, 7, 2, 2));
        panelCalendario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelCalendario.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(panelCalendario);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);

        //Eventos
        btnAnterior.addActionListener(e -> {
            mesActual = mesActual.minusMonths(1);
            iniciarActualizacionCalendario();
        });
        btnSiguiente.addActionListener(e -> {
            mesActual = mesActual.plusMonths(1);
            iniciarActualizacionCalendario();
        });
        btnHoy.addActionListener(e -> {
            mesActual = YearMonth.now();
            iniciarActualizacionCalendario();
        });
    }

    private void setControlesEnabled(boolean enabled) {
        btnHoy.setEnabled(enabled);
        btnSiguiente.setEnabled(enabled);
        btnAnterior.setEnabled(enabled);
    }
    private void iniciarActualizacionCalendario() {
        setControlesEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

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
                SwingWorker<CalendarioData, Void> worker = new SwingWorker<CalendarioData, Void>() {
                    @Override
                    protected CalendarioData doInBackground() throws Exception {
                        List<Encargo> encargos = encargoService.buscarTodos();
                        long totalAtrasados = encargos.stream()
                                .filter(e -> e.getFechaEntrega() != null && e.getFechaEntrega().isBefore(LocalDate.now()) && e.getEstado() != EstadoEncargo.ENTREGADO)
                                .count();
                        return new CalendarioData(encargos, totalAtrasados);
                    }
                    @Override
                    protected void done() {
                        try {
                            CalendarioData data = get();
                            LocalDate primerDia = mesActual.atDay(1);
                            int diaSemanaInicio = primerDia.getDayOfWeek().getValue() - 1;
                            for (int i = 0; i < diaSemanaInicio; i++) {
                                panelCalendario.add(new JLabel(""));
                            }
                            for (int dia = 1; dia <= mesActual.lengthOfMonth(); dia++) {
                                LocalDate fecha = mesActual.atDay(dia);
                                JPanel panelDia = crearPanelDia(fecha, dia, data.encargos, data.totalAtrasados);
                                panelCalendario.add(panelDia);
                            }
                            panelCalendario.revalidate();
                            panelCalendario.repaint();
                        }catch (InterruptedException | ExecutionException e) {
                            Throwable cause = e.getCause() != null ? e.getCause() : e;
                            JOptionPane.showMessageDialog(CalendarioPanel.this, "Error al actualizar calendario;" + cause.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }finally {
                            setControlesEnabled(true);
                            setCursor(Cursor.getDefaultCursor());
                        }
                    }
                };
                worker.execute();
    }
    private JPanel crearPanelDia(LocalDate fecha, int dia, List<Encargo>  encargos, long atrasados) {
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

        if (encargosHoy > 0) {
            JLabel lblEncargos = new JLabel("Encargos: " + encargosHoy);
            lblEncargos.setFont(new Font("Arial", Font.PLAIN, 10));
            lblEncargos.setForeground(new Color(0, 100, 0));
            panelEncargos.add(lblEncargos);
        }
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
            StringBuilder tooltip = new StringBuilder("<html>Entregas para " + fecha + ":<br>");
            encargos.stream().filter(e -> e.getFechaEntrega() != null && e.getFechaEntrega().equals(fecha))
                    .forEach(e -> tooltip.append("- ").append(e.getDescripcion()).append("<br>"));
            tooltip.append("</html>");
            panelDia.setToolTipText(tooltip.toString());
        }
        return panelDia;
    }
}
