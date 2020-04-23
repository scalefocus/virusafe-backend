package io.virusafe.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {

    private static final String SWAGGER_VERSION = "1.0.0";
    private static final String VIRU_SAFE_BE_REST_API = "ViruSafe Backend REST API";
    public static final String BASE_PACKAGE = "io.virusafe";

    /**
     * Provides the primary API configuration.
     *
     * @param apiEndPointsInfo
     * @return
     */
    @Bean
    public Docket api(final ApiInfo apiEndPointsInfo) {
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
                .paths(PathSelectors.any())
                .build()
                .genericModelSubstitutes(ResponseEntity.class)
                .apiInfo(apiEndPointsInfo);
    }

    /**
     * Gives information about the API.
     *
     * @return
     */
    @Bean
    public ApiInfo apiEndPointsInfo() {
        return new ApiInfoBuilder().title(VIRU_SAFE_BE_REST_API)
                .description(VIRU_SAFE_BE_REST_API)
                .version(SWAGGER_VERSION)
                .build();
    }
}