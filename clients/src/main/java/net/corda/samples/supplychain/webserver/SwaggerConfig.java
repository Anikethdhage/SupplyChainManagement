package net.corda.samples.supplychain.webserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket SwaggerApi() {
        return new Docket(DocumentationType.OAS_30)
                .select().apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any()).build().pathMapping("/").useDefaultResponseMessages(false)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("API Documentation", "Documentation of all API's", "1.0.0","T&C", new Contact("Aniketh", "https://github.com/anikethdhage","anikethdhage@gmail.com")
        ,"License of API", "https://swagger.io/docs/", Collections.emptyList());
    }
}
