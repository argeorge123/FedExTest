package edu.washu.bms.fedex.fedexintegration.repository;

import edu.washu.bms.kit.kitintegration.Entities.BmsKitRequest;
import edu.washu.bms.kit.kitintegration.Entities.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SiteRepository extends JpaRepository<Site, Long> {

    @Query("select c.Name from Site c where c.identifier = :id")
    String findSiteById(@Param("id") Long identifier);
}
