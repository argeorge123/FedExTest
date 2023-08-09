package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Getter
@Setter
@Data
@ResponseBody
public class Notifications {
    private RequestStatus requestStatus;
    private String statusChangeDate;
    private String denialReason;
    private String comments;
}
