package edu.nccu.cs.simmeter.config;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import edu.nccu.cs.simmeter.exception.SystemException;
import edu.nccu.cs.simmeter.util.ExceptionUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class SecurityConfig {

    @Value("${key-conf.length}")
    private int length;
    @Value("${key-conf.private}")
    private String privateKey;
    @Value("${key-conf.public}")
    private String publicKey;
    @Value("${key-conf.type}")
    private String type;
    @Value("${key-conf.provider}")
    private String provider;

    public SecurityConfig() {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Bean
    public KeyPair keyPair() throws SystemException {
        try {
            return generateFromImport();
        } catch (SystemException ex) {
            return generateFromInit();
        }
    }

    private KeyPair generateFromInit() throws SystemException {
        try {
            log.warn("generate key pair.");
            KeyPairGenerator g = KeyPairGenerator.getInstance(type, provider);
            g.initialize(length);
            return g.generateKeyPair();
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            log.error("failed too.");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new SystemException("Key pair importing and generating are all failed.");
        }
    }

    private KeyPair generateFromImport() throws SystemException {
        try {
            log.warn("load key pair.");
            KeyFactory factory = KeyFactory.getInstance(type, provider);
            byte[] pubKeyBlob = Base64.decode(publicKey);
            PublicKey pubKey = factory.generatePublic(new X509EncodedKeySpec(pubKeyBlob));
            byte[] privKeyBlob = Base64.decode(privateKey);
            PrivateKey privKey = factory.generatePrivate(new PKCS8EncodedKeySpec(privKeyBlob));
            return new KeyPair(pubKey, privKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchProviderException e) {
            log.error("failed.");
            log.error(ExceptionUtils.getStackTrace(e));
            throw new SystemException(e);
        }
    }
}
