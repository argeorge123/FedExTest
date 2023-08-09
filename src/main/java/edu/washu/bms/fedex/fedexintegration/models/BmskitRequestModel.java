package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BmskitRequestModel {
    private String bmsRequestID;
    private String studyID;
    private String kitType;
    private int requestedKitCount;
    private String neededDate;
    private boolean isPriority;
    private String shippingAccount;
    private Requester requester;
    private ShippingAddress shippingAddress;
}
