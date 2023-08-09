package edu.washu.bms.fedex.fedexintegration.service;

import edu.washu.bms.fedex.fedexintegration.Entities.BmsKitRequest;
import edu.washu.bms.fedex.fedexintegration.controller.BMSFedexController;
import edu.washu.bms.fedex.fedexintegration.models.*;
import edu.washu.bms.fedex.fedexintegration.repository.*;
import org.hibernate.query.criteria.internal.expression.ConcatExpression;
import org.json.simple.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.Query;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import javax.mail.MessagingException;
import java.io.IOException;


import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.text.DateFormat;
import java.util.Calendar;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;




@Service
@Configuration
public class BMSFedexService {

    private static final Logger logger = LoggerFactory.getLogger(BMSKitRequestController.class);

    @Autowired
    private BMSFedexRepository bmsFedexRepository;
    @Autowired
    private MayoForSyncRepository mayoForSyncRepository;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    EmailService emailService;


    // from config properties file
    @Value("${kit_create_url}")
    private String kit_create_url;
    @Value("${auth_url}")
    private String auth_url;
    @Value("${grant_type}")
    private String grant_type;
    @Value("${client_id}")
    private String client_id;
    @Value("${client_secret}")
    private String client_secret;


    public String biomsKitRequestID;


    // Create Kit Request for POST
    public void createBmsKitRequest(){
        logger.info("got into createbmskitrequest");
        //logger.debug("In Create Request Service-->" + kit_create_url);
        logger.info("In Create Request Service-->" + kit_create_url);
        List<BmsKitRequest> bmsKitRequests = this.findAllOPenKitRequests();
        if(this.findAllOPenKitRequests()==null){
        logger.info("No kit requests found for processing");
        }
        else {
            prepareAndCreate(bmsKitRequests, false);
        }
    }


    private void prepareAndCreate(List<BmsKitRequest> bmsKitRequests,boolean isPriority) {
        logger.info("In Prepare and Create Request-->");
        long endDate = Timestamp.valueOf(LocalDateTime.now()).getTime();
        Calendar cal = Calendar.getInstance();
        Date currentTime = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH");
        String formattedDate= dateFormat.format(currentTime);
        long compareTime = Integer.parseInt(formattedDate);
        logger.info("---------->compareTime--------"+compareTime);
               //This is in a loop and it processes all the requests one by one inside this for loop.
        for(BmsKitRequest bmsKitRequest: bmsKitRequests) {

            HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(),getHttpHeaders());
            if(entity== null){
                logger.info("entered entity");
            }
            else {
                logger.info("success");
            }
        }
    }

    private List<BmsKitRequest> findKitRequest(){
        List<BmsKitRequest> nBmsKitRequest= bmsKitRequestRepository.findKitRequest(Long.valueOf(biomsKitRequestID));
        return nBmsKitRequest;
    }

    // Sets Headers for the Request
    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (this.getAccessToken().length() > 0) {
            headers.set("Authorization", "Bearer "+this.getAccessToken());
        } else {
            logger.info(this.getAccessToken()+"------------->access token");
            logger.info("Unable to get access token and will rerun in 1 hour!");
        }
        return headers;
    }

    // Gets all Open Requests in a time interval from BioMS DB
    private List<BmsKitRequest> findAllOPenKitRequests(){
        logger.info("Finding all WashU In process kit requests to process ");
      List<BmsKitRequest> allOpenRequests= bmsKitRequestRepository.findAllOpenRequests(81,"In Process", getLastRunTimeFwd());
      logger.info("Number of WashU In process kit = {}",allOpenRequests.size());
      return allOpenRequests;
    }

    // Gets the Bearer token from Mayo Authentication service
    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type","application/x-www-form-urlencoded");
        TokenPayload tokenPayload= new TokenPayload();
        tokenPayload.setMclient_id(client_id);
        tokenPayload.setMclient_secret(client_secret);
        tokenPayload.setMgrant_type(grant_type);
        String t1 = client_id;
        String t2 = client_secret;
        String t3 = grant_type;
        System.out.println(t1);
        System.out.println(t2);
        System.out.println(t3);
       // HttpEntity<String> request = new HttpEntity<String>(tokenPayload.toString(), headers);

        MultiValueMap<String, String> map= new LinkedMultiValueMap<String, String>();
        map.add("client_id", t1);
        map.add("client_secret", t2);
        map.add("grant_type", t3);
        HttpEntity<MultiValueMap<String, String>> request1 = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        logger.info("request1--------->"+request1);
        String accessToken = "";
        logger.info("Trying to Get access token!"+this.auth_url);
        try {
            ResponseEntity<TokenResponse> response = new RestTemplate().exchange(this.auth_url, HttpMethod.POST, request1, TokenResponse.class);
            logger.info(response.getStatusCode()+"-------------->got status code");
            if (response.getStatusCode() == HttpStatus.OK && response.getBody().getAccess_token() != null) {
                accessToken =  response.getBody().getAccess_token();
                logger.info(accessToken +"---> Got access token!");
            }
        } catch (Exception ex) {
            //logger.debug("Unable to get access token due to = {}",ex.getMessage());
            logger.info("Unable to get access token due to = {}",ex.getMessage());
            emailService.sendSimpleEmail("alliancedevelopment@email.wustl.edu" ,"Alliance-FedEx Integration Get Access Token failed", "Could not get access token due to = {}"+ex.getMessage());
        }
        return accessToken;
    }

    // Gets Last run time Stamp from Mayo Sync table and parse it to date.
    private Timestamp getLastRunTimeFwd() {
        Long lastRunTimestamp = mayoForSyncRepository.getLastRunTimestamp().getLastRunTimeStamp();
        java.sql.Timestamp one = new java.sql.Timestamp(lastRunTimestamp);
        logger.info("forward sync last run time stamp = {}", Instant.ofEpochMilli(lastRunTimestamp).atZone(ZoneId.systemDefault()).toLocalDate());
        return one;
    }

     // Updates Last run time Stamp in Mayo Sync table forward sync.
     private void updateLastRunTimeFwd(long dbEndDate) {
        logger.info("forward sync updated last run time stamp = {}",dbEndDate);
        mayoForSyncRepository.updateLastRunTimestamp(dbEndDate);
    }


       // Find Site Name
    private String findSiteByID(Long siteId) {
        return siteRepository.findSiteById(siteId);
    }

    // Find Kit Name
    private String getKitType(long kitId) {
        logger.debug("Get Kit name by id = {}",kitId);
        logger.info(bmsKitRepository.findBmsKitById(kitId)+"----------->external kit type id");
        return bmsKitRepository.findBmsKitById(kitId);

    }
}
