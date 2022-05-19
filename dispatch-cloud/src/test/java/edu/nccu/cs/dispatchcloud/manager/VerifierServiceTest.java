package edu.nccu.cs.dispatchcloud.manager;

import edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity;
import edu.nccu.cs.dispatchcloud.fixdata.FixDataRepository;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static edu.nccu.cs.utils.CollectionUtils.toList;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
public class VerifierServiceTest {
    @Autowired
    private ReceiverService receiverService;
    @Autowired
    private VerifierService verifierService;
    @Autowired
    private SignedMeterDataRepository meterDataRepository;
    @Autowired
    private FixDataRepository fixDataRepository;

    @Test
    public void testLoadContext() {
        assertThat(receiverService).isNotNull();
        assertThat(verifierService).isNotNull();
        assertThat(meterDataRepository).isNotNull();
        assertThat(fixDataRepository).isNotNull();
    }

    @Test
    public void testIdempotent() {
        fixDataRepository.deleteAll();

        Set<Long> fixData = Set.of(System.currentTimeMillis());

        verifierService.createFixData(fixData);

        List<FixDataEntity> fixData1 = toList(fixDataRepository.findAll());
        assertThat(fixData1.size()).isEqualTo(fixData.size());

        log.info("fix data 1: {}, id: {}",
                fixData1.get(0).toString(),
                fixData1.get(0).getId());

        verifierService.createFixData(fixData);

        List<FixDataEntity> fixData2 = toList(fixDataRepository.findAll());
        assertThat(fixData2.size())
                .isEqualTo(fixData.size())
                .isEqualTo(fixData1.size());

        fixDataRepository.deleteAll();
    }
}
