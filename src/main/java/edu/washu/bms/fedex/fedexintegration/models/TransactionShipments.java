package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import org.springframework.web.bind.annotation.ResponseBody;


@Data
@Getter
@Setter
@ResponseBody

public class TransactionShipments {
    private String masterTrackingNumber;
    private String serviceType;
    private List<PieceResponses> pieceResponses;
}
