package edu.nccu.cs.dispatchcloud.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@ToString
public class DocumentBase {
    @Id
    private String id;
}
