package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Requester {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String site;
    private String comments;
}
