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
import config.SystemConfig;

public class RequestReportDao {

	static AmazonDynamoDB dynamoDB;

	// Initializer
	private static void init() throws Exception {
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (C:\\Users\\kima0\\.aws\\credentials), and is in valid format.", e);
		}
		dynamoDB = AmazonDynamoDBClientBuilder.standard().withCredentials(credentialsProvider)
				.withRegion(SystemConfig.getRegionProd()).build();
	}

	public static void saveReportId(String requestId, String sellerId, String startDate, String endDate,
			String submitDate) throws Exception {
		String tableName = "RequestId";
		init(); // Initialize
		try {
			Map<String, AttributeValue> item = newItem(requestId, sellerId, startDate, endDate, submitDate);
			PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
			dynamoDB.putItem(putItemRequest);
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

	private static Map<String, AttributeValue> newItem(String requestId, String sellerId, String startDate,
			String endDate, String submitDate) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		if (!requestId.isEmpty()) {
			item.put("ReportRequestId", new AttributeValue(requestId));
		}
		if (!sellerId.isEmpty()) {
			item.put("SellerId", new AttributeValue(sellerId));
		}
		if (!startDate.isEmpty()) {
			item.put("StartDate", new AttributeValue(startDate));
		}
		if (!endDate.isEmpty()) {
			item.put("EndDate", new AttributeValue(endDate));
		}
		if (!startDate.isEmpty()) {
			item.put("SubmitDate", new AttributeValue(submitDate));
		}
		// GeneratedFlgは初期値0（レポートID未作成）
		int flg = 0;
		item.put("GeneratedFlg", new AttributeValue().withN(Integer.toString(flg)));

		return item;
	}
}
