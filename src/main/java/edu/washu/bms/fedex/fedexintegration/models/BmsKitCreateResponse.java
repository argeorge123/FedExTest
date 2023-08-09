package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class BmsKitCreateResponse {
    private Meta meta;
    private RequestStatus requestStatus;
}
