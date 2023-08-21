package edu.washu.bms.fedex.fedexintegration.controller;

import edu.washu.bms.fedex.fedexintegration.service.BMSFedexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@Component
public class BMSFedexController {

    private static final Logger logger = LoggerFactory.getLogger(BMSFedexController.class);

    @Autowired
    private BMSFedexService bmsFedexService;

//    @GetMapping("/kit")
//    @Scheduled(cron="*/5 * * * * MON-FRI")
//    public String index() {
//        logger.debug("In Kit integration");
//        System.out.println("----> Hey I am running!");
//        return "Hello I am good!!";
//    }

    // Scheduler for creating the request!
//   @Scheduled(cron="0 0 22 * * MON-FRI" )
    //@Scheduled(cron="*/10 * * * * MON-FRI")
    @Scheduled(fixedDelay = 60000, initialDelay = 60000)
    public void createKitRequest() {
        logger.info("Create shipment Initiated");
       bmsFedexService.createBmsKitRequest();
    }


}


