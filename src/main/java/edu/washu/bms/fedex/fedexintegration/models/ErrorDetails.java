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
    private List<ErrorDetails> errors;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getCustomerTransactionId() {
        return customerTransactionId;
    }

    public void setCustomerTransactionId(String customerTransactionId) {
        this.customerTransactionId = customerTransactionId;
    }

    public List<ErrorDetails> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDetails> errors) {
        this.errors = errors;
    }
}