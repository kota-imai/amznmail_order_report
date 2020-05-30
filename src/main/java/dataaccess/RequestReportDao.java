package dataaccess;

import java.util.HashMap;

import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;

public class RequestReportDao extends DynamoDbDao{

	public void saveReportId(String requestId, String sellerId, String startDate, String endDate,
			String submitDate) throws Exception {
		String tableName = "RequestId";
		super.init(); // 初期化
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
		// GeneratedFlgの初期値は0（レポートID未作成状態）
		int flg = 0;
		item.put("GeneratedFlg", new AttributeValue().withN(Integer.toString(flg)));

		return item;
	}
}
