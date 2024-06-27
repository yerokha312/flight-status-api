package dev.yerokha.flightstatusapi.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Yerbolat",
                        email = "yerbolatt312@gmail.com",
                        url = "https://t.me/yerokhych"
                ),
                title = "Flight Status API",
                description = "OpenApi documentation for Flight Status Project",
                version = "0.0.1"
        ),
        servers = {
                @Server(
                        description = "Flight Status API",
                        url = "http://localhost:8080"
                )
        }
)
public class OpenApiConfig {
}

