package dataaccess;
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


import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;

import config.SystemConfig;

//import util.ListOrdersXMLToMap;

/**
 * This sample demonstrates how to perform a few simple operations with the
 * Amazon DynamoDB service.
 */
public class RequestReportDao {

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

//    public static void saveRequestId(String requestId, String sellerId, String startDate, String submitDate) throws Exception {
//        init();
//
//        try {
//            String tableName = "RequestId";

            // Create a table with a primary hash key named 'name', which holds a string
//            CreateTableRequest createTableRequest = new CreateTableRequest().withTableName(tableName)
//                .withKeySchema(new KeySchemaElement().withAttributeName("name").withKeyType(KeyType.HASH))
//                .withAttributeDefinitions(new AttributeDefinition().withAttributeName("name").withAttributeType(ScalarAttributeType.S))
//                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L));

            // Create table if it does not exist yet
//            TableUtils.createTableIfNotExists(dynamoDB, createTableRequest);
            // wait for the table to move into ACTIVE state
//            TableUtils.waitUntilActive(dynamoDB, tableName);

            // Describe our new table
//            DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(tableName);
//            TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
//            System.out.println("Table Description: " + tableDescription);
            
//            for (int i = 0 ; i < keyList.size() ; i++) {
//            	String RequestId = keyList.get(i);
//            	String BuyerEmail = xmltomap.get(OrderID, "BuyerEmail");
//            	Integer itemShippedNum = 0;
//            	if (!xmltomap.get(OrderID, "NumberOfItemsShipped").isEmpty()) {
//            		itemShippedNum = Integer.parseInt(xmltomap.get(OrderID, "NumberOfItemsShipped"));
//            	}
//            	String channel = xmltomap.get(OrderID, "FulfillmentChannel");
//            	String purchaseDate = xmltomap.get(OrderID, "PurchaseDate");
            	// Add an item
//            	Map<String, AttributeValue> item = newItem(requestId, sellerId, StartDate);
//            	PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
//            	PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
//            	System.out.println("Saved in DynamoDB : RequestID =" +  requestId);
//            }
                
            // Scan items for movies with a year attribute greater than 1985
//            HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
//            Condition condition = new Condition()
//                .withComparisonOperator(ComparisonOperator.GT.toString())
//                .withAttributeValueList(new AttributeValue().withN("1985"));
//            scanFilter.put("year", condition);
//            ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
//            ScanResult scanResult = dynamoDB.scan(scanRequest);
//            System.out.println("Result: " + scanResult);
////
//        } catch (AmazonServiceException ase) {
//            System.out.println("Caught an AmazonServiceException, which means your request made it "
//                    + "to AWS, but was rejected with an error response for some reason.");
//            System.out.println("Error Message:    " + ase.getMessage());
//            System.out.println("HTTP Status Code: " + ase.getStatusCode());
//            System.out.println("AWS Error Code:   " + ase.getErrorCode());
//            System.out.println("Error Type:       " + ase.getErrorType());
//            System.out.println("Request ID:       " + ase.getRequestId());
//        } catch (AmazonClientException ace) {
//            System.out.println("Caught an AmazonClientException, which means the client encountered "
//                    + "a serious internal problem while trying to communicate with AWS, "
//                    + "such as not being able to access the network.");
//            System.out.println("Error Message: " + ace.getMessage());
//        }
//    }
    
    public static void saveReportId(String requestId, String sellerId, String startDate, String endDate, String submitDate) throws Exception {
    	String tableName = "RequestId";
    	init();
    	try {
    		
    		Map<String, AttributeValue> item = newItem(requestId, sellerId, startDate, endDate, submitDate);
    		PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
    		PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
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

	private static Map<String, AttributeValue> newItem(String requestId, String sellerId, String startDate, String endDate, String submitDate) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		//Trim
		if (!requestId.isEmpty()) {item.put("ReportRequestId", new AttributeValue(requestId));}
//		if (!generatedReportId.isEmpty()) {item.put("GeneratedReportId", new AttributeValue(generatedReportId));}
		if (!sellerId.isEmpty()){item.put("SellerId", new AttributeValue(sellerId));}
		if (!startDate.isEmpty()){item.put("StartDate", new AttributeValue(startDate));}
		if (!endDate.isEmpty()){item.put("EndDate", new AttributeValue(endDate));}
		if (!startDate.isEmpty()){item.put("SubmitDate", new AttributeValue(submitDate));}
        int flg = 0;//Flgは初期値0
        item.put("GeneratedFlg", new AttributeValue().withN(Integer.toString(flg)));
        
		return item;
	}
}
