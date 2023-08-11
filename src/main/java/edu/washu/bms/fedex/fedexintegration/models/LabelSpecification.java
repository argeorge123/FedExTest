package edu.washu.bms.fedex.fedexintegration.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class LabelSpecification {
    private LabelStockType labelStockType;
    private ImageType imageType;
}
