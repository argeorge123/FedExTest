package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TokenPayload {
    private String mgrant_type;
    private String mclient_id;
    private String mclient_secret;
}
