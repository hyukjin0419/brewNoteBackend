package com.pard.server.brewnotebackend.global.config;


//SwaggerConfig.java

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo(){
        return new Info()
                .title("Blendery Test APIs")
//                .description("Dash Board API")
//                .version("1.0.0")
                .contact(new Contact()
                        .name("Hyukjin Choi")
                        .email("hjchoi.career@gmail.com"));
    }
}