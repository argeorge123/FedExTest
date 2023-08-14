package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;

@Data
@Getter
@Setter
@ResponseBody

public class TransactionShipments {
    private ShipmentDocuments shipmentDocuments;
}
