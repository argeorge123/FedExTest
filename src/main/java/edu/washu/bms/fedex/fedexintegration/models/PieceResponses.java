package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;

@Getter
@Setter
@Data
@ResponseBody

public class PieceResponses {
    private String masterTrackingNumber;
    private String trackingNumber;
    private String url;
}
