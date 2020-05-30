package dataaccess;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
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
import config.MysqlConfig;

public class CreateMessageDao extends DynamoDbDao {

	public List<Map<String, AttributeValue>> getTemplate(String sellerid) throws Exception {
		List<Map<String, AttributeValue>> items = new ArrayList<Map<String, AttributeValue>>();
		super.init(); // 初期化
		try {
			String tableName = "Template";

			// テンプレートをスキャンする
			HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue(sellerid));
			scanFilter.put("UserId", condition);
			ScanRequest scanRequest = new ScanRequest(tableName).withScanFilter(scanFilter);
			ScanResult scanResult = dynamoDB.scan(scanRequest);
			items = scanResult.getItems();

		} catch (AmazonServiceException ase) {
			super.LoggerAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			super.LoggerAmazonClientException(ace);
		}
		return items;
	}

	// お礼メールを保存する
	public void saveThanksMessage(String orderId, String sellerId, String kbn, String productName, String itemQuantity,
			String to, String from, String arrival) throws Exception {

		final String SQL = "insert into MailThanks" + "( OrderId" + ", SellerId" + ", Kbn" + ", ProductName"
				+ ", ItemQuantity" + ", To_Email" + ", From_Email" + ", EstimatedArrivalDate" + " ) values ("
				+ encloseSingleQuote(orderId) + "," + encloseSingleQuote(sellerId) + "," + encloseSingleQuote(kbn) + ","
				+ encloseSingleQuote(productName) + "," + itemQuantity + "," + encloseSingleQuote(to) + ","
				+ encloseSingleQuote(from) + "," + formatArrivalDate(arrival) + ")";

		// MySQL接続
		Connection conn = null;
		PreparedStatement ps = null;
		MysqlConfig conf = new MysqlConfig();
		try {
			conn = DriverManager.getConnection(conf.URL, conf.USER, conf.PASS);
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(SQL);
			ps.executeUpdate();
			conn.commit();
		} catch (SQLIntegrityConstraintViolationException de) {// 同一の注文IDがあった場合
			conn.rollback();
			System.out.println("There is the record in MailThanks table / OrderId = " + orderId);
			return;
		} catch (Exception e) {
			conn.rollback();
			System.out.println("***failed to Insert MailThanks : orderid = " + orderId + " ***");
			e.printStackTrace();
			throw new Exception();
		} finally {
			new Closer().closeConnection(conn, ps); // クローズ処理
		}
	}

	// Thanksメール作成済みフラグの反転
	public void updateThanksCreatedFlg(String OrderId) throws Exception {
		init();
		try {
			String tableName = "Ship";

			// Item更新
			Map<String, AttributeValueUpdate> item = new HashMap<String, AttributeValueUpdate>();
			Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
			int already_generated = 1;
			key.put("amazon_order_id", new AttributeValue().withS(OrderId));
			item.put("thanks_mail_created", new AttributeValueUpdate().withAction(AttributeAction.PUT)
					.withValue(new AttributeValue().withN(Integer.toString(already_generated))));
			UpdateItemRequest updateItemRequest = new UpdateItemRequest().withTableName(tableName).withKey(key)
					.withAttributeUpdates(item);
			dynamoDB.updateItem(updateItemRequest);

		} catch (AmazonServiceException ase) {
			super.LoggerAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			super.LoggerAmazonClientException(ace);
		}
	}

	// クラス内共有メソッド
	// シングルクォーテーションで囲む
	private String encloseSingleQuote(String str) {
		return "'" + str + "'";
	}

	// Amznサーバで許容されている日付型に変換する
	private String formatArrivalDate(String str) {
		return "'" + str.substring(0, 19).replace("T", " ") + "'";
	}
}