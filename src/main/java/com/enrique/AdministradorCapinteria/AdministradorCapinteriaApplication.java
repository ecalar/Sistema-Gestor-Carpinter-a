package com.enrique.AdministradorCapinteria;

import com.enrique.AdministradorCapinteria.domain.ports.in.ClienteServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.in.EncargoServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.in.MaterialServicePort;
import com.enrique.AdministradorCapinteria.ui.MainFrame;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class AdministradorCapinteriaApplication {

	public static void main(String[] args) {
		//Configurar Spring para desktop
		ConfigurableApplicationContext ctx = new SpringApplicationBuilder(AdministradorCapinteriaApplication.class).headless(false).web(WebApplicationType.NONE).run(args);

		//Obtener los services de Spring
		ClienteServicePort clienteService = ctx.getBean(ClienteServicePort.class);
		EncargoServicePort encargoService = ctx.getBean(EncargoServicePort.class);
		MaterialServicePort materialService = ctx.getBean(MaterialServicePort.class);

		//Lanzar interfaz
		SwingUtilities.invokeLater(() -> {
			MainFrame mainFrame = new MainFrame(clienteService, encargoService, materialService);
			mainFrame.setVisible(true);
			System.out.println("Interfaz Swing iniciada correctamente");
		});
		}
}
