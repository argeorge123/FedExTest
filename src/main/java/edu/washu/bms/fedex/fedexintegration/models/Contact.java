package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Contact {
    private String personName;
    private String emailAddress;
    private String phoneNumber;
    private String companyName;
}
