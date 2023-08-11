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

    private static final Logger logger = LoggerFactory.getLogger(BMSFedexController.class);

    @Autowired
    private BMSFedexRepository bmsFedexRepository;
    @Autowired
    private FedexForSyncRepository fedexForSyncRepository;
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
        logger.info("In Prepare and Create fedex Request-->");
        long endDate = Timestamp.valueOf(LocalDateTime.now()).getTime();
        Calendar cal = Calendar.getInstance();
        Date currentTime = cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH");
        String formattedDate= dateFormat.format(currentTime);
        //This is in a loop and it processes all the requests one by one inside this for loop.
        for(BmsKitRequest bmsKitRequest: bmsKitRequests) {
            BmsfedexModel bmsfedexModel = new BmsfedexModel();
            bmsfedexModel.setRequestedShipment(setRequestedShipment(bmsKitRequest));
            bmsfedexModel.setLabelResponseOptions("URL_ONLY");
            bmsfedexModel.setAccountNumber(setAccountNumber());

            HttpEntity<BmsfedexModel> request = new HttpEntity<>(bmsfedexModel);

            JSONObject jsonObject= new JSONObject();
            JSONObject jsonObjectRequestedShipment= new JSONObject();
            jsonObjectRequestedShipment.put("shipper",bmsfedexModel.getRequestedShipment().getShipper());
            jsonObject.put("requestedShipment",jsonObjectRequestedShipment);
            JSONObject jsonObjectShipper = new JSONObject();
            jsonObjectShipper.put("streetLines",bmsfedexModel.getRequestedShipment().getShipper().getRepoAddress().getStreetLines());
            jsonObjectShipper.put("city",bmsfedexModel.getRequestedShipment().getShipper().getRepoAddress().getCity());
            jsonObjectShipper.put("postalCode",bmsfedexModel.getRequestedShipment().getShipper().getRepoAddress().getPostalCode());
            jsonObjectShipper.put("countryCode",bmsfedexModel.getRequestedShipment().getShipper().getRepoAddress().getCountryCode());
            jsonObject.put("address",jsonObjectShipper);
            jsonObjectShipper.put("personName",bmsfedexModel.getRequestedShipment().getShipper().getRepoContact().getPersonName());
            jsonObjectShipper.put("emailAddress",bmsfedexModel.getRequestedShipment().getShipper().getRepoContact().getEmailAddress());
            jsonObjectShipper.put("phoneNumber",bmsfedexModel.getRequestedShipment().getShipper().getRepoContact().getPhoneNumber());
            jsonObjectShipper.put("companyName",bmsfedexModel.getRequestedShipment().getShipper().getRepoContact().getCompanyName());
            jsonObject.put("contact",jsonObjectShipper);


            logger.info("Payload for fedex------------->"+jsonObject.toString());




            HttpEntity<String> entity = new HttpEntity<String>(getHttpHeaders());
            if(entity== null){
                logger.info("entered entity");
            }
            else {
                logger.info("success");
            }
        }
    }

    private List<BmsKitRequest> findKitRequest(){
        List<BmsKitRequest> nBmsKitRequest= bmsFedexRepository.findKitRequest(Long.valueOf(biomsKitRequestID));
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
      List<BmsKitRequest> allOpenRequests= bmsFedexRepository.findAllOpenRequests(81,"In Process");
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
        Long lastRunTimestamp = fedexForSyncRepository.getLastRunTimestamp().getLastRunTimeStamp();
        java.sql.Timestamp one = new java.sql.Timestamp(lastRunTimestamp);
        logger.info("forward sync last run time stamp = {}", Instant.ofEpochMilli(lastRunTimestamp).atZone(ZoneId.systemDefault()).toLocalDate());
        return one;
    }

     // Updates Last run time Stamp in Mayo Sync table forward sync.
     private void updateLastRunTimeFwd(long dbEndDate) {
        logger.info("forward sync updated last run time stamp = {}",dbEndDate);
        fedexForSyncRepository.updateLastRunTimestamp(dbEndDate);
    }


       // Find Site Name
    private String findSiteByID(Long siteId) {
        return siteRepository.findSiteById(siteId);
    }

    private Address setAddress(BmsKitRequest bmsKitRequest) {
        Address address = new Address();
        String country = bmsKitRequest.getCountry();
        address.setStreetLines(bmsKitRequest.getAddress1());
        address.setCity(bmsKitRequest.getCity());
        address.setPostalCode(bmsKitRequest.getPostalCode());
        if("United States".equalsIgnoreCase(country)){
            address.setCountryCode("US");
        }else if ("Canada".equalsIgnoreCase(country)) {
            address.setCountryCode("CA");
        }
        return address;
    }

        private RepoAddress setRepoAddress(BmsKitRequest bmsKitRequest) {
            RepoAddress address = new RepoAddress();
            address.setStreetLines("425 S Euclid, Room 5120,");
            address.setCity("St Louis");
            address.setPostalCode("63110");
            address.setCountryCode("US");
            return address;
            }

    private Contact setContact(BmsKitRequest bmsKitRequest) {
        Contact contact = new Contact();
        String fullName = bmsKitRequest.getReqFirstName() + " " + bmsKitRequest.getReqLastName();
        contact.setPersonName(fullName);
        contact.setEmailAddress(bmsKitRequest.getRequesterEmail());
        contact.setPhoneNumber(bmsKitRequest.getRequesterPhoneNumber());
        contact.setCompanyName(findSiteByID(bmsKitRequest.getRequestorSiteId()));
        return contact;
    }

        private RepoContact setRepoContact(BmsKitRequest bmsKitRequest) {
            RepoContact contact = new RepoContact();
            contact.setPersonName("Laura Granderson");
            contact.setEmailAddress("tbank@wudosis.wustl.edu");
            contact.setPhoneNumber("(314)454-7615");
            contact.setCompanyName("Alliance Biorepository at Washington University in St. Louis");
            return contact;
        }


     private ShippingChargesPayment setShippingChargesPayment(BmsKitRequest bmsKitRequest){
         ShippingChargesPayment shippingChargesPayment = new ShippingChargesPayment();
         shippingChargesPayment.setPaymentType("SENDER");
     }


     private LabelSpecification setLabelSpecification(BmsKitRequest bmsKitRequest){
         LabelSpecification labelSpecification = new LabelSpecification();
         labelSpecification.setLabelStockType("PAPER_4X6");
         labelSpecification.setImageType("PDF");
     }

     private Weight setWeight(BmsKitRequest bmsKitRequest){
         Weight weight = new Weight();
         weight.setUnits("LB");
         weight.setValue(3);
     }

    private RequestedShipment setRequestedShipment(BmsKitRequest bmsKitRequest) {
        RequestedShipment requestedShipment = new RequestedShipment();
        Shipper shipper = new Shipper();
        Recipients recipients = new Recipients();
        RequestedPackageLineItems RequestedPackageLineItems = new RequestedPackageLineItems();
        ShippingChargesPayment shippingChargesPayment = new ShippingChargesPayment();
        LabelSpecification labelSpecification = new LabelSpecification();
        RequestedPackageLineItems requestedPackageLineItems = new RequestedPackageLineItems();

        // Setting shipper(repo) address and contact
        shipper.setRepoAddress(setRepoAddress(bmsKitRequest));
        shipper.setRepoContact(setRepoContact(bmsKitRequest));

        //Setting recipients(collection-site) address an contact
        recipients.setAddress(setAddress(bmsKitRequest));
        recipients.setContact(setContact(bmsKitRequest));

        //Setting pickup type
        requestedShipment.setPickupType("USE_SCHEDULED_PICKUP");

        //Setting service type
        String shippingOption = bmsKitRequest.getShippingOption();
        String country = bmsKitRequest.getCountry();
        if ("United States".equalsIgnoreCase(country)) {
            if ("Regular (within 10 business days from today)".equalsIgnoreCase(shippingOption)) {
                requestedShipment.setServiceType("FEDEX_EXPRESS_SAVER");
            } else if ("Expedited via FedEx (within 1-2 business days from today)".equalsIgnoreCase(shippingOption)) {
                requestedShipment.setServiceType("PRIORITY_OVERNIGHT");
            }
        } else if ("Canada".equalsIgnoreCase(country)) {
            requestedShipment.setServiceType("FEDEX_INTERNATIONAL_PRIORITY");
        }

        // Setting packaging type
        requestedShipment.setPackagingType("YOUR_PACKAGING");

        //Setting Shipping Charges Payment
        requestedShipment.setShippingChargesPayment(setShippingChargesPayment(bmsKitRequest));

        //Setting label specification
        requestedShipment.setLabelSpecification(setLabelSpecification(bmsKitRequest));

        //Setting the weight of the package
        requestedPackageLineItems.setWeight(setWeight(bmsKitRequest));

    }


    private AccountNumber setAccountNumber(){
        AccountNumber accountNumber = new AccountNumber();
        accountNumber.setValue("740561073");
    }
}
