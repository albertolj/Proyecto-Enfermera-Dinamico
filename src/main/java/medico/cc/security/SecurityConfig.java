package medico.cc.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Desactiva CSRF para facilitar las pruebas, ajustar según sea necesario
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/public/**").permitAll() // Permitir acceso a ciertas rutas públicas
                .anyRequest().authenticated() // Resto requiere autenticación
            )
            .httpBasic(); // Usar autenticación básica
        return http.build();
    }
}
