package edu.nccu.cs.recorder.querier;

import edu.nccu.cs.recorder.fetcher.SignedMeterEntity;
import edu.nccu.cs.recorder.fetcher.SignedMeterRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Objects;
import java.util.Optional;

@Controller
@Slf4j
public class QuerierController {

    @Autowired
    private SignedMeterRepository repository;

    @GetMapping("/meterData/{timestamp}")
    public RecorderResponse getByTimestamp(@PathVariable("timestamp") Long timestamp) {
        if (Objects.isNull(timestamp)) {
            return EMPTY_RESPONSE;
        }

        Optional<SignedMeterEntity> entity = repository.findByTimestamp(timestamp);

        return entity.map(RecorderResponse::new)
                     .orElse(EMPTY_RESPONSE);
    }

    public static final RecorderResponse EMPTY_RESPONSE = new RecorderResponse();

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecorderResponse {
        private SignedMeterEntity payload;
    }
}
