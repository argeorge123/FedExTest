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
    @Query("select c from BmsKitRequest c where c.repository= :repository and c.kitRequestStatus= :kit_request_status and (c.shippingOption =:shipping_Regular or c.shippingOption =:shipping_Expedited)")
    List<BmsKitRequest> findAllOpenRequests(@Param("repository") int repository,
                                            @Param("kit_request_status") String kit_request_status,
                                            @Param("shipping_Regular") String shipping_Regular,
                                            @Param("shipping_Expedited") String shipping_Expedited);

 //Updating the kit request transaction with Mayo kit request ID.
    @Transactional
    @Modifying
   @Query("update BmsKitRequest c set c.externalRequestId = :reqID where c.id = :extReqID")
    void updateExternalRequestID(@Param("reqID") String reqID,
                                 @Param("extReqID") Long extReqID);

    @Query("select c from BmsKitRequest c where c.id = :extReqID")
    List<BmsKitRequest> findKitRequest(@Param("extReqID") Long extReqID);


// Updates the status to "In process" once the request has been sent to Mayo
    @Transactional
    @Modifying
    @Query("update BmsKitRequest c set c.kitRequestStatus = :status where c.id = :extReqID")
    void updateKitStatus(@Param("status") String status,
                                    @Param("extReqID") Long id);
}
