package edu.nccu.cs.simmeter.util;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jcajce.spec.RawEncodedKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.Test;

import static edu.nccu.cs.simmeter.util.ByteUtils.getBytesFromLong;
import static edu.nccu.cs.simmeter.util.ByteUtils.mergeByteArrays;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class SignatureUtilTest {

    @Test
    public void testSign() throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        long a = 102313;
        long b = 203019;
        byte[] aArr = getBytesFromLong(a);
        byte[] bArr = getBytesFromLong(b);
        byte[] cArr = mergeByteArrays(aArr, bArr);

        KeyPairGenerator g = KeyPairGenerator.getInstance("DSA", "BC");
        g.initialize(1024);
        KeyPair keyPair = g.generateKeyPair();

        byte[] sign = SignatureUtils.sign(keyPair, cArr);
        log.info(Base64.getEncoder().encodeToString(sign));

        PublicKey pub = keyPair.getPublic();
        byte[] pubRaw = pub.getEncoded();
        // byte[] pubRaw = new PKCS8EncodedKeySpec(pub.getEncoded()).getEncoded();
        String pubB64 = Base64.getEncoder().encodeToString(pubRaw);

        PrivateKey priv = keyPair.getPrivate();
        byte[] privRaw = priv.getEncoded();
        // byte[] privRaw = new PKCS8EncodedKeySpec(priv.getEncoded()).getEncoded();
        String privB64 = Base64.getEncoder().encodeToString(privRaw);

        log.info("pub: {}", pubB64);
        log.info("priv: {}", privB64);

        byte[] pubImp = Base64.getDecoder().decode(pubB64);
        byte[] privImp = Base64.getDecoder().decode(privB64);

        KeyFactory factory = KeyFactory.getInstance("DSA", "BC");
        // PublicKey pubKey2 = factory.generatePublic(new PKCS8EncodedKeySpec(pubImp));
        PublicKey pubKey2 = factory.generatePublic(new X509EncodedKeySpec(pubImp));
        PrivateKey privKey2 = factory.generatePrivate(new PKCS8EncodedKeySpec(privImp));
        KeyPair keyPair2 = new KeyPair(pubKey2, privKey2);

        byte[] sign2 = SignatureUtils.sign(keyPair2, cArr);

        boolean result1 = SignatureUtils.verify(keyPair2, cArr, sign);
        assertThat(result1).isTrue();

        boolean result2 = SignatureUtils.verify(keyPair, cArr, sign2);
        assertThat(result2).isTrue();
    }

}
