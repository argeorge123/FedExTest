package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Data
public class RequestedShipment {
    private Shipper shipper;
    private List<Recipients> recipients;
    private String pickupType;
    private String serviceType;
    private String packagingType;
    private double totalWeight;
    private ShippingChargesPayment shippingChargesPayment;
    private LabelSpecification labelSpecification;
    private List<RequestedPackageLineItems> requestedPackageLineItems;


}
