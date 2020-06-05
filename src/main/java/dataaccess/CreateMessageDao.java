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
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;

import dataaccess.parents.DynamoDbDao;

public class CreateMessageDao extends DynamoDbDao {

	public List<Map<String, AttributeValue>> getTemplate(String sellerid) throws Exception {
		List<Map<String, AttributeValue>> items = new ArrayList<Map<String, AttributeValue>>();
		super.init(); // 初期化
		setTableName("Template"); // テーブルを設定
		try {
			// テンプレートをスキャンする
			HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue(sellerid));
			scanFilter.put("UserId", condition);
			ScanRequest scanRequest = new ScanRequest(this.getTableName()).withScanFilter(scanFilter);
			ScanResult scanResult = dynamoDB.scan(scanRequest);
			items = scanResult.getItems();

		} catch (AmazonServiceException ase) {
			super.LoggerAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			super.LoggerAmazonClientException(ace);
		}
		return items;
	}

	// Thanksメール作成済みフラグの反転
	public void updateThanksCreatedFlg(String OrderId) throws Exception {
		super.init();
		setTableName("Ship");
		try {
			// Item更新
			Map<String, AttributeValueUpdate> item = new HashMap<String, AttributeValueUpdate>();
			Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			int already_generated = 1;
			key.put("amazon_order_id", new AttributeValue().withS(OrderId));
			item.put("thanks_mail_created", new AttributeValueUpdate().withAction(AttributeAction.PUT)
					.withValue(new AttributeValue().withN(Integer.toString(already_generated))));
			UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(getTableName()).withKey(key)
					.withAttributeUpdates(item);
			dynamoDB.updateItem(updateItemRequest);

		} catch (AmazonServiceException ase) {
			super.LoggerAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			super.LoggerAmazonClientException(ace);
		}
	}
}