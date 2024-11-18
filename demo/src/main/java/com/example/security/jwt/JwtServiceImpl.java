package com.example.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio de JWT
 */
@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.key.private}")
    private String privateKeyPath;

    @Value("${jwt.key.public}")
    private String publicKeyPath;

    @Value("${jwt.expiration}")
    private Long jwtExpiration; // in seconds

    /**
     * Extrae el nombre de usuario del token
     */
    @Override
    public String extractUserName(String token) {
        log.info("Extracting username from token: {}", token);
        return extractClaim(token, DecodedJWT::getSubject);
    }

    /**
     * Genera un token
     */
    @Override
    public String generateToken(UserDetails userDetails) {
        log.info("Generating token for user: {}", userDetails.getUsername());
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Valida el token
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        log.info("Validating token: {} for user: {}", token, userDetails.getUsername());
        final String userName = extractUserName(token);
        return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Genera un token con datos extra
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        try {
            PrivateKey privateKey = loadPrivateKey();

            Algorithm algorithm = Algorithm.ECDSA256(null, (ECPrivateKey) privateKey);

            Date now = new Date();
            Date expirationDate = new Date(now.getTime() + (jwtExpiration * 1000)); // Convert to milliseconds

            return JWT.create()
                    .withHeader(createHeader())
                    .withSubject(userDetails.getUsername())
                    .withIssuedAt(now)
                    .withExpiresAt(expirationDate)
                    .withClaim("extraClaims", extraClaims)
                    .sign(algorithm);
        } catch (Exception e) {
            log.error("Error generating token: {}", e.getMessage());
            throw new RuntimeException("Error generating token", e);
        }
    }

    /**
     * Extrae un reclamo del token
     */
    private <T> T extractClaim(String token, Function<DecodedJWT, T> claimsResolvers) {
        DecodedJWT decodedJWT = JWT.decode(token);
        return claimsResolvers.apply(decodedJWT);
    }

    /**
     * Comprueba si el token ha expirado
     */
    private boolean isTokenExpired(String token) {
        Date expirationDate = extractExpiration(token);
        return expirationDate.before(new Date());
    }

    /**
     * Extrae la fecha de expiración
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, DecodedJWT::getExpiresAt);
    }

    /**
     * Carga la clave privada
     */
    private PrivateKey loadPrivateKey() throws Exception {
        String privateKeyPEM = loadKeyContent(privateKeyPath)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    /**
     * Carga la clave pública
     */
    private PublicKey loadPublicKey() throws Exception {
        String publicKeyPEM = loadKeyContent(publicKeyPath)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePublic(new X509EncodedKeySpec(decoded));
    }

    /**
     * Lee el contenido de una clave
     */
    private String loadKeyContent(String keyPath) throws Exception {
        if (keyPath.startsWith("classpath:")) {
            String resourcePath = keyPath.substring("classpath:".length());
            try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
                if (is == null) {
                    throw new IllegalArgumentException("Key file not found: " + keyPath);
                }
                return new String(is.readAllBytes());
            }
        } else {
            return Files.readString(Path.of(keyPath));
        }
    }

    /**
     * Crea el encabezado JWT
     */
    private Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap<>();
        header.put("typ", "JWT");
        header.put("alg", "ES256");
        return header;
    }
}
