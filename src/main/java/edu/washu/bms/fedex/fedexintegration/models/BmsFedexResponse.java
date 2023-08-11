package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BmsFedexResponse {
    private String transactionId;
    private String customerTransactionId;
    private Output output;
}
