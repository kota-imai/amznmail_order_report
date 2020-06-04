package dataaccess;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import dataaccess.parent.DynamoDbDao;

public class GetShipmentInfoDao extends DynamoDbDao {

    public List<Map<String, AttributeValue>> getInfoThanx(String sellerid) throws Exception {
    	List<Map<String, AttributeValue>> items = new ArrayList<Map<String, AttributeValue>>();
        super.init(); // 初期化
        setTableName("Ship");
        try {
            HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
            // 条件1 出品者IDがおなじ
            Condition condition1 = new Condition()
                .withComparisonOperator(ComparisonOperator.EQ.toString())
                .withAttributeValueList(new AttributeValue(sellerid));
            // 条件2 到着予定の2日後
//            String timeStamp = new UtilityTools().getTimeStampTwoDaysAgo();
//            System.out.println(timeStamp);
//            Condition condition2 = new Condition()
//                    .withComparisonOperator(ComparisonOperator.BEGINS_WITH.toString())
//                    .withAttributeValueList(new AttributeValue(timeStamp));
            // 条件3 お礼メールが未作成（thanks_mail_createdが0）
            Condition condition3 = new Condition()
                	.withComparisonOperator(ComparisonOperator.EQ.toString())
                	.withAttributeValueList(new AttributeValue().withN("0"));

            scanFilter.put("seller_id", condition1);
//            scanFilter.put("estimated_arrival_date", condition2);
            scanFilter.put("thanks_mail_created", condition3);
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
}