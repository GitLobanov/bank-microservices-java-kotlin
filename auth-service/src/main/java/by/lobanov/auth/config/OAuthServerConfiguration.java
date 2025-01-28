package by.lobanov.auth.config;

import by.lobanov.auth.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.time.Duration;
import java.util.Set;

@Configuration
public class OAuthServerConfiguration {

    private final PasswordEncoder passwordEncoder;

    public OAuthServerConfiguration(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        // Client Credentials Client
        RegisteredClient clientCredentialsClient = RegisteredClient.withId("client-credentials-client-id")
                .clientId("client-credentials")
                .clientSecret(passwordEncoder.encode("client-secret"))
                .authorizationGrantTypes(grantTypes -> grantTypes.add(AuthorizationGrantType.CLIENT_CREDENTIALS))
                .scopes(scopes -> scopes.addAll(Set.of("read", "write")))
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(false)
                        .build())
                .build();

        // Authorization Code Client
        RegisteredClient authorizationCodeClient = RegisteredClient.withId("auth-code-client-id")
                .clientId("auth-code-client")
                .clientSecret(passwordEncoder.encode("auth-code-secret"))
                .authorizationGrantTypes(grantTypes -> grantTypes.addAll(Set.of(
                        AuthorizationGrantType.AUTHORIZATION_CODE,
                        AuthorizationGrantType.REFRESH_TOKEN)))
                .scopes(scopes -> scopes.addAll(Set.of("read", "write")))
                .redirectUri("http://localhost:8095/login/oauth2/code/auth-code-client")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .refreshTokenTimeToLive(Duration.ofDays(30))
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)
                        .build())
                .build();

        return new InMemoryRegisteredClientRepository(clientCredentialsClient, authorizationCodeClient);
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(RegisteredClientRepository registeredClientRepository) {
        return new InMemoryOAuth2AuthorizationService();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                .anyRequest().authenticated());
        http.headers(Customizer.withDefaults());
        http.sessionManagement(Customizer.withDefaults());
        http.formLogin(Customizer.withDefaults());
        http.anonymous(Customizer.withDefaults());
        http.csrf(Customizer.withDefaults());
        http.userDetailsService(inMemoryUserDetailsService());
        return http.build();
    }

    public UserDetailsService inMemoryUserDetailsService() {
        org.springframework.security.core.userdetails.User.UserBuilder users = org.springframework.security.core.userdetails.User.builder();
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        userDetailsManager.createUser(users.username("admin")
                .password(passwordEncoder.encode("admin"))
                .roles("ADMIN")
                .build());
        userDetailsManager.createUser(users.username("user")
                .password(passwordEncoder.encode("user"))
                .roles("USER")
                .build());
        return userDetailsManager;
    }
}