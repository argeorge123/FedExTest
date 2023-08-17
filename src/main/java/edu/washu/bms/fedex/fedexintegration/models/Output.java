package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;


@Data
@Getter
@Setter

public class Output {
    private List<TransactionShipments> transactionShipments;
}
