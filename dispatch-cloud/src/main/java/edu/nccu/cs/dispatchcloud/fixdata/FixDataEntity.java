package edu.nccu.cs.dispatchcloud.fixdata;

import edu.nccu.cs.dispatchcloud.common.DocumentBase;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document("fix_data")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FixDataEntity extends DocumentBase {
    private Long timestamp;
    private FixState state;
    private Date initTime;
    private Date waitTime;
    private Date doneTime;

    public FixDataEntity init() {
        String id = String.format("FIX::%d", timestamp);
        super.setId(id);
        return this;
    }

    public enum FixState {
        INIT,
        WAIT,
        DONE
    }
}
