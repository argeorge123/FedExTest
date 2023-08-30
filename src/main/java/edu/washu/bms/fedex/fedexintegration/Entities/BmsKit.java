package edu.washu.bms.fedex.fedexintegration.Entities;

import javax.persistence.*;

@Entity
@Table(name = "BMS_SPECIMEN_COLLECTION_KIT")
public class BmsKit {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "SPEC_COLL_KIT_NAME")
    private String specCollKitName;
    @Column(name = "SPEC_COLL_KIT_CONTENTS")
    private String specCollKitContents;
    @Column(name = "REPO_SITE_ID")
    private String repSiteId;
    @Column(name = "SPEC_COLL_KIT_STATUS")
    private String specCollKitStatus;
    @Column(name = "COLLECTION_PROTOCOL_ID")
    private int collectionProtocolId;
    @Column(name="EXTRNL_KIT_TYPE")
    private String kitType;
}
