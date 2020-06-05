package dataaccess;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
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

import dataaccess.parents.DynamoDbDao;
import util.UtilityTools;

public class GetReportRequestListDao extends DynamoDbDao {

	public List<Map<String, AttributeValue>> scanRequestIdWithSellerId(String sellerId) throws Exception {
		List<Map<String, AttributeValue>> idList = new ArrayList<Map<String, AttributeValue>>();
		super.init(); // 初期化
		setTableName("RequestId");
		try {
			HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
			// 条件1 SellerIdがおなじ
			Condition condition1 = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue(sellerId)); 
			// 条件2 レポートID未生成（GeneratedFlgが0）
			Condition condition2 = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue().withN("0")); 
			scanFilter.put("SellerId", condition1);
			scanFilter.put("GeneratedFlg", condition2);
			ScanRequest scanRequest = new ScanRequest(this.getTableName()).withScanFilter(scanFilter);
			ScanResult scanResult = dynamoDB.scan(scanRequest);
			idList = scanResult.getItems();
		} catch (AmazonServiceException ase) {
			super.LoggerAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			super.LoggerAmazonClientException(ace);
		}
		return idList;
	}

	// レポートIDを保存する
	public void saveGeneratedId(List<String> ReportIdList, Map<String, Map<String, String>> map, String sellerId)
			throws Exception {
		super.init(); // 初期化
		setTableName("GeneratedId");
		try {
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
						PutItemRequest putItemRequest = new PutItemRequest(this.getTableName(), item);
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
			super.LoggerAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			super.LoggerAmazonClientException(ace);
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
		super.init(); // 初期化
		setTableName("RequestId");
		try {
			// update
			Map<String, AttributeValueUpdate> item = new HashMap<String, AttributeValueUpdate>();
			Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			// レポートID生成済み
			int already_generated = 1;

			key.put("ReportRequestId", new AttributeValue().withS(ReportRequestId));
			item.put("GeneratedFlg", new AttributeValueUpdate().withAction(AttributeAction.PUT)
					.withValue(new AttributeValue().withN(Integer.toString(already_generated))));
			String timeStamp = new UtilityTools().getCurrentTimeStamp();
			item.put("GeneratedTime", new AttributeValueUpdate().withAction(AttributeAction.PUT)
					.withValue(new AttributeValue().withS(timeStamp)));
			UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(this.getTableName()).withKey(key)
					.withAttributeUpdates(item);
			UpdateItemResult result = dynamoDB.updateItem(updateItemRequest);
			System.out.println("Result: " + result);

		} catch (AmazonServiceException ase) {
			super.LoggerAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			super.LoggerAmazonClientException(ace);
		}
	}

	// データなしの要求IDを削除する
	public void deleteNoDataRequestId(String ReportRequestId) throws Exception {
		super.init(); // 初期化
		setTableName("RequestId");
		try {
			// delete Item
			Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			key.put("ReportRequestId", new AttributeValue().withS(ReportRequestId));
			DeleteItemRequest deleteItemRequest = new DeleteItemRequest().withTableName(this.getTableName()).withKey(key);
			dynamoDB.deleteItem(deleteItemRequest);
			System.out.println("Result: Item deleted RequestReportId = " + ReportRequestId);
		} catch (AmazonServiceException ase) {
			super.LoggerAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			super.LoggerAmazonClientException(ace);
		}
	}
}
