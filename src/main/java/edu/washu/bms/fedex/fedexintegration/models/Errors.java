package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Data
@Getter
@Setter
public class Errors {
    private String code;
    private String message;
    private List<ParameterList> parameterList;
}
