package com.enrique.AdministradorCapinteria.ui;

import com.enrique.AdministradorCapinteria.domain.model.Encargo;
import com.enrique.AdministradorCapinteria.domain.ports.in.EncargoServicePort;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class BusquedaPanel extends JPanel {
    private final EncargoServicePort encargoService;
    private JTable tablaFacturacion;
    private JButton btnMarcarPagado, btnActualizar, btnBuscar;
    private DefaultTableModel tableModel;
    private JTextField txtBusqueda;

    public BusquedaPanel(EncargoServicePort encargoService) {
        this.encargoService = encargoService;
        initializeUI();
        iniciarCargaTodosLosEncargos();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        Estilos.aplicarEstiloPanelMOderno(this);

        //Titulo
        JLabel titulo = new JLabel("BÚSQUEDA Y GESTIÓN DE ENCARGOS", SwingConstants.CENTER);
        Estilos.aplicarEstiloTitulo(titulo);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        //Tabla busqueda
        String[] columNames = {"Nº", "ID", "CLIENTE", "DESCRIPCIÓN", "PRECIO", "ESTADO ENCARGO", "ESTADO PAGO", "FECHA ENCARGO", "FECHA PAGO"};
        tableModel = new DefaultTableModel(columNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaFacturacion = new JTable(tableModel);
        Estilos.aplicarEstiloTabla(tablaFacturacion);

        //ToopLip para descripciones largas
        tablaFacturacion.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 3 && value != null) {
                    String texto = value.toString();
                    setToolTipText(texto.length() > 30 ? texto : null);
                } else {
                    setToolTipText(null);
                }
                return c;
            }
        });
        tablaFacturacion.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = tablaFacturacion.rowAtPoint(evt.getPoint());
                    int col = tablaFacturacion.columnAtPoint(evt.getPoint());

                    if (row >= 0 && col == 3) {
                        String descripcion = (String) tableModel.getValueAt(row, col);
                        if (descripcion != null) {
                            JTextArea textArea = new JTextArea(descripcion);
                            textArea.setWrapStyleWord(true);
                            textArea.setLineWrap(true);
                            textArea.setEditable(false);
                            textArea.setBackground(UIManager.getColor("OptionPane.background"));

                            JScrollPane scrollPane = new JScrollPane(textArea);
                            scrollPane.setPreferredSize(new Dimension(400, 200));

                            JOptionPane.showMessageDialog(BusquedaPanel.this, scrollPane, "Descripción Completa del Encargo", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        });

        //Panel tabla scroll
        JScrollPane scrollPane = new JScrollPane(tablaFacturacion);
        add(scrollPane, BorderLayout.CENTER);

        //Panel controles
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.setBackground(Estilos.COLOR_FONDO);

        //Panel de busqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelBusqueda.setBackground(Estilos.COLOR_FONDO);
        panelBusqueda.add(new JLabel("Buscar:"));
        txtBusqueda = new JTextField(25);
        Estilos.aplicarEstiloTextField(txtBusqueda);
        panelBusqueda.add(txtBusqueda);
        btnBuscar = new JButton("Buscar");
        Estilos.aplicarEstiloBotonSecundario(btnBuscar);
        panelBusqueda.add(btnBuscar);
        Estilos.aplicarEstiloBotonModerno(btnBuscar);

        //Boton de ayuda
        JButton btnAyuda = new JButton("?");
        btnAyuda.setToolTipText("Mostrar ayuda de búsqueda");
        panelBusqueda.add(btnAyuda);
        Estilos.aplicarEstiloBotonModerno(btnAyuda);

        //Panel botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelBotones.setBackground(Estilos.COLOR_FONDO);
        btnMarcarPagado = new JButton("Marcar como Pagado");
        btnActualizar = new JButton("Actualizar");

        Estilos.aplicarEstiloBotonModerno(btnMarcarPagado);
        Estilos.aplicarEstiloBotonSecundario(btnActualizar);

        panelBotones.add(btnMarcarPagado);
        panelBotones.add(btnActualizar);

        Estilos.aplicarEstiloBotonModerno(btnMarcarPagado);
        Estilos.aplicarEstiloBotonModerno(btnActualizar);

        panelInferior.add(panelBusqueda, BorderLayout.WEST);
        panelInferior.add(panelBotones, BorderLayout.EAST);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(panelInferior, BorderLayout.SOUTH);

        //Eventos
        btnActualizar.addActionListener(e -> iniciarCargaTodosLosEncargos());
        btnMarcarPagado.addActionListener(e -> marcarComoPagado());
        btnBuscar.addActionListener(e -> iniciarBusquedaEncargos());
        txtBusqueda.addActionListener(e -> iniciarBusquedaEncargos());//Buscar al pulsar Enter
        btnAyuda.addActionListener(e -> mostrarAyudaBusqueda());
    }

    private void setControlesEnabled(boolean enabled) {
        btnBuscar.setEnabled(enabled);
        btnActualizar.setEnabled(enabled);
        btnMarcarPagado.setEnabled(enabled);
        txtBusqueda.setEnabled(enabled);
    }
    private void iniciarCargaTodosLosEncargos() {
        setControlesEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<List<Encargo>, Void> worker = new SwingWorker<List<Encargo>, Void>() {
            @Override
            protected List<Encargo> doInBackground() throws Exception {
                return encargoService.buscarTodos();
            }
            @Override
            protected void done() {
                try {
                    List<Encargo> encargos = get();
                    cargarEncargosEnTabla(encargos);
                }catch (InterruptedException | ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(BusquedaPanel.this, "Error al cargar encargos: " + cause.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }finally {
                    setControlesEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }
    private void cargarEncargosEnTabla(List<Encargo> encargos) {
        tableModel.setRowCount(0);
        try {
            int numero = 1;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (Encargo encargo : encargos) {
                Object[] rowData = {
                        numero++,
                        encargo.getId(),
                        encargo.getCliente() != null ? encargo.getCliente().getNombre() + " " + encargo.getCliente().getApellido1() : "N/A",
                        encargo.getDescripcion(),
                        String.format("€%.2f", encargo.getPrecio()),
                        encargo.getEstado(),
                        encargo.getEstadoPago(),
                        encargo.getFechaEncargo() != null ? encargo.getFechaEncargo().format(dateFormatter) : "N/A",
                        encargo.getFechaPago() != null ? encargo.getFechaPago().format(dateFormatter) : "Pendiente"
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar encargos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void iniciarBusquedaEncargos() {
        String busqueda = txtBusqueda.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            iniciarCargaTodosLosEncargos();
            return;
        }
        setControlesEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<List<Encargo>, Void> worker = new SwingWorker<List<Encargo>, Void>() {
            @Override
            protected List<Encargo> doInBackground() throws Exception {
                List<Encargo> todosEncargos = encargoService.buscarTodos();
                List<Encargo> encargosFiltrados = new ArrayList<>();

                for (Encargo encargo : todosEncargos) {
                    boolean coincideCliente = false;
                    boolean coincideDescripcion = false;
                    boolean coincideTipoMueble = false;
                    boolean coincideTipoMadera = false;
                    boolean coincideEstado = false;
                    boolean coincideEstadoPago = false;
                    boolean coincideId;


                    if (encargo.getCliente() != null) {
                        String nombreCliente = encargo.getCliente().getNombre().toLowerCase();
                        String apellido1Cliente = encargo.getCliente().getApellido1().toLowerCase();
                        String apellido2Cliente = encargo.getCliente().getApellido2() != null ? encargo.getCliente().getApellido2().toLowerCase() : "";
                        coincideCliente = nombreCliente.contains(busqueda)
                                || apellido1Cliente.contains(busqueda)
                                || apellido2Cliente.contains(busqueda);
                    }
                    if (encargo.getDescripcion() != null) {
                        coincideDescripcion = encargo.getDescripcion().toLowerCase().contains(busqueda);
                    }
                    if (encargo.getTipoMueble() != null) {
                        coincideTipoMueble = encargo.getTipoMueble().toLowerCase().contains(busqueda);
                    }
                    if (encargo.getTipoMadera() != null) {
                        coincideTipoMadera = encargo.getTipoMadera().toLowerCase().contains(busqueda);
                    }
                    if (encargo.getEstado() != null) {
                        coincideEstado = encargo.getEstado().toString().toLowerCase().contains(busqueda);
                    }
                    if (encargo.getEstadoPago() != null) {
                        coincideEstadoPago = encargo.getEstadoPago().toString().toLowerCase().contains(busqueda);
                    }

                    coincideId = String.valueOf(encargo.getId()).contains(busqueda);
                    boolean coincideTotal = coincideCliente
                            || coincideDescripcion
                            || coincideTipoMueble
                            || coincideTipoMadera
                            || coincideEstado
                            || coincideEstadoPago
                            || coincideId;
                    if (coincideTotal) {
                        encargosFiltrados.add(encargo);
                    }
                }
                return encargosFiltrados;
            }

            @Override
            protected void done() {
                try {
                    List<Encargo> encargosFiltrados = get();
                    cargarEncargosEnTabla(encargosFiltrados);
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    JOptionPane.showMessageDialog(BusquedaPanel.this, "Error en la búsqueda: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    private void marcarComoPagado() {
        int selectedRow = tablaFacturacion.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un encargo para marcar como pagado", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long encargoId = (Long) tableModel.getValueAt(selectedRow, 1);
        String descripcion = (String) tableModel.getValueAt(selectedRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this, "¿Marcar como PAGADO en encargo:\n" + descripcion + "?", "Confirmar Pago", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
        iniciarMarcarComoPagado(encargoId);

        }
    }
    private void iniciarMarcarComoPagado(Long encargoId) {
        setControlesEnabled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                encargoService.marcarComoPagado(encargoId, LocalDate.now());
                return null;
            }
            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(BusquedaPanel.this, "Encargo marcardo como PAGADO", "EXITO", JOptionPane.INFORMATION_MESSAGE);
                    iniciarCargaTodosLosEncargos();
                }catch (InterruptedException | ExecutionException e) {
                    Throwable cause = e.getCause() != null ? e.getCause(): e;
                    JOptionPane.showMessageDialog(BusquedaPanel.this, "Error al marcar como PAGADO: " + cause.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }finally {
                    setControlesEnabled(true);
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }
    private void mostrarAyudaBusqueda() {
        String mensajeAyuda =
                "<html><div style='width: 400px;'>" +
                        "<h3>Guía de Búsqueda</h3>" +
                        "<b>Puedes buscar por:</br><br>" +
                        "· <b>Nombre/Apellidos/</b> del Cliente (ej: 'Antonio', 'García')<br>" +
                        "· <b>Descripción</b> del encargo<br>" +
                        "· <b>Tipo de Mueble</b> (ej: 'mesa', 'estantería')<br>" +
                        "· <b>Tipo de Madera</b> (ej: 'roble', 'pino')<br>" +
                        "· <b>Estado del Encargo</b>:<br>" +
                        "&nbsp;&nbsp;- PRESUPUESTADO, ACEPTADO, FABRICACION- TERMINADO, ENTREGADO<br>" +
                        "· <b>Estado de Pago</b>:<br>" +
                        "&nbsp;&nbsp;- PENDIENTE, PAGADO<br>" +
                        "· <b>ID</b> del Encargo (ej: '5')<br><br>" +
                        "<b>Ejemplos prácticos:</b><br>" +
                        "• 'Antonio' → Todos los encargos de clientes llamados Antonio<br>" +
                        "• 'mesa roble' → Encargos de mesas de roble<br>" +
                        "• 'terminado pendiente' → Encargos terminados con pago pendiente<br>" +
                        "• '5' → Encargo con ID 5</div></html>";

        JOptionPane.showMessageDialog(this, mensajeAyuda, "Ayuda de Búsqueda", JOptionPane.INFORMATION_MESSAGE);

    }
}

