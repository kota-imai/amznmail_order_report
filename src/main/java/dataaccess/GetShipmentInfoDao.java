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
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;

import config.SystemConfig;
import util.UtilityTools;

public class GetShipmentInfoDao {

    static AmazonDynamoDB dynamoDB;

    // Initializer
    private static void init() throws Exception {
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

    public static List<Map<String, AttributeValue>> getInfo(String sellerid) throws Exception {
    	List<Map<String, AttributeValue>> items = new ArrayList<Map<String, AttributeValue>>();
        init(); // Initialize
        try {
            String tableName = "Ship";

            HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
            Condition condition1 = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue(sellerid));
            Condition condition2 = new Condition()
                	.withComparisonOperator(ComparisonOperator.EQ.toString())
                	.withAttributeValueList(new AttributeValue().withN("0"));
            scanFilter.put("seller_id", condition1);
            scanFilter.put("shipped_mail_created", condition2);
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
    
    public static List<Map<String, AttributeValue>> getInfoThanx(String sellerid) throws Exception {
    	List<Map<String, AttributeValue>> items = new ArrayList<Map<String, AttributeValue>>();
        init();
        try {
            String tableName = "Ship";
            HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
            Condition condition1 = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue(sellerid));
            // 2 days past from arrival date sort
//            String timeStamp = new UtilityTools().getTimeStampTwoDaysAgo();
//            System.out.println(timeStamp);
//            Condition condition2 = new Condition()
//                    .withComparisonOperator(ComparisonOperator.BEGINS_WITH.toString())
//                    .withAttributeValueList(new AttributeValue(timeStamp));
            Condition condition3 = new Condition()
                	.withComparisonOperator(ComparisonOperator.EQ.toString())
                	.withAttributeValueList(new AttributeValue().withN("0"));
            scanFilter.put("seller_id", condition1);
//            scanFilter.put("estimated_arrival_date", condition2);
            scanFilter.put("thanks_mail_created", condition3);
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
}