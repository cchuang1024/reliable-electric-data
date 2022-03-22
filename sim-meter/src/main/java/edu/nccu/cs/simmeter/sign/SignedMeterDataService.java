package edu.nccu.cs.simmeter.sign;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import edu.nccu.cs.simmeter.exception.ApplicationException;
import edu.nccu.cs.simmeter.normal.MeterData;
import edu.nccu.cs.simmeter.normal.MeterDataService;
import edu.nccu.cs.simmeter.util.ByteUtils;
import edu.nccu.cs.simmeter.util.ExceptionUtils;
import edu.nccu.cs.simmeter.util.SignatureUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SignedMeterDataService {

    @Autowired
    private MeterDataService meterDataService;
    @Autowired
    private KeyPair keyPair;

    public synchronized SignedMeterData getSignedMeterNow() {
        MeterData meterData = meterDataService.getMeterNow();

        try {
            byte[] powerBytes = ByteUtils.getBytesFromLong(meterData.getPower());
            byte[] energyBytes = ByteUtils.getBytesFromLong(meterData.getEnergy());
            byte[] merged = ByteUtils.mergeByteArrays(powerBytes, energyBytes);
            byte[] signed = SignatureUtils.sign(keyPair, merged);
            String signedB64 = Base64.toBase64String(signed);

            return SignedMeterData.builder()
                                  .meterData(meterData)
                                  .signature(signedB64)
                                  .build();

        } catch (IOException | NoSuchAlgorithmException | SignatureException | NoSuchProviderException | InvalidKeyException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new ApplicationException(e);
        }
    }
}
