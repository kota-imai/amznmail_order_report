package dataaccess;

import java.util.HashMap;

import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

import dataaccess.parents.DynamoDbDao;

public class RequestReportDao extends DynamoDbDao {

	public void saveReportId(String requestId, String sellerId, String startDate, String endDate, String submitDate)
			throws Exception {
		super.init(); // 初期化
		setTableName("RequestId");
		try {
			Map<String, AttributeValue> item = newItem(requestId, sellerId, startDate, endDate, submitDate);
			PutItemRequest putItemRequest = new PutItemRequest(this.getTableName(), item);
			dynamoDB.putItem(putItemRequest);
		} catch (AmazonServiceException ase) {
			super.LoggerAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			super.LoggerAmazonClientException(ace);
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
		// GeneratedFlgの初期値は0（レポートID未作成状態）
		int flg = 0;
		item.put("GeneratedFlg", new AttributeValue().withN(Integer.toString(flg)));
		return item;
	}
}