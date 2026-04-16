package com.example.invoice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI invoiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Invoice PDF Generator API")
                        .version("v1")
                        .description("REST API for generating enterprise-grade invoice PDFs")
                        .contact(new Contact().name("Invoice Team").email("support@example.com")));
    }
}
