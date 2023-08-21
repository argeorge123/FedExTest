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



// Updates the shipping label with url
    @Transactional
    @Modifying
    @Query("update BmsKitRequest c set c.shippingLabel = :url,c.shipmentTrackingNumber =:masterTrackNumber where c.id = :id")
    void updateShippingLabel(@Param("url") String url,
                             @Param("masterTrackNumber") String masterTrackNumber,
                             @Param("id") Long id);

    // Updates the status to "Ready to send" once the request has been sent to Mayo
    @Transactional
    @Modifying
    @Query("update BmsKitRequest c set c.kitRequestStatus = :status where c.id = :id")
    void updateKitStatus(@Param("status") String status,
                         @Param("id") Long id);
}
