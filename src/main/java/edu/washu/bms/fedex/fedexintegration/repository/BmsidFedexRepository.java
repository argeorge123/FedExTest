package edu.washu.bms.fedex.fedexintegration.repository;

import edu.washu.bms.fedex.fedexintegration.Entities.BmsKit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BmsidFedexRepository  extends JpaRepository<BmsKit, Long> {

    @Query("select c.specCollKitName from BmsKit c where c.id =:id")
    String findBmsKitById(@Param("id") long id);
}
