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
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import com.amazonaws.services.dynamodbv2.model.UpdateItemResult;

import dataaccess.parent.DynamoDbDao;
import util.UtilityTools;

public class GetFbaShipmentReportDao extends DynamoDbDao {
	
	// 未発行のレポートIDを検索する
	public List<Map<String, AttributeValue>> scanGeneratedId(String sellerId) throws Exception {
		List<Map<String, AttributeValue>> idList = new ArrayList<Map<String, AttributeValue>>();
		super.init(); // 初期化
		setTableName("GeneratedId");
		try {
			HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
			// 条件1 出品者IDがぉなじ
			Condition condition1 = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue(sellerId));
			// 条件2 発行済みステータスが0（未発行）
			Condition condition2 = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue().withN("0"));
			scanFilter.put("SellerId", condition1);
			scanFilter.put("NotIssued", condition2);

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

	public List<Map<String, AttributeValue>> scanRequestIdWithSellerId(String sellerId) throws Exception {
		List<Map<String, AttributeValue>> idList = new ArrayList<Map<String, AttributeValue>>();
		super.init(); // 初期化
		setTableName("RequestId");
		try {
			// アクティブなリクエストIDがあるか検索
			HashMap<String, Condition> scanFilter = new HashMap<String, Condition>();
			Condition condition = new Condition().withComparisonOperator(ComparisonOperator.EQ.toString())
					.withAttributeValueList(new AttributeValue(sellerId));
			scanFilter.put("SellerId", condition);

			ScanRequest scanRequest = new ScanRequest(this.getTableName()).withScanFilter(scanFilter);
			ScanResult scanResult = dynamoDB.scan(scanRequest);

			idList = scanResult.getItems();
			
		} catch (AmazonServiceException ase) {
			super.LoggerAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			super.LoggerAmazonClientException(ace);		}
		return idList;
	}
	
	// 出荷情報を保存する
    public void saveShipmentInfo(String sellerId, List<String[]> items) throws Exception {
    	super.init(); // 初期化
    	setTableName("Ship");
    	try {
    		for (int i = 0 ;i<items.size() ;i++) {
    			String amazon_order_id = null;try{amazon_order_id = items.get(i)[0];} catch(NullPointerException e) {amazon_order_id = "";}
    			String merchant_order_id = null;try{merchant_order_id = items.get(i)[1];} catch(NullPointerException e) {merchant_order_id = "";}
    			String shipment_id = null;try{shipment_id = items.get(i)[2];} catch(NullPointerException e) {shipment_id = "";}
    			String shipment_item_id = null;try{shipment_item_id = items.get(i)[3];} catch(NullPointerException e) {shipment_item_id = "";}
    			String amazon_order_item_id = null;try{amazon_order_item_id = items.get(i)[4];} catch(NullPointerException e) {amazon_order_item_id = "";}
    			String merchant_order_item_id = null;try{merchant_order_item_id = items.get(i)[5];} catch(NullPointerException e) {merchant_order_item_id = "";}
    			String purchase_date = null;try{purchase_date = items.get(i)[6];} catch(NullPointerException e) {purchase_date = "";}
    			String payments_date = null;try{payments_date = items.get(i)[7];} catch(NullPointerException e) {payments_date = "";}
    			String shipment_date = null;try{shipment_date = items.get(i)[8];} catch(NullPointerException e) {shipment_date = "";}
    			String reporting_date = null;try{reporting_date = items.get(i)[9];} catch(NullPointerException e) {reporting_date = "";}
    			String buyer_email = null;try{buyer_email = items.get(i)[10];} catch(NullPointerException e) {buyer_email = "";}
    			String buyer_name = null;try{buyer_name = items.get(i)[11];} catch(NullPointerException e) {buyer_name = "";}
    			String buyer_phone_number = null;try{buyer_phone_number = items.get(i)[12];} catch(NullPointerException e) {buyer_phone_number = "";}
    			String sku = null;try{sku = items.get(i)[13];} catch(NullPointerException e) {sku = "";}
    			String product_name = null;try{product_name = items.get(i)[14];} catch(NullPointerException e) {product_name = "";}
    			String quantity_shipped = null;try{quantity_shipped = items.get(i)[15];} catch(NullPointerException e) {quantity_shipped = "";}
    			String currency = null;try{currency = items.get(i)[16];} catch(NullPointerException e) {currency = "";}
    			String item_price = null;try{item_price = items.get(i)[17];} catch(NullPointerException e) {item_price = "";}
    			String item_tax = null;try{item_tax = items.get(i)[18];} catch(NullPointerException e) {item_tax = "";}
    			String shipping_price = null;try{shipping_price = items.get(i)[19];} catch(NullPointerException e) {shipping_price = "";}
    			String shipping_tax = null;try{shipping_tax = items.get(i)[20];} catch(NullPointerException e) {shipping_tax = "";}
    			String gift_wrap_price = null;try{gift_wrap_price = items.get(i)[21];} catch(NullPointerException e) {gift_wrap_price = "";}
    			String gift_wrap_tax = null;try{gift_wrap_tax = items.get(i)[22];} catch(NullPointerException e) {gift_wrap_tax = "";}
    			String ship_service_level = null;try{ship_service_level = items.get(i)[23];} catch(NullPointerException e) {ship_service_level = "";}
    			String recipient_name = null;try{recipient_name = items.get(i)[24];} catch(NullPointerException e) {recipient_name = "";}
    			String ship_address_1 = null;try{ship_address_1 = items.get(i)[25];} catch(NullPointerException e) {ship_address_1 = "";}
    			String ship_address_2 = null;try{ship_address_2 = items.get(i)[26];} catch(NullPointerException e) {ship_address_2 = "";}
    			String ship_address_3 = null;try{ship_address_3 = items.get(i)[27];} catch(NullPointerException e) {ship_address_3 = "";}
    			String ship_city = null;try{ship_city = items.get(i)[28];} catch(NullPointerException e) {ship_city = "";}
    			String ship_state = null;try{ship_state = items.get(i)[29];} catch(NullPointerException e) {ship_state = "";}
    			String ship_postal_code = null;try{ship_postal_code = items.get(i)[30];} catch(NullPointerException e) {ship_postal_code = "";}
    			String ship_country = null;try{ship_country = items.get(i)[31];} catch(NullPointerException e) {ship_country = "";}
    			String ship_phone_number = null;try{ship_phone_number = items.get(i)[32];} catch(NullPointerException e) {ship_phone_number = "";}
    			String bill_address_1 = null;try{bill_address_1 = items.get(i)[33];} catch(NullPointerException e) {bill_address_1 = "";}
    			String bill_address_2 = null;try{bill_address_2 = items.get(i)[34];} catch(NullPointerException e) {bill_address_2 = "";}
    			String bill_address_3 = null;try{bill_address_3 = items.get(i)[35];} catch(NullPointerException e) {bill_address_3 = "";}
    			String bill_city = null;try{bill_city = items.get(i)[36];} catch(NullPointerException e) {bill_city = "";}
    			String bill_state = null;try{bill_state = items.get(i)[37];} catch(NullPointerException e) {bill_state = "";}
    			String bill_postal_code = null;try{bill_postal_code = items.get(i)[38];} catch(NullPointerException e) {bill_postal_code = "";}
    			String bill_country = null;try{bill_country = items.get(i)[39];} catch(NullPointerException e) {bill_country = "";}
    			String item_promotion_discount = null;try{item_promotion_discount = items.get(i)[40];} catch(NullPointerException e) {item_promotion_discount = "";}
    			String ship_promotion_discount = null;try{ship_promotion_discount = items.get(i)[41];} catch(NullPointerException e) {ship_promotion_discount = "";}
    			String carrier = null;try{carrier = items.get(i)[42];} catch(NullPointerException e) {carrier = "";}
    			String tracking_number = null;try{tracking_number = items.get(i)[43];} catch(NullPointerException e) {tracking_number = "";}
    			String estimated_arrival_date = null;try{estimated_arrival_date = items.get(i)[44];} catch(NullPointerException e) {estimated_arrival_date = "";}
    			String fulfillment_center_id = null;try{fulfillment_center_id = items.get(i)[45];} catch(NullPointerException e) {fulfillment_center_id = "";}
    			String fulfillment_channel = null;try{fulfillment_channel = items.get(i)[46];} catch(NullPointerException e) {fulfillment_channel = "";}
    			String sales_channel = null;try{sales_channel = items.get(i)[47];} catch(NullPointerException e) {sales_channel = "";}
    			String seller_id = sellerId;
            	Map<String, AttributeValue> item = newItem(
            			amazon_order_id,
            			merchant_order_id,
            			shipment_id,
            			shipment_item_id,
            			amazon_order_item_id,
            			merchant_order_item_id,
            			purchase_date,
            			payments_date,
            			shipment_date,
            			reporting_date,
            			buyer_email,
            			buyer_name,
            			buyer_phone_number,
            			sku,
            			product_name,
            			quantity_shipped,
            			currency,
            			item_price,
            			item_tax,
            			shipping_price,
            			shipping_tax,
            			gift_wrap_price,
            			gift_wrap_tax,
            			ship_service_level,
            			recipient_name,
            			ship_address_1,
            			ship_address_2,
            			ship_address_3,
            			ship_city,
            			ship_state,
            			ship_postal_code,
            			ship_country,
            			ship_phone_number,
            			bill_address_1,
            			bill_address_2,
            			bill_address_3,
            			bill_city,
            			bill_state,
            			bill_postal_code,
            			bill_country,
            			item_promotion_discount,
            			ship_promotion_discount,
            			carrier,
            			tracking_number,
            			estimated_arrival_date,
            			fulfillment_center_id,
            			fulfillment_channel,
            			sales_channel,
            			seller_id);
            	PutItemRequest putItemRequest = new PutItemRequest(this.getTableName(), item);
            	dynamoDB.putItem(putItemRequest);
        	}
		} catch (AmazonServiceException ase) {
			super.LoggerAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			super.LoggerAmazonClientException(ace);
		}
	}
    
    // Item定義用
    private static Map<String, AttributeValue> newItem(
            String amazon_order_id,
            String merchant_order_id,
            String shipment_id,
            String shipment_item_id,
            String amazon_order_item_id,
            String merchant_order_item_id,
            String purchase_date,
            String payments_date,
            String shipment_date,
            String reporting_date,
            String buyer_email,
            String buyer_name,
            String buyer_phone_number,
            String sku,
            String product_name,
            String quantity_shipped,
            String currency,
            String item_price,
            String item_tax,
            String shipping_price,
            String shipping_tax,
            String gift_wrap_price,
            String gift_wrap_tax,
            String ship_service_level,
            String recipient_name,
            String ship_address_1,
            String ship_address_2,
            String ship_address_3,
            String ship_city,
            String ship_state,
            String ship_postal_code,
            String ship_country,
            String ship_phone_number,
            String bill_address_1,
            String bill_address_2,
            String bill_address_3,
            String bill_city,
            String bill_state,
            String bill_postal_code,
            String bill_country,
            String item_promotion_discount,
            String ship_promotion_discount,
            String carrier,
            String tracking_number,
            String estimated_arrival_date,
            String fulfillment_center_id,
            String fulfillment_channel,
            String sales_channel,
            String seller_id) {
        		Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
            	//Trim
        		try{if(!amazon_order_id .equals("")) {item.put("amazon_order_id", new AttributeValue(amazon_order_id ));}}catch(NullPointerException e){}
        		try{if(!merchant_order_id .equals("")) {item.put("merchant_order_id", new AttributeValue(merchant_order_id ));}}catch(NullPointerException e){}
        		try{if(!shipment_id .equals("")) {item.put("shipment_id", new AttributeValue(shipment_id ));}}catch(NullPointerException e){}
        		try{if(!shipment_item_id .equals("")) {item.put("shipment_item_id", new AttributeValue(shipment_item_id ));}}catch(NullPointerException e){}
        		try{if(!amazon_order_item_id .equals("")) {item.put("amazon_order_item_id", new AttributeValue(amazon_order_item_id ));}}catch(NullPointerException e){}
        		try{if(!merchant_order_item_id .equals("")) {item.put("merchant_order_item_id", new AttributeValue(merchant_order_item_id ));}}catch(NullPointerException e){}
        		try{if(!purchase_date .equals("")) {item.put("purchase_date", new AttributeValue(purchase_date ));}}catch(NullPointerException e){}
        		try{if(!payments_date .equals("")) {item.put("payments_date", new AttributeValue(payments_date ));}}catch(NullPointerException e){}
        		try{if(!shipment_date .equals("")) {item.put("shipment_date", new AttributeValue(shipment_date ));}}catch(NullPointerException e){}
        		try{if(!reporting_date .equals("")) {item.put("reporting_date", new AttributeValue(reporting_date ));}}catch(NullPointerException e){}
        		try{if(!buyer_email .equals("")) {item.put("buyer_email", new AttributeValue(buyer_email ));}}catch(NullPointerException e){}
        		try{if(!buyer_name .equals("")) {item.put("buyer_name", new AttributeValue(buyer_name ));}}catch(NullPointerException e){}
        		try{if(!buyer_phone_number .equals("")) {item.put("buyer_phone_number", new AttributeValue(buyer_phone_number ));}}catch(NullPointerException e){}
        		try{if(!sku .equals("")) {item.put("sku", new AttributeValue(sku ));}}catch(NullPointerException e){}
        		try{if(!product_name .equals("")) {item.put("product_name", new AttributeValue(product_name ));}}catch(NullPointerException e){}
        		try{if(!quantity_shipped .equals("")) {item.put("quantity_shipped", new AttributeValue(quantity_shipped ));}}catch(NullPointerException e){}
        		try{if(!currency .equals("")) {item.put("currency", new AttributeValue(currency ));}}catch(NullPointerException e){}
        		try{if(!item_price .equals("")) {item.put("item_price", new AttributeValue(item_price ));}}catch(NullPointerException e){}
        		try{if(!item_tax .equals("")) {item.put("item_tax", new AttributeValue(item_tax ));}}catch(NullPointerException e){}
        		try{if(!shipping_price .equals("")) {item.put("shipping_price", new AttributeValue(shipping_price ));}}catch(NullPointerException e){}
        		try{if(!shipping_tax .equals("")) {item.put("shipping_tax", new AttributeValue(shipping_tax ));}}catch(NullPointerException e){}
        		try{if(!gift_wrap_price .equals("")) {item.put("gift_wrap_price", new AttributeValue(gift_wrap_price ));}}catch(NullPointerException e){}
        		try{if(!gift_wrap_tax .equals("")) {item.put("gift_wrap_tax", new AttributeValue(gift_wrap_tax ));}}catch(NullPointerException e){}
        		try{if(!ship_service_level .equals("")) {item.put("ship_service_level", new AttributeValue(ship_service_level ));}}catch(NullPointerException e){}
        		try{if(!recipient_name .equals("")) {item.put("recipient_name", new AttributeValue(recipient_name ));}}catch(NullPointerException e){}
        		try{if(!ship_address_1 .equals("")) {item.put("ship_address_1", new AttributeValue(ship_address_1 ));}}catch(NullPointerException e){}
        		try{if(!ship_address_2 .equals("")) {item.put("ship_address_2", new AttributeValue(ship_address_2 ));}}catch(NullPointerException e){}
        		try{if(!ship_address_3 .equals("")) {item.put("ship_address_3", new AttributeValue(ship_address_3 ));}}catch(NullPointerException e){}
        		try{if(!ship_city .equals("")) {item.put("ship_city", new AttributeValue(ship_city ));}}catch(NullPointerException e){}
        		try{if(!ship_state .equals("")) {item.put("ship_state", new AttributeValue(ship_state ));}}catch(NullPointerException e){}
        		try{if(!ship_postal_code .equals("")) {item.put("ship_postal_code", new AttributeValue(ship_postal_code ));}}catch(NullPointerException e){}
        		try{if(!ship_country .equals("")) {item.put("ship_country", new AttributeValue(ship_country ));}}catch(NullPointerException e){}
        		try{if(!ship_phone_number .equals("")) {item.put("ship_phone_number", new AttributeValue(ship_phone_number ));}}catch(NullPointerException e){}
        		try{if(!bill_address_1 .equals("")) {item.put("bill_address_1", new AttributeValue(bill_address_1 ));}}catch(NullPointerException e){}
        		try{if(!bill_address_2 .equals("")) {item.put("bill_address_2", new AttributeValue(bill_address_2 ));}}catch(NullPointerException e){}
        		try{if(!bill_address_3 .equals("")) {item.put("bill_address_3", new AttributeValue(bill_address_3 ));}}catch(NullPointerException e){}
        		try{if(!bill_city .equals("")) {item.put("bill_city", new AttributeValue(bill_city ));}}catch(NullPointerException e){}
        		try{if(!bill_state .equals("")) {item.put("bill_state", new AttributeValue(bill_state ));}}catch(NullPointerException e){}
        		try{if(!bill_postal_code .equals("")) {item.put("bill_postal_code", new AttributeValue(bill_postal_code ));}}catch(NullPointerException e){}
        		try{if(!bill_country .equals("")) {item.put("bill_country", new AttributeValue(bill_country ));}}catch(NullPointerException e){}
        		try{if(!item_promotion_discount .equals("")) {item.put("item_promotion_discount", new AttributeValue(item_promotion_discount ));}}catch(NullPointerException e){}
        		try{if(!ship_promotion_discount .equals("")) {item.put("ship_promotion_discount", new AttributeValue(ship_promotion_discount ));}}catch(NullPointerException e){}
        		try{if(!carrier .equals("")) {item.put("carrier", new AttributeValue(carrier ));}}catch(NullPointerException e){}
        		try{if(!tracking_number .equals("")) {item.put("tracking_number", new AttributeValue(tracking_number ));}}catch(NullPointerException e){}
        		try{if(!estimated_arrival_date .equals("")) {item.put("estimated_arrival_date", new AttributeValue(estimated_arrival_date ));}}catch(NullPointerException e){}
        		try{if(!fulfillment_center_id .equals("")) {item.put("fulfillment_center_id", new AttributeValue(fulfillment_center_id ));}}catch(NullPointerException e){}
        		try{if(!fulfillment_channel .equals("")) {item.put("fulfillment_channel", new AttributeValue(fulfillment_channel ));}}catch(NullPointerException e){}
        		try{if(!sales_channel.equals("")) {item.put("sales_channel", new AttributeValue(sales_channel));}}catch(NullPointerException e){}
        		item.put("seller_id", new AttributeValue(seller_id));
                int flg = 0;//Flgは初期値0
        		item.put("thanks_mail_created", new AttributeValue().withN(Integer.toString(flg)));
        		item.put("shipped_mail_created", new AttributeValue().withN(Integer.toString(flg)));
            return item;
	}

    // 発行済みフラグを反転する
    public void updateIssuedFlg(String generatedId) throws Exception {
        super.init(); // 初期化
        setTableName("GeneratedId");
        try {
            // アイテム更新
            Map<String, AttributeValueUpdate> item = new HashMap<String, AttributeValueUpdate>();
            Map<String, AttributeValue> key = new HashMap<String, AttributeValue>();
            // 発行済みフラグ
            int already_issued = 1;
            key.put("GeneratedReportId", new AttributeValue().withS(generatedId));
            item.put("NotIssued", 
                new AttributeValueUpdate()
                    .withAction(AttributeAction.PUT)
                    .withValue(new AttributeValue().withN(Integer.toString(already_issued))));
            String timeStamp = new UtilityTools().getCurrentTimeStamp();
            item.put("IssuedTime",
                    new AttributeValueUpdate()
                    .withAction(AttributeAction.PUT)
                    .withValue(new AttributeValue().withS(timeStamp)));
            UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName(this.getTableName())
                .withKey(key)
                .withAttributeUpdates(item);
            UpdateItemResult result = dynamoDB.updateItem(updateItemRequest);
            System.out.println("Result: " + result);

		} catch (AmazonServiceException ase) {
			super.LoggerAmazonServiceException(ase);
		} catch (AmazonClientException ace) {
			super.LoggerAmazonClientException(ace);
		}
    }
}