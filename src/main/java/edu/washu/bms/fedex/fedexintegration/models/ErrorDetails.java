package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Data
@Getter
@Setter
public class ErrorDetails {
    private String transactionId;
    private String customerTransactionId;
    private List<Errors> errors;
}