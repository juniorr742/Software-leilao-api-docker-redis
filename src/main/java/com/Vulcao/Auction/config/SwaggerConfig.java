package com.Vulcao.Auction.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customAPI(){
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Servidor de desenvolvimento local");

        Server prod = new Server();
        prod.setUrl("https://software-auction-api-redis.net");
        prod.setDescription("Servidor de produção Https");

        return new OpenAPI().info(new Info()
                .title("Softaware leilão API")
                .version("v1.0")
                .description("API para gerenciamento de um leilão com atualização em tempo real"))
                .servers(List.of(localServer, prod))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", SchemeSecurity()));
    }

    private SecurityScheme SchemeSecurity(){
        return new SecurityScheme()
                .name("Bearer Authentication")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}
