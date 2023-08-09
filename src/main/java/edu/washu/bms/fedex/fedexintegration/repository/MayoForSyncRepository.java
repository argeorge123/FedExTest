package edu.washu.bms.fedex.fedexintegration.repository;

import edu.washu.bms.kit.kitintegration.Entities.MayoForSyncDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MayoForSyncRepository extends JpaRepository<MayoForSyncDetails, Long> {

    @Query("select c from MayoForSyncDetails c")
    MayoForSyncDetails getLastRunTimestamp();

    @Transactional
    @Modifying
    @Query("update MayoForSyncDetails c set c.lastRunTimeStamp = :lastRunTimeStamp")
    void updateLastRunTimestamp(long lastRunTimeStamp);
}
