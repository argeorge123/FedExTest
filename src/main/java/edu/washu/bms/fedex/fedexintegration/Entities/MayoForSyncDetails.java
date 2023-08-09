package edu.washu.bms.fedex.fedexintegration.Entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Mayo_Kit_Sync_Details_Fw ")
@Data
@Getter
@Setter
public class MayoForSyncDetails {
    @Id
    @Column(name = "LAST_RUN_TIME_STAMP")
    private Long lastRunTimeStamp;
}
