package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BmsfedexModel {
    private RequestedShipment requestedShipment;
    private String labelResponseOptions;
    private String accountNumber;
}
