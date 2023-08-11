package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RepoAddress {
    private String streetLines;
    private String city;
    private String postalCode;
    private String countryCode;
}
