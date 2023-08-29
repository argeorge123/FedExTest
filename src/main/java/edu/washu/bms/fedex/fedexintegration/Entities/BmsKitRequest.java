package edu.washu.bms.fedex.fedexintegration.Entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;


@Entity
@Data
@Getter
@Setter
@Table(name = "BMS_SPECIMEN_COLL_KIT_REQ")
public class BmsKitRequest {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "REQUESTER_USER_ID")
    private Long requestedUserId;
    @Column(name = "REQUESTER_SITE_ID")
    private Long requestorSiteId;
    @Column(name = "REQUESTED_DATE")
    private Date requestedDate;
    @Column(name = "KIT_COUNT")
    private Integer kit_count;
    @Column(name = "SHORT_TITLE")
    private String shortTitle;
    @Column(name = "KIT_NEEDED_BY")
    private Date kitNeededBy;
    @Column(name = "REQUESTER_COMMENTS")
    private String requesterComments;
    @Column(name = "REPOSITORY")
    private int repository;
    @Column(name = "KIT_SHIPMENT_DATE")
    private Date kitShipmentDate;
    @Column(name = "KIT_SERIAL_NO")
    private String kitSerialNo;
    @Column(name = "KIT_REQUEST_STATUS")
    public String kitRequestStatus;
    @Column(name = "KIT_SENT_BY_USER_ID")
    private String kitSentByUserId;
    @Column(name = "REPOSITORY_COMMENTS")
    private String repositoryComments;
    @Column(name = "REQUESTER_ADDRESS")
    private String requesterAddress;
    @Column(name = "PATIENT_ID")
    private String patientId;
    @Column(name = "REQUESTER_FEDEX_NO")
    private String requesterFedexNo;
    @Column(name = "REQUESTER_EMAIL")
    private String requesterEmail;
    @Column(name = "REQUESTER_PHONE_NUMBER")
    private String requesterPhoneNumber;
    @Column(name = "SHIPMENT_TRACKING_NUMBER")
    private String shipmentTrackingNumber;
    @Column(name = "SPEC_COLL_KIT_ID")
    private int specimenCollKitId;
    @Column(name = "REQ_FIRST_NAME")
    private String reqFirstName;
    @Column(name = "REQ_LAST_NAME")
    private String reqLastName;
    @Column(name = "REQ_MIDDLE_NAME")
    private String reqMiddleName;
    @Column(name = "ADDRESS1")
    private String address1;
    @Column(name = "ADDRESS2")
    private String address2;
    @Column(name = "ADDRESS3")
    private String address3;
    @Column(name = "CITY")
    private String city;
    @Column(name = "STATE")
    private String state;
    @Column(name = "COUNTRY")
    private String country;
    @Column(name = "POSTAL_CODE")
    private String postalCode;
    @Column(name = "EXT_REQ_ID")
    private String externalRequestId;
    @Column(name = "DATE_CREATED")
    private Timestamp createdTimeStamp;
    @Column(name = "DATE_MODIFIED")
    private Timestamp modifiedTimeStamp;
    @Column(name = "SHIPPING_OPTION")
    private String shippingOption;
    @Column(name = "SHIPPING_LABEL")
    private String shippingLabel;
    @Column(name = "SHIPPING_ERROR")
    private String shippingError;
}
