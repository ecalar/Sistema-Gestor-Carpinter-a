package com.enrique.AdministradorCapinteria.application;

import com.enrique.AdministradorCapinteria.domain.ports.in.ClienteServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.in.EncargoServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.in.MaterialServicePort;
import com.enrique.AdministradorCapinteria.domain.ports.out.ClienteRepositoryPort;
import com.enrique.AdministradorCapinteria.domain.ports.out.EncargoRepositoryPort;
import com.enrique.AdministradorCapinteria.domain.ports.out.MaterialRepositoryPort;
import com.enrique.AdministradorCapinteria.domain.service.ClienteService;
import com.enrique.AdministradorCapinteria.domain.service.EncargoService;
import com.enrique.AdministradorCapinteria.domain.service.MaterialService;
import org.springframework.context.annotation.Bean;

public class AppConfig {

    @Bean
    public ClienteServicePort clienteService(ClienteRepositoryPort clienteRepository) {
        return new ClienteService(clienteRepository);
    }

    @Bean
    public EncargoServicePort encargoService(EncargoRepositoryPort encargoRepository, ClienteRepositoryPort clienteRepository) {
        return new EncargoService(encargoRepository, clienteRepository);
    }

    @Bean
    public MaterialServicePort materialService(MaterialRepositoryPort materialRepository) {
        return new MaterialService(materialRepository);
    }
}
