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
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;

import config.SystemConfig;
import util.UtilityTools;

public class GetReportRequestListDao {

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

	public List<Map<String, AttributeValue>> scanRequestIdWithSellerId(String sellerId) throws Exception {
		List<Map<String, AttributeValue>> idList = new ArrayList<Map<String, AttributeValue>>();
		init(); // Initialize
		try {
			String tableName = "RequestId";
			HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
			Condition condition1 = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue(sellerId)); // SellerIdがおなじ
			Condition condition2 = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue().withN("0")); // レポートID未作成
			scanFilter.put("SellerId", condition1);
			scanFilter.put("GeneratedFlg", condition2);
			ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
			ScanResult scanResult = dynamoDB.scan(scanRequest);
			idList = scanResult.getItems();
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

	// レポートIDを保存する
	public void saveGeneratedId(List<String> ReportIdList, Map<String, Map<String, String>> map, String sellerId)
			throws Exception {
		init(); // Initialize
		try {
			String tableName = "GeneratedId";
			for (int i = 0; i < ReportIdList.size(); i++) {
				// 検索結果を展開
				String requestId = ReportIdList.get(i);

				// 注文データがあった場合は注文ごとに展開して注文情報テーブルにそれぞれ保存
				if ("_DONE_".equals(map.get(requestId).get("ReportProcessingStatus"))) {
					String generatedReportId = map.get(requestId).get("GeneratedReportId");
					String startDate = map.get(requestId).get("StartDate");
					String endDate = map.get(requestId).get("EndDate");

					// データ保存
					try {
						Map<String, AttributeValue> item = newItem(requestId, generatedReportId, sellerId, startDate,
								endDate);
						PutItemRequest putItemRequest = new PutItemRequest(tableName, item);
						dynamoDB.putItem(putItemRequest);
						System.out.println("Saved in DynamoDB : RequestID = " + generatedReportId);
						updateGeneratedFlg(requestId); // 作成済みフラグを反転する
					} catch (NullPointerException ne) {
						System.out.println("***NO DATA FOUND ReportRequestID = " + requestId);
						ne.printStackTrace();
						return;
					}
					// 注文データがなかった場合は要求IDを削除
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

	// Item定義用
	private static Map<String, AttributeValue> newItem(String requestId, String generatedReportId, String sellerId,
			String startDate, String endDate) {
		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		if (!requestId.isEmpty()) {
			item.put("RequestReportId", new AttributeValue(requestId));
		}
		if (!generatedReportId.isEmpty()) {
			item.put("GeneratedReportId", new AttributeValue(generatedReportId));
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
		int flg = 0;
		item.put("NotIssued", new AttributeValue().withN(Integer.toString(flg)));

		return item;
	}

	// 作成済みフラグを反転する
	public void updateGeneratedFlg(String ReportRequestId) throws Exception {
		init(); // Initialize
		try {
			String tableName = "RequestId";
			// update
			Map<String, AttributeValueUpdate> item = new HashMap<String, AttributeValueUpdate>();
			Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			int already_generated = 1;
			key.put("ReportRequestId", new AttributeValue().withS(ReportRequestId));
			item.put("GeneratedFlg", new AttributeValueUpdate().withAction(AttributeAction.PUT)
					.withValue(new AttributeValue().withN(Integer.toString(already_generated))));
			String timeStamp = new UtilityTools().getCurrentTimeStamp();
			item.put("GeneratedTime", new AttributeValueUpdate().withAction(AttributeAction.PUT)
					.withValue(new AttributeValue().withS(timeStamp)));
			UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(tableName).withKey(key)
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

	// データなしの要求IDを削除する
	public void deleteNoDataRequestId(String ReportRequestId) throws Exception {
		init(); // Initialize
		try {
			String tableName = "RequestId";
			// delete Item
			Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("ReportRequestId", new AttributeValue().withS(ReportRequestId));
			DeleteItemRequest deleteItemRequest = new DeleteItemRequest().withTableName(tableName).withKey(key);
			dynamoDB.deleteItem(deleteItemRequest);
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
