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
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.simple.JSONObject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import javax.mail.MessagingException;
import java.io.IOException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

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
    private BmsidFedexRepository bmsidFedexRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    EmailService emailService;


    // from config properties file
    @Value("${fedex_create_url}")
    private String fedex_create_url;
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
        logger.info("In Create Fedex Request Service-->" + fedex_create_url);
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
        String formattedDate = dateFormat.format(currentTime);
        //This is in a loop and it processes all the requests one by one inside this for loop.
        for (BmsKitRequest bmsKitRequest : bmsKitRequests) {
            BmsfedexModel bmsfedexModel = new BmsfedexModel();
            bmsfedexModel.setRequestedShipment(setRequestedShipment(bmsKitRequest));
            bmsfedexModel.setLabelResponseOptions("URL_ONLY");
            bmsfedexModel.setAccountNumber(setAccountNumber(bmsKitRequest).getValue());

            HttpEntity<BmsfedexModel> request = new HttpEntity<>(bmsfedexModel);

            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObjectRequestedShipment = new JSONObject();
            JSONObject jsonObjectShipper = new JSONObject();
            JSONObject jsonRObjectAddress = new JSONObject();
            JSONObject jsonRObjectContact = new JSONObject();
            JSONObject jsonObjectRecipients = new JSONObject();
            JSONObject jsonObjectAddress = new JSONObject();
            JSONObject jsonObjectContact = new JSONObject();
            JSONObject jsonObjectShippingChargesPayment = new JSONObject();
            JSONObject jsonObjectLabelSpecification = new JSONObject();
            JSONObject jsonObjectRequestedPackageLineItems = new JSONObject();
            JSONObject jsonObjectWeight = new JSONObject();
            JSONArray jsonArrayCustomerReferences = new JSONArray();
            JSONObject jsonObjectCustomerReferences = new JSONObject();
            JSONObject jsonObjectValue = new JSONObject();
            JSONObject jsonObjectResponsibleParty = new JSONObject();
            JSONObject jsonObjectPayor = new JSONObject();


            List<String> repostreetLines = bmsfedexModel.getRequestedShipment().getShipper().getRepoAddress().getStreetLines();
            // Create a JSONArray for streetLines
            JSONArray repostreetLinesArray = new JSONArray();
            for (String streetLine : repostreetLines) {
                repostreetLinesArray.add(streetLine);
            }
            jsonRObjectAddress.put("streetLines", repostreetLinesArray);
            jsonRObjectAddress.put("city", bmsfedexModel.getRequestedShipment().getShipper().getRepoAddress().getCity());
            jsonRObjectAddress.put("stateOrProvinceCode", bmsfedexModel.getRequestedShipment().getShipper().getRepoAddress().getStateOrProvinceCode());
            jsonRObjectAddress.put("postalCode", bmsfedexModel.getRequestedShipment().getShipper().getRepoAddress().getPostalCode());
            jsonRObjectAddress.put("countryCode", bmsfedexModel.getRequestedShipment().getShipper().getRepoAddress().getCountryCode());

            jsonRObjectContact.put("personName", bmsfedexModel.getRequestedShipment().getShipper().getRepoContact().getPersonName());
            jsonRObjectContact.put("emailAddress", bmsfedexModel.getRequestedShipment().getShipper().getRepoContact().getEmailAddress());
            jsonRObjectContact.put("phoneNumber", bmsfedexModel.getRequestedShipment().getShipper().getRepoContact().getPhoneNumber());

            for (Recipients recipient : bmsfedexModel.getRequestedShipment().getRecipients()) {
                List<String> streetLines = recipient.getAddress().getStreetLines();

                // Create a JSONArray for streetLines
                JSONArray streetLinesArray = new JSONArray();
                for (String streetLine : streetLines) {
                    streetLinesArray.add(streetLine);
                }

                jsonObjectAddress.put("streetLines", streetLinesArray);
                jsonObjectAddress.put("city", recipient.getAddress().getCity());
                jsonObjectAddress.put("stateOrProvinceCode", recipient.getAddress().getStateOrProvinceCode());
                jsonObjectAddress.put("postalCode", recipient.getAddress().getPostalCode());
                jsonObjectAddress.put("countryCode", recipient.getAddress().getCountryCode());

                jsonObjectContact.put("personName", recipient.getContact().getPersonName());
                jsonObjectContact.put("emailAddress", recipient.getContact().getEmailAddress());
                jsonObjectContact.put("phoneNumber", recipient.getContact().getPhoneNumber());
            }

            for (RequestedPackageLineItems packageItem : bmsfedexModel.getRequestedShipment().getRequestedPackageLineItems()) {
                jsonObjectWeight.put("units", packageItem.getWeight().getUnits());
                jsonObjectWeight.put("value", packageItem.getWeight().getValue());
                List<CustomerReferences> customerReferencesList = packageItem.getCustomerReferences();
                for (CustomerReferences customerReferences : customerReferencesList) {
                    jsonObjectCustomerReferences.put("customerReferenceType", customerReferences.getCustomerReferenceType());
                    jsonObjectCustomerReferences.put("value", customerReferences.getValue());
                    jsonArrayCustomerReferences.add(jsonObjectCustomerReferences);
                }
                jsonObjectRequestedPackageLineItems.put("weight", jsonObjectWeight);
                jsonObjectRequestedPackageLineItems.put("customerReferences", jsonArrayCustomerReferences);
                jsonObjectRequestedPackageLineItems.put("groupPackageCount", packageItem.getGroupPackageCount());
            }

            jsonObjectValue.put("value", bmsfedexModel.getAccountNumber());

            jsonObjectShipper.put("address", jsonRObjectAddress);
            jsonObjectShipper.put("contact", jsonRObjectContact);
            jsonObjectRecipients.put("address", jsonObjectAddress);
            jsonObjectRecipients.put("contact", jsonObjectContact);
            JSONArray recipientShipArray = new JSONArray();
            recipientShipArray.add(jsonObjectRecipients);
            JSONArray requestedPackageLineItemsArray = new JSONArray();
            requestedPackageLineItemsArray.add(jsonObjectRequestedPackageLineItems);
            jsonObjectResponsibleParty.put("accountNumber",jsonObjectValue);
            jsonObjectPayor.put("responsibleParty",jsonObjectResponsibleParty);
            jsonObjectShippingChargesPayment.put("paymentType", bmsfedexModel.getRequestedShipment().getShippingChargesPayment().getPaymentType());
            jsonObjectShippingChargesPayment.put("payor", jsonObjectPayor);
            jsonObjectLabelSpecification.put("labelStockType", bmsfedexModel.getRequestedShipment().getLabelSpecification().getLabelStockType());
            jsonObjectLabelSpecification.put("imageType", bmsfedexModel.getRequestedShipment().getLabelSpecification().getImageType());
            jsonObjectRequestedShipment.put("shipper", jsonObjectShipper);
            jsonObjectRequestedShipment.put("recipients", recipientShipArray);
            jsonObjectRequestedShipment.put("pickupType", bmsfedexModel.getRequestedShipment().getPickupType());
            jsonObjectRequestedShipment.put("serviceType", bmsfedexModel.getRequestedShipment().getServiceType());
            jsonObjectRequestedShipment.put("packagingType", bmsfedexModel.getRequestedShipment().getPackagingType());
            jsonObjectRequestedShipment.put("shippingChargesPayment", jsonObjectShippingChargesPayment);
            jsonObjectRequestedShipment.put("labelSpecification", jsonObjectLabelSpecification);
            jsonObjectRequestedShipment.put("requestedPackageLineItems", requestedPackageLineItemsArray);
            jsonObject.put("requestedShipment", jsonObjectRequestedShipment);

            jsonObject.put("labelResponseOptions", bmsfedexModel.getLabelResponseOptions());

            jsonObject.put("accountNumber", jsonObjectValue);

            logger.info("Payload for fedex------------->" + jsonObject.toString());


            HttpEntity<String> entity = new HttpEntity<String>(jsonObject.toString(), getHttpHeaders());
            if (entity == null) {
                logger.info("entered entity");
            } else {
                logger.info("----------entity----------->" + entity);
                String createfedexUrl = this.fedex_create_url;
                UriComponentsBuilder URL = UriComponentsBuilder.fromHttpUrl(createfedexUrl)
                        .queryParam("param", jsonObject);
                ;
                logger.info(URL.toUriString() + "--------------->This is the create fedex url");
                try {
                    logger.info("----------inside create fedex response try-------->");
                    //ResponseEntity<BmsFedexResponse> response = this.restTemplate.exchange(URL.build().toUri(), HttpMethod.POST, entity, BmsFedexResponse.class);
                    ResponseEntity<BmsFedexResponse> response = this.restTemplate.exchange(createfedexUrl, HttpMethod.POST, entity, BmsFedexResponse.class);
                    logger.info("----------create fedex response-------->" + response);

                    if (response.getStatusCode() == HttpStatus.OK) {

                        Output output = response.getBody().getOutput();
                        List<TransactionShipments> transactionShipmentsList = output.getTransactionShipments();

                        for (TransactionShipments transactionShipments : transactionShipmentsList) {
                            String masterTrackNumber = transactionShipments.getMasterTrackingNumber();
                            List<PieceResponses> pieceResponsesList = transactionShipments.getPieceResponses();
                            for (PieceResponses pieceResponse : pieceResponsesList) {
                                List<PackageDocuments> packageDocumentsList = pieceResponse.getPackageDocuments();

                                for (PackageDocuments packageDocument : packageDocumentsList) {
                                    String url = packageDocument.getUrl();
                                    bmsFedexRepository.updateShippingLabel(url,masterTrackNumber,bmsKitRequest.getId());
                                }
                            }
                        }
                        String kStatus = "Ready to Ship";
                        bmsFedexRepository.updateKitStatus(kStatus,bmsKitRequest.getId());
                        logger.info("-------------kit id------------->" + bmsKitRequest.getId());
                    }
                }
            /**    catch (HttpClientErrorException.BadRequest ex) {
                    logger.info("----------inside create fedex response catch-------->");
                        // Handle bad request exception
                        String responseBody = ex.getResponseBodyAsString();
                        if (responseBody != null && !responseBody.isEmpty()) {
                            logger.info("---------- inside BadRequest----------->");
                            ObjectMapper objectMapper = new ObjectMapper();
                            try {
                                ErrorDetails errorDetails = objectMapper.readValue(responseBody, ErrorDetails.class);
                                logger.info("---------- errorResponse----------->"+errorDetails);

                                if (errorDetails.getTransactionId() != null) {
                                    errorDetails.setTransactionId(errorDetails.getTransactionId());
                                }

                                if (errorDetails.getCustomerTransactionId() != null) {
                                    errorDetails.setCustomerTransactionId(errorDetails.getCustomerTransactionId());
                                }

                                if (errorDetails.getErrors() != null) {
                                    List<Errors> errorList = new ArrayList<>();

                                    for (Errors errorObj : errorDetails.getErrors()) {
                                        Errors error = new Errors();
                                        // Extract error properties from errorObj and add to errorList
                                        error.setCode(errorObj.getCode());
                                        error.setMessage(errorObj.getMessage());

                                        List<Parameter> parameterList = new ArrayList<>();
                                        for (Parameter parameterObj : errorObj.getParameterList()) {
                                            Parameter parameter = new Parameter();
                                            parameter.setKey(parameterObj.getKey());
                                            parameter.setValue(parameterObj.getValue());
                                            parameterList.add(parameter);
                                        }

                                        error.setParameterList(parameterList);
                                        errorList.add(error);
                                    }
                                    errorDetails.setErrors(errorList);
                                }
                            } catch (IOException e) {
                                logger.info("Failed to map error response: {}", e.getMessage());
                            }
                        }
                } **/

                catch (Exception ex) {
                    logger.info("Create fedex request Failed with reason = {}", ex.getMessage());
                    String kStatus = "Create shipment failed";
                    bmsFedexRepository.updateKitStatus(kStatus, bmsKitRequest.getId());
                    if ("Regular (within 10 business days from today)".equalsIgnoreCase(bmsKitRequest.getShippingOption())) {
                        String kError = "Create FedEx Shipment Failed. Please verify the shipping address";
                        bmsFedexRepository.updateErrorStatus(kError, bmsKitRequest.getId());
                        emailService.sendSimpleEmail("alliancedevelopment@email.wustl.edu", "Alliance-Fedex Integration Create Shipment Request failed", "Create Shipment Failed. Please verify the shipping address for kit request ID: " + bmsKitRequest.getId() + ".Shipping address mentioned: " + bmsKitRequest.getRequesterAddress());
                    } else if ("Expedited via FedEx (within 1-2 business days from today)".equalsIgnoreCase(bmsKitRequest.getShippingOption())) {
                        String kError = "Create FedEx Shipment Failed. Please verify the shipping address / FedEx account number";
                        bmsFedexRepository.updateErrorStatus(kError, bmsKitRequest.getId());
                        emailService.sendSimpleEmail("alliancedevelopment@email.wustl.edu", "Alliance-Fedex Integration Create Shipment Request failed", "Create Shipment Failed. Please verify the shipping address / FedEx account number for kit request ID: " + bmsKitRequest.getId() + ". Shipping address mentioned: " + bmsKitRequest.getRequesterAddress() + ". Shipping account number: " + bmsKitRequest.getRequesterFedexNo());
                    }
                }
            }
        }
    }

    // Sets Headers for the Request
    private HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-locale","en_US");
        if (this.getAccessToken().length() > 0) {
            headers.set("authorization", "Bearer "+this.getAccessToken());
        } else {
            logger.info(this.getAccessToken()+"------------->access token");
            logger.info("Unable to get access token and will rerun in 1 hour!");
        }
        return headers;
    }

    // Gets all Open Requests in a time interval from BioMS DB
    private List<BmsKitRequest> findAllOPenKitRequests(){
        logger.info("Finding all WashU In process kit requests to process ");
      List<BmsKitRequest> allOpenRequests= bmsFedexRepository.findAllOpenRequests(81,"In Process","Regular (within 10 business days from today)", "Expedited via FedEx (within 1-2 business days from today)", "United States");
      logger.info("Number of WashU In process kit = {}",allOpenRequests.size());
      return allOpenRequests;
    }

    // Gets the Bearer token from Mayo Authentication service
    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("content-type","application/x-www-form-urlencoded");
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
        List<String> streetLines = new ArrayList<>();
        String country = bmsKitRequest.getCountry();
        streetLines.add(bmsKitRequest.getAddress1());
        address.setStreetLines(streetLines);
        address.setCity(bmsKitRequest.getCity());
        String state = bmsKitRequest.getState().toLowerCase();
        Map<String, String> stateNameToCodeMap = new HashMap<>();
// Load US State Names.
        stateNameToCodeMap.put("alabama","AL");
        stateNameToCodeMap.put("alaska","AK");
        stateNameToCodeMap.put("arizona","AZ");
        stateNameToCodeMap.put("arkansas","AR");
        stateNameToCodeMap.put("california","CA");
        stateNameToCodeMap.put("colorado","CO");
        stateNameToCodeMap.put("connecticut","CT");
        stateNameToCodeMap.put("delaware","DE");
        stateNameToCodeMap.put("district of columbia","DC");
        stateNameToCodeMap.put("florida","FL");
        stateNameToCodeMap.put("georgia","GA");
        stateNameToCodeMap.put("hawaii","HI");
        stateNameToCodeMap.put("idaho","ID");
        stateNameToCodeMap.put("illinois","IL");
        stateNameToCodeMap.put("indiana","IN");
        stateNameToCodeMap.put("iowa","IA");
        stateNameToCodeMap.put("kansas","KS");
        stateNameToCodeMap.put("kentucky","KY");
        stateNameToCodeMap.put("louisiana","LA");
        stateNameToCodeMap.put("maine","ME");
        stateNameToCodeMap.put("maryland","MD");
        stateNameToCodeMap.put("massachusetts","MA");
        stateNameToCodeMap.put("michigan","MI");
        stateNameToCodeMap.put("minnesota","MN");
        stateNameToCodeMap.put("mississippi","MS");
        stateNameToCodeMap.put("missouri","MO");
        stateNameToCodeMap.put("montana","MT");
        stateNameToCodeMap.put("nebraska","NE");
        stateNameToCodeMap.put("nevada","NV");
        stateNameToCodeMap.put("new hampshire","NH");
        stateNameToCodeMap.put("new jersey","NJ");
        stateNameToCodeMap.put("new mexico","NM");
        stateNameToCodeMap.put("new york","NY");
        stateNameToCodeMap.put("north carolina","NC");
        stateNameToCodeMap.put("north dakota","ND");
        stateNameToCodeMap.put("ohio","OH");
        stateNameToCodeMap.put("oklahoma","OK");
        stateNameToCodeMap.put("oregon","OR");
        stateNameToCodeMap.put("pennsylvania","PA");
        stateNameToCodeMap.put("rhode island","RI");
        stateNameToCodeMap.put("south carolina","SC");
        stateNameToCodeMap.put("south dakota","SD");
        stateNameToCodeMap.put("tennessee","TN");
        stateNameToCodeMap.put("texas","TX");
        stateNameToCodeMap.put("utah","UT");
        stateNameToCodeMap.put("vermont","VT");
        stateNameToCodeMap.put("virginia","VA");
        stateNameToCodeMap.put("washington","WA");
        stateNameToCodeMap.put("west virginia","WV");
        stateNameToCodeMap.put("wisconsin","WI");
        stateNameToCodeMap.put("wyoming","WY");
        stateNameToCodeMap.put("guam", "GU");
        stateNameToCodeMap.put("puerto rico","PR");
        stateNameToCodeMap.put("virgin islands","VI");
        stateNameToCodeMap.put("armed forces (ae)","AE");
        stateNameToCodeMap.put("armed forces americas","AA");
        stateNameToCodeMap.put("armed forces pacific","AP");


        // Load Canada State Names.
        stateNameToCodeMap.put("alberta","AB");
        stateNameToCodeMap.put("british columbia","BC");
        stateNameToCodeMap.put("manitoba","MB");
        stateNameToCodeMap.put("new brunswick","NB");
        stateNameToCodeMap.put("newfoundland and labrador","NF");
        stateNameToCodeMap.put("northwest territories","NT");
        stateNameToCodeMap.put("nova scotia","NS");
        stateNameToCodeMap.put("nunavut","NU");
        stateNameToCodeMap.put("ontario","ON");
        stateNameToCodeMap.put("prince edward island","PE");
        stateNameToCodeMap.put("quebec","QC");
        stateNameToCodeMap.put("saskatchewan","SK");
        stateNameToCodeMap.put("yukon territory","YT");

        if (stateNameToCodeMap.containsKey(state)) {
            address.setStateOrProvinceCode(stateNameToCodeMap.get(state));
        } else {
            address.setStateOrProvinceCode(bmsKitRequest.getState());
        }
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
            List<String> streetLines = new ArrayList<>();
            streetLines.add("425 S Euclid, Room 5120");
            address.setStreetLines(streetLines);
            address.setCity("St Louis");
            address.setStateOrProvinceCode("MO");
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
        return contact;
    }

        private RepoContact setRepoContact(BmsKitRequest bmsKitRequest) {
            RepoContact contact = new RepoContact();
            contact.setPersonName("Laura Granderson");
            contact.setEmailAddress("tbank@wudosis.wustl.edu");
            contact.setPhoneNumber("(314)454-7615");
            return contact;
        }


     private ShippingChargesPayment setShippingChargesPayment(BmsKitRequest bmsKitRequest){
         ShippingChargesPayment shippingChargesPayment = new ShippingChargesPayment();
         String shippingOption = bmsKitRequest.getShippingOption();
             if ("Regular (within 10 business days from today)".equalsIgnoreCase(shippingOption)) {
                 shippingChargesPayment.setPaymentType("SENDER");
             } else if ("Expedited via FedEx (within 1-2 business days from today)".equalsIgnoreCase(shippingOption)) {
                 shippingChargesPayment.setPaymentType("RECIPIENT");
                 shippingChargesPayment.setPayor(setPayor(bmsKitRequest));
             }
         return shippingChargesPayment;
     }

     private Payor setPayor(BmsKitRequest bmsKitRequest){
         Payor payor = new Payor();
         payor.setResponsibleParty(setResponsibleParty(bmsKitRequest));
        return payor;
     }

     private ResponsibleParty setResponsibleParty(BmsKitRequest bmsKitRequest){
         ResponsibleParty responsibleParty = new ResponsibleParty();
         responsibleParty.setAccountNumber(setAccountNumber(bmsKitRequest));
         return responsibleParty;
     }

     private LabelSpecification setLabelSpecification(BmsKitRequest bmsKitRequest){
         LabelSpecification labelSpecification = new LabelSpecification();
         labelSpecification.setLabelStockType("STOCK_4X6");
         labelSpecification.setImageType("PDF");
         return labelSpecification;
     }

     private Weight setWeight(BmsKitRequest bmsKitRequest){
         Weight weight = new Weight();
         weight.setUnits("LB");
         weight.setValue(3);
         return weight;
     }

    private CustomerReferences setCustomerReferences(BmsKitRequest bmsKitRequest){
        CustomerReferences customerReferences = new CustomerReferences();
        customerReferences.setCustomerReferenceType("CUSTOMER_REFERENCE");
        String shortTitle = bmsKitRequest.getShortTitle();
        String kitName = this.getSpecCollKitName(bmsKitRequest.getSpecimenCollKitId());

        String referenceValue = shortTitle + "_" + kitName;
        if (referenceValue.length() > 40) {
            referenceValue = referenceValue.substring(0, 40);
        }

        customerReferences.setValue(referenceValue);
        return customerReferences;
    }

    private RequestedShipment setRequestedShipment(BmsKitRequest bmsKitRequest) {
        RequestedShipment requestedShipment = new RequestedShipment();
        Shipper shipper = new Shipper();
        List<Recipients> recipientList = new ArrayList<>();
        Recipients recipients = new Recipients();
        ShippingChargesPayment shippingChargesPayment = new ShippingChargesPayment();
        LabelSpecification labelSpecification = new LabelSpecification();
        List<RequestedPackageLineItems> packageLineItemList = new ArrayList<>();
        RequestedPackageLineItems requestedPackageLineItems = new RequestedPackageLineItems();
        List<CustomerReferences> customerReferencesList = new ArrayList<>();


        // Setting shipper(repo) address and contact
        shipper.setRepoAddress(setRepoAddress(bmsKitRequest));
        shipper.setRepoContact(setRepoContact(bmsKitRequest));
        requestedShipment.setShipper(shipper);

        //Setting recipients(collection-site) address an contact
        recipients.setAddress(setAddress(bmsKitRequest));
        recipients.setContact(setContact(bmsKitRequest));
        recipientList.add(recipients);
        requestedShipment.setRecipients(recipientList);

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
        requestedPackageLineItems.setGroupPackageCount(bmsKitRequest.getKit_count());
        customerReferencesList.add(setCustomerReferences(bmsKitRequest));
        requestedPackageLineItems.setCustomerReferences(customerReferencesList);
        packageLineItemList.add(requestedPackageLineItems);
        requestedShipment.setRequestedPackageLineItems(packageLineItemList);

        return requestedShipment;
    }

    private AccountNumber setAccountNumber(BmsKitRequest bmsKitRequest){
      AccountNumber accountNumber = new AccountNumber();
        String shippingOption = bmsKitRequest.getShippingOption();
        if ("Regular (within 10 business days from today)".equalsIgnoreCase(shippingOption)) {
            accountNumber.setValue("740561073");
        }else if ("Expedited via FedEx (within 1-2 business days from today)".equalsIgnoreCase(shippingOption)) {
            accountNumber.setValue(bmsKitRequest.getRequesterFedexNo());
        }
        return accountNumber;
    }

    // Find Kit Name
    private String getSpecCollKitName(long kitId) {
        logger.debug("Get Kit name by id = {}",kitId);
        logger.info(bmsidFedexRepository.findBmsKitById(kitId)+"----------->external kit type id");
        return bmsidFedexRepository.findBmsKitById(kitId);

    }
}
