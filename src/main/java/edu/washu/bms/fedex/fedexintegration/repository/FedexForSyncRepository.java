package edu.washu.bms.fedex.fedexintegration.repository;

import edu.washu.bms.fedex.fedexintegration.Entities.FedexForSyncDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface FedexForSyncRepository extends JpaRepository<FedexForSyncDetails, Long> {

    @Query("select c from FedexForSyncDetails c")
    FedexForSyncDetails getLastRunTimestamp();

    @Transactional
    @Modifying
    @Query("update FedexForSyncDetails c set c.lastRunTimeStamp = :lastRunTimeStamp")
    void updateLastRunTimestamp(long lastRunTimeStamp);
}
