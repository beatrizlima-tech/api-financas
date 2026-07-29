package br.com.cotiinformatica.api_financas.components;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
public class UsuarioAutenticadoComponent {

    public UUID obterUsuarioId(Jwt jwt) {

        var subject = Objects.requireNonNull(
                jwt.getSubject(),
                "O token JWT não possui o identificador do usuário."
                );

        return UUID.fromString(subject);
    }

    public String obterEmail(Jwt jwt) {

        return Objects.requireNonNull(
                jwt.getClaimAsString("email"),
                "O token JWT não possui o e-mail do usuário."
        );
    }
}
