package edu.nccu.cs.dispatchcloud.manager;

import edu.nccu.cs.dispatchcloud.fixdata.FixDataEntity;
import edu.nccu.cs.dispatchcloud.fixdata.FixDataRepository;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataEntity;
import edu.nccu.cs.dispatchcloud.signedmeterdata.SignedMeterDataRepository;
import edu.nccu.cs.utils.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class QuerierService {

    @Autowired
    private SignedMeterDataRepository meterDataRepository;

    @Autowired
    private FixDataRepository fixDataRepository;

    public List<SignedMeterDataEntity> getMeterDataByDate(LocalDate date) {
        Instant start = DateTimeUtils.toInstant(date.atTime(0, 0, 0));
        Instant end = DateTimeUtils.toInstant(date.plusDays(1).atTime(0, 0, 0));

        return meterDataRepository.findByTimestampBetween(start.toEpochMilli(), end.toEpochMilli());
    }

    public List<FixDataEntity> getFixDataByDate(LocalDate date) {
        Instant start = DateTimeUtils.toInstant(date.atTime(0, 0, 0));
        Instant end = DateTimeUtils.toInstant(date.plusDays(1).atTime(0, 0, 0));

        return fixDataRepository.findByTimestampBetween(start.toEpochMilli(), end.toEpochMilli());
    }

    public List<SignedMeterDataEntity> getMeterDataBetweenPreTimestamp(long start, long end) {
        return meterDataRepository.findByPreTimestampBetween(start, end);
    }

    public List<SignedMeterDataEntity> getMeterDataBetweenTimestamp(long start, long end) {
        return meterDataRepository.findByTimestampBetween(start, end);
    }
}
