package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Data
@Getter
@Setter


public class TransactionShipments {
    private ShipmentDocuments shipmentDocuments;

    public ShipmentDocuments getShipmentDocuments() {
        return shipmentDocuments;
    }

    public void setShipmentDocuments(ShipmentDocuments shipmentDocuments) {
        this.shipmentDocuments = shipmentDocuments;
    }
}
