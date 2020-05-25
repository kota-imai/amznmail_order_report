package dataaccess;
import java.util.ArrayList;


/*
 * Copyright 2012-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;

import config.SystemConfig;
import util.UtilityTools;

//import util.ListOrdersXMLToMap;

/**
 * This sample demonstrates how to perform a few simple operations with the
 * Amazon DynamoDB service.
 */
public class GetReportRequestListDao {

    /*
     * Before running the code:
     *      Fill in your AWS access credentials in the provided credentials
     *      file template, and be sure to move the file to the default location
     *      (C:\\Users\\kima0\\.aws\\credentials) where the sample code will load the
     *      credentials from.
     *      https://console.aws.amazon.com/iam/home?#security_credential
     *
     * WARNING:
     *      To avoid accidental leakage of your credentials, DO NOT keep
     *      the credentials file in your source directory.
     */

    static AmazonDynamoDB dynamoDB;

    /**
     * The only information needed to create a client are security credentials
     * consisting of the AWS Access Key ID and Secret Access Key. All other
     * configuration, such as the service endpoints, are performed
     * automatically. Client parameters, such as proxies, can be specified in an
     * optional ClientConfiguration object when constructing a client.
     *
     * @see com.amazonaws.auth.BasicAWSCredentials
     * @see com.amazonaws.auth.ProfilesConfigFile
     * @see com.amazonaws.ClientConfiguration
     */
    private static void init() throws Exception {
        /*
         * The ProfileCredentialsProvider will return your [default]
         * credential profile by reading from the credentials file located at
         * (C:\\Users\\kima0\\.aws\\credentials).
         */
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
        try {
            credentialsProvider.getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\kima0\\.aws\\credentials), and is in valid format.",
                    e);
        }
        dynamoDB = AmazonDynamoDBClientBuilder.standard()
            .withCredentials(credentialsProvider)
            .withRegion(SystemConfig.getRegionProd())
            .build();
    }
    
    public List<Map<String, AttributeValue>> scanRequestIdWithSellerId(String sellerId) throws Exception {
    	List<Map<String, AttributeValue>> idList = new ArrayList<Map<String, AttributeValue>>();
    	init();
    	try {
            // Scan items
        	String tableName = "RequestId";
        	HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
        	Condition condition1 = new Condition()
        			.withComparisonOperator(ComparisonOperator.EQ.toString())
        			.withAttributeValueList(new AttributeValue(sellerId));
            Condition condition2 = new Condition()
                	.withComparisonOperator(ComparisonOperator.EQ.toString())
                	.withAttributeValueList(new AttributeValue().withN("0"));
        	scanFilter.put("SellerId", condition1);
        	scanFilter.put("GeneratedFlg", condition2);
        	ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
        	ScanResult scanResult = dynamoDB.scan(scanRequest);
        	idList = scanResult.getItems();
//System.out.println(idList);
    	} catch (AmazonServiceException ase) {
    		System.out.println("Caught an AmazonServiceException, which means your request made it "
    				+ "to AWS, but was rejected with an error response for some reason.");
    		System.out.println("Error Message:    " + ase.getMessage());
    		System.out.println("HTTP Status Code: " + ase.getStatusCode());
    		System.out.println("AWS Error Code:   " + ase.getErrorCode());
    		System.out.println("Error Type:       " + ase.getErrorType());
    		System.out.println("Request ID:       " + ase.getRequestId());
    	} catch (AmazonClientException ace) {
    		System.out.println("Caught an AmazonClientException, which means the client encountered "
    				+ "a serious internal problem while trying to communicate with AWS, "
    				+ "such as not being able to access the network.");
    		System.out.println("Error Message: " + ace.getMessage());
    	}
    	return idList;
    }

    public void saveGeneratedId(List<String> ReportIdList, Map<String, Map<String, String>> map, String sellerId) throws Exception {
        init();
        try {
            String tableName = "GeneratedId";
    		for (int i = 0; i < ReportIdList.size(); i++) {
    			//Expand items to get each values
				String requestId = ReportIdList.get(i);
				
				if ("_DONE_".equals(map.get(requestId).get("ReportProcessingStatus"))){
    				String generatedReportId = map.get(requestId).get("GeneratedReportId");
    				String startDate = map.get(requestId).get("StartDate");
    	    		String endDate = map.get(requestId).get("EndDate");
    	    		//and store them in the database
    	    		try {
    	            	Map<String, AttributeValue> item = newItem(requestId, generatedReportId, sellerId, startDate, endDate);
    	                PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
    	                PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
    	                System.out.println("Saved in DynamoDB : RequestID = " +  generatedReportId);
    	            	//reverse generatedFlg
    	            	updateGeneratedFlg(requestId);
    	    		} catch (NullPointerException ne){
    	    			System.out.println("***NO DATA FOUND ReportRequestID = " + requestId);
    	    			ne.printStackTrace();
    	    			return;
    	    		}
    	    		
				} else if ("_DONE_NO_DATA_".equals(map.get(requestId).get("ReportProcessingStatus"))) {
					System.out.println("***NO DATA FOUND ReportRequestID = " + requestId + "***");
					deleteNoDataRequestId(requestId);
				
				} else {
					System.out.println("***INVALID REPORTREQUESTID IS DETECTED! ReportRequestID" + requestId + "***");
				}
    		}
           	
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to AWS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

    private static Map<String, AttributeValue> newItem(String requestId, String generatedReportId, String sellerId, String startDate, String endDate) {
    	Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        //Trim
    	if (!requestId.isEmpty()) {item.put("RequestReportId", new AttributeValue(requestId));}
        if (!generatedReportId.isEmpty()) {item.put("GeneratedReportId", new AttributeValue(generatedReportId));}
        if (!sellerId.isEmpty()){item.put("SellerId", new AttributeValue(sellerId));}
        if (!startDate.isEmpty()){item.put("StartDate", new AttributeValue(startDate));}
        if (!endDate.isEmpty()){item.put("EndDate", new AttributeValue(endDate));}
        int flg = 0;
        item.put("NotIssued", new AttributeValue().withN(Integer.toString(flg)));
        
        return item;
    }

    public void updateGeneratedFlg(String ReportRequestId) throws Exception {
        init();
        try {
            String tableName = "RequestId";
            
            // Update an item
            Map<String, AttributeValueUpdate> item = new HashMap<String, AttributeValueUpdate>();
            Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            int already_generated = 1;
            key.put("ReportRequestId", new AttributeValue().withS(ReportRequestId));
            item.put("GeneratedFlg", 
                new AttributeValueUpdate()
                    .withAction(AttributeAction.PUT)
                    .withValue(new AttributeValue().withN(Integer.toString(already_generated))));
            String timeStamp = new UtilityTools().getCurrentTimeStamp();
            item.put("GeneratedTime",
                    new AttributeValueUpdate()
                    .withAction(AttributeAction.PUT)
                    .withValue(new AttributeValue().withS(timeStamp)));
            UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(tableName)
                .withKey(key)
                .withAttributeUpdates(item);
            UpdateItemResult result = dynamoDB.updateItem(updateItemRequest);
            System.out.println("Result: " + result);

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to AWS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
    
    public void deleteNoDataRequestId(String ReportRequestId) throws Exception {
        init();
        try {
        	String tableName = "RequestId";
        	// delete Item
        	Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
        	key.put("ReportRequestId", new AttributeValue().withS(ReportRequestId));
            DeleteItemRequest deleteItemRequest = new DeleteItemRequest()
                .withTableName(tableName)
                .withKey(key);
            DeleteItemResult result = dynamoDB.deleteItem(deleteItemRequest);
            System.out.println("Result: Item deleted RequestReportId = " + ReportRequestId);
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to AWS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }

}
