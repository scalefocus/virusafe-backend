package io.virusafe.configuration;

import io.virusafe.security.filter.ClientIdFilter;
import io.virusafe.security.filter.FilterChainExceptionHandlingFilter;
import io.virusafe.security.filter.JwtAuthenticationFilter;
import io.virusafe.security.mdc.MdcFilter;
import io.virusafe.service.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;

    private final ClientIdFilter clientIdFilter;

    private final FilterChainExceptionHandlingFilter filterChainExceptionHandlingFilter;

    private final MdcFilter mdcFilter;

    private final AuthenticationProvider authenticationProvider;

    /**
     * Construct the SecurityConfiguration bean, injecting required dependencies and instantiating all required
     * filter beans.
     * Filter beans are instantiated here, rather than autowired, to avoid them being picked up and injected
     * in places where we don't want them to be.
     *
     * @param jwtConfiguration       the autowired JwtConfiguration to be used by authentication filters
     * @param exceptionResolver      the autowired HandlerExceptionResolver
     * @param userDetailsService     the autowired UserDetailsService to use for validating users during authentication
     * @param authenticationProvider the autowired AuthenticationProvider to use for basic authentication
     */
    @Autowired
    public SecurityConfiguration(final JwtConfiguration jwtConfiguration,
                                 final @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionResolver,
                                 final UserDetailsService userDetailsService,
                                 final AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = new JwtAuthenticationFilter(jwtConfiguration, userDetailsService);
        this.clientIdFilter = new ClientIdFilter(jwtConfiguration);
        this.filterChainExceptionHandlingFilter = new FilterChainExceptionHandlingFilter(exceptionResolver);
        this.mdcFilter = new MdcFilter();
        this.authenticationProvider = authenticationProvider;
    }

    @Configuration
    @Order(100)
    public class DefaultJwtAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    // Don't authorize access to token-related endpoints.
                    .mvcMatchers(
                            "/token",
                            "/token/refresh",
                            "/pin")
                    .permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .addFilterBefore(filterChainExceptionHandlingFilter, UsernamePasswordAuthenticationFilter.class)
                    .addFilterAfter(jwtAuthFilter, FilterChainExceptionHandlingFilter.class)
                    .addFilterAfter(clientIdFilter, FilterChainExceptionHandlingFilter.class)
                    .addFilterAfter(mdcFilter, JwtAuthenticationFilter.class)
                    .exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        }
    }

    @Configuration
    @Order(2)
    public class BasicAuthAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            http
                    .csrf().disable()
                    .mvcMatcher("/admin/**")
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .httpBasic()
            ;
        }

        /**
         * Configure authentication manager to use our custom authentication provider.
         *
         * @param auth the AuthenticationManagerBuilder to configure
         */
        @Autowired
        public void configureGlobal(final AuthenticationManagerBuilder auth) {
            auth.authenticationProvider(authenticationProvider);
        }

        /**
         * Create a BCryptPasswordEncoder bean to use for basic authentication.
         *
         * @return the BCryptPasswordEncoder
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }

    @Configuration
    @Order(1)
    public static class NoSecurityAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(final HttpSecurity http) throws Exception {
            // Ignore all Swagger-related endpoints as well as public information documents.
            http
                    .csrf().disable()
                    .requestMatchers()
                    .mvcMatchers("/v2/api-docs",
                            "/configuration/ui",
                            "/swagger-resources/**",
                            "/configuration/**",
                            "/swagger-ui.html",
                            "/webjars/**",
                            "/actuator/**",
                            "/information/**",
                            "/index.html");
        }
    }
}