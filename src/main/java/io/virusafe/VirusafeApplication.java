package io.virusafe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@SuppressWarnings({"PMD.UseUtilityClass"})
public class VirusafeApplication {

    /**
     * Spring application entrypoint
     * @param args app arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(VirusafeApplication.class, args);
    }

}
