package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Shipper {
    private RepoAddress repoAddress;
    private RepoContact repoContact;
}
