package com.enrique.AdministradorCapinteria.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class Estilos {
    public static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    public static final Color COLOR_SECUNDARIO = new Color(52, 152, 219);
    public static final Color COLOR_EXITO = new Color(39, 174, 96);
    public static final Color COLOR_PELIGRO = new Color(231, 76, 60);
    public static final Color COLOR_ADVERTENCIA = new Color(243, 156, 18);
    public static final Color COLOR_FONDO = new Color(245, 245, 245);

    //Border
    public static Border BORDE_REDONDEADO = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
    );
    public static Border BORDE_SOMBRA = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
    );
    //Estilizar componentes
    public static void aplicarEstiloBotonModerno(JButton boton) {
        if (boton == null) return;

        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(COLOR_PRIMARIO);
        boton.setForeground(Color.WHITE);
        boton.setBorder(BORDE_REDONDEADO);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //Efecto hover
        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            private final Color colorOriginal = COLOR_PRIMARIO;
            private final Color colorHover = COLOR_SECUNDARIO;
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorHover);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorOriginal);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorHover.darker());
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                boton.setBackground(colorHover);
            }
        });
    }

    public static void aplicarEstiloBotonSecundario(JButton boton) {
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        boton.setBackground(Color.WHITE);
        boton.setForeground(COLOR_PRIMARIO);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_PRIMARIO, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    public static void aplicarEstiloPanelMOderno(JPanel panel) {
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }
    public static void aplicarEstiloTitulo(JLabel titulo) {
        titulo.setFont(new Font("Segoe UI",Font.BOLD, 20));
        titulo.setForeground(new Color(44, 62, 80));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
    }
    public static void aplicarEstiloSubtitulo(JLabel subtitulo) {
        subtitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtitulo.setForeground(new Color(127, 140, 141));
    }
    public static void aplicarEstiloTabla(JTable tabla) {
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setRowHeight(25);
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(240, 240, 240));
        tabla.setSelectionBackground(new Color(220, 240, 255));
        tabla.setSelectionForeground(Color.BLACK);

        //Header de tabla moderno
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(new Color(70, 130, 180));
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);
    }
    public static void aplicarEstiloTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }
    public static void aplicarEstiloComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                        BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
    }
    public static void aplicarEstiloBotonDeshabilitado(JButton boton) {
        if (boton == null) return;

        boton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        boton.setBackground(new Color(200, 200, 200));
        boton.setForeground(new Color(150, 150, 150));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        boton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }
}
