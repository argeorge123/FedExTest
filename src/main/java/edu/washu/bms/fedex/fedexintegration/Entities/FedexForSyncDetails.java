package edu.washu.bms.fedex.fedexintegration.Entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "FEDEX_SYNC_DETAILS_FW")
@Data
@Getter
@Setter
public class FedexForSyncDetails {
    @Id
    @Column(name = "LAST_RUN_TIME_STAMP")
    private Long lastRunTimeStamp;
}
