package edu.nccu.cs.simmeter.sign;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import edu.nccu.cs.domain.MeterData;
import edu.nccu.cs.domain.SignedMeterData;
import edu.nccu.cs.exception.ApplicationException;
import edu.nccu.cs.simmeter.normal.MeterDataService;
import edu.nccu.cs.simmeter.util.SignatureUtils;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static edu.nccu.cs.utils.ByteUtils.getBytesFromLong;
import static edu.nccu.cs.utils.ByteUtils.mergeByteArrays;
import static edu.nccu.cs.utils.ExceptionUtils.getStackTrace;

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
            byte[] powerBytes = getBytesFromLong(meterData.getPower());
            byte[] energyBytes = getBytesFromLong(meterData.getEnergy());
            byte[] merged = mergeByteArrays(powerBytes, energyBytes);
            byte[] signed = SignatureUtils.sign(keyPair, merged);
            String signedB64 = Base64.toBase64String(signed);

            return SignedMeterData.builder()
                                  .meterData(meterData)
                                  .signature(signedB64)
                                  .build();

        } catch (NoSuchAlgorithmException | SignatureException
                | NoSuchProviderException | InvalidKeyException e) {
            log.error(getStackTrace(e));
            throw new ApplicationException(e);
        }
    }
}
