package edu.washu.bms.fedex.fedexintegration.repository;

import edu.washu.bms.fedex.fedexintegration.Entities.BmsKitRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

@Repository
public interface BMSFedexRepository extends JpaRepository<BmsKitRequest, Long> {
//ALL BIOMS DATABASE HANDELING FOR READ AND UPDATE ARE DONE HERE
//Finding all open requests for process.
    @Query("select c from BmsKitRequest c where c.repository= :repository and c.kitRequestStatus= :kit_request_status ")
    List<BmsKitRequest> findAllOpenRequests(@Param("repository") int repository,
                                            @Param("kit_request_status") String kit_request_status);

 //Updating the kit request transaction with Mayo kit request ID.
    @Transactional
    @Modifying
   @Query("update BmsKitRequest c set c.externalRequestId = :reqID where c.id = :extReqID")
    void updateExternalRequestID(@Param("reqID") String reqID,
                                 @Param("extReqID") Long extReqID);

    @Query("select c from BmsKitRequest c where c.id = :extReqID")
    List<BmsKitRequest> findKitRequest(@Param("extReqID") Long extReqID);

//After getting notifications, updates the Status, Tracking number, Comments and Modified date.
    @Transactional
    @Modifying
    @Query("update BmsKitRequest c set c.kitRequestStatus = :status,c.shipmentTrackingNumber= :trackingNumber, c.repositoryComments = :repositoryComments, c.modifiedTimeStamp = :modifiedTimeStamp where c.id = :extReqID")
    void updateNotificationStatus(@Param("status") String status,
                         @Param("extReqID") Long extReqID,
                         @Param("trackingNumber") String trackingNumber,
                         @Param("repositoryComments") String repositoryComments,
                         @Param("modifiedTimeStamp") Timestamp modifiedTimeStamp);

    @Transactional
    @Modifying
    @Query("update BmsKitRequest c set c.kitShipmentDate =:pstatusChangeDate where c.id = :extReqID")
    void updateShippingDate(@Param("extReqID") Long extReqID,
                            @Param("pstatusChangeDate") Date pstatusChangeDate);

// Updates the status to "In process" once the request has been sent to Mayo
    @Transactional
    @Modifying
    @Query("update BmsKitRequest c set c.kitRequestStatus = :status where c.id = :extReqID")
    void updateKitStatus(@Param("status") String status,
                                    @Param("extReqID") Long id);
}
