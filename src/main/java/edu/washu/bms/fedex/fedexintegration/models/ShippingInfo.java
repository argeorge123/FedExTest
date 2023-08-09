package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class ShippingInfo {
    private String shipper;
    private String shippingAccount;
    // TODO needs to be updated!
    private ShippingAddress shippingAddress;

}
