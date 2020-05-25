package dataaccess;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;

import config.MysqlConfig;
import config.SystemConfig;

/**
 * This sample demonstrates how to perform a few simple operations with the
 * Amazon DynamoDB service.
 */
public class CreateMessageDao {

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
            .withRegion(SystemConfig.getRegionProd())//Sydney(test)
            .build();
    }

    public List<Map<String, AttributeValue>> getTemplate(String sellerid) throws Exception {
    	List<Map<String, AttributeValue>> items = new ArrayList<Map<String, AttributeValue>>();
        init();
        try {
            String tableName = "Template";

            // Scan items for movies with a year attribute greater than 1985
            HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
            Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue(sellerid));
            scanFilter.put("UserId", condition);
            ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
            ScanResult scanResult = dynamoDB.scan(scanRequest);
            items = scanResult.getItems();

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
		return items;
    }
    
    public void saveMessage(
    		String orderId,
    		String sellerId,
    		String configSet,
    		String from,
    		String to,
    		String subject,
    		String HTML,
    		String FLAT,
    		String kbn
    		) throws Exception {
    	init();
    	String tableName = "Mail";
    	try {
    		// Add an item
    		Map<String, AttributeValue> item = newItem(orderId,sellerId,configSet,from,to,subject,HTML,FLAT,kbn);
    		PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
    		PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
    		System.out.println("Result: " + putItemResult);

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
    
    public void saveThanksMessage(
    		String orderId,
    		String sellerId,
    		String kbn,
    		String productName,
    		String itemQuantity,
    		String to,
    		String from,
    		String arrival
    		) throws Exception {

        final String SQL = "insert into MailThanks"
        						+ "( OrderId, SellerId, Kbn, ProductName, ItemQuantity, To_Email, From_Email, EstimatedArrivalDate )"
        						+ "values(" 
        						+ EncloseWithSingleQuote(orderId) + ","
        						+ EncloseWithSingleQuote(sellerId) + ","
        						+ EncloseWithSingleQuote(kbn) + ","
        						+ EncloseWithSingleQuote(productName) + ","
        						+ itemQuantity + ","
        						+ EncloseWithSingleQuote(to) + ","
        						+ EncloseWithSingleQuote(from) + ","
        						+ FormatArrivalDate(arrival) + ")";
        Connection conn = null;
        PreparedStatement ps = null;
        MysqlConfig conf = new MysqlConfig();
        try{
        	conn = DriverManager.getConnection(conf.URL, conf.USER, conf.PASS);
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(SQL);
            ps.executeUpdate();
            conn.commit();
        } catch (SQLIntegrityConstraintViolationException de) {//already exists
        	conn.rollback();
        	System.out.println("There is the record in MailThanks table / OrderId = " + orderId );
        	return;
        } catch (Exception e) {
        	conn.rollback();
        	System.out.println("***failed to Insert MailThanks : orderid = " + orderId + " ***");
        	e.printStackTrace();
        	throw new Exception();
        }finally {
        	if (ps != null) {
        		ps.close();
        	}
        	if (conn != null) {
        		conn.close();
        	}
        }
    }
    
    public void updateThanksCreatedFlg(String OrderId) throws Exception {
        init();
        try {
            String tableName = "Ship";
            
            // Update an item
            Map<String, AttributeValueUpdate> item = new HashMap<String, AttributeValueUpdate>();
            Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            int already_generated = 1;
            key.put("amazon_order_id", new AttributeValue().withS(OrderId));
            item.put("thanks_mail_created", 
                new AttributeValueUpdate()
                    .withAction(AttributeAction.PUT)
                    .withValue(new AttributeValue().withN(Integer.toString(already_generated))));
            UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(tableName)
                .withKey(key)
                .withAttributeUpdates(item);
            UpdateItemResult result = dynamoDB.updateItem(updateItemRequest);

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
    
    private static Map<String, AttributeValue> newItem(
    		String orderId,
    		String sellerId,
    		String configSet,
    		String from,
    		String to,
    		String subject,
    		String HTML,
    		String FLAT,
    		String kbn
    		) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("OrderId", new AttributeValue(orderId));
        item.put("SellerId", new AttributeValue(sellerId));
        item.put("ConfigSet", new AttributeValue(configSet));
        item.put("From", new AttributeValue(from));
        item.put("To", new AttributeValue(to));
        item.put("Subject", new AttributeValue(subject));
        item.put("HTML", new AttributeValue(HTML));
        item.put("FLAT", new AttributeValue(FLAT));
        item.put("Kbn", new AttributeValue(kbn));
        //SentFlgは初期値0（未送信）にする
        int sent = 0;
        item.put("Sent", new AttributeValue().withN(Integer.toString(sent)));
        
        return item;
    }
	
	private String EncloseWithSingleQuote(String str) {
		return "'" + str + "'";
	}
	
	private String FormatArrivalDate(String str) {
		return "'" + str.substring(0,19).replace("T"," ") + "'";
	}
}
