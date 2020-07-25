package exec;

import java.util.List;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import config.SystemConfig;
import dataaccess.CreateMessageDao;
import dataaccess.GetShipmentInfoDao;
import dataaccess.SaveThanksMessageDao;
import mail.ThanksMessage;

public class CreateThanksMessage {

	public static void main(String[] args) {
		final String sellerId = SystemConfig.getSellerId();
		List<Map<String, AttributeValue>> itemList = null;

		// ユーザIDごとの注文情報を取得、なければおわり
		try {
			GetShipmentInfoDao dao = new GetShipmentInfoDao();
			itemList = dao.getInfoThanx(sellerId);
			System.out.println(itemList);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("***Failed to get a template***");
		}
		if (itemList.isEmpty()) {
			System.out.println("There is NO ORDER whose thanks-messages haven't created");
			return;
		}
		// Email情報を作成してDB保存する
		if (itemList.size() > 0) {
			for (int i = 0; i < itemList.size(); i++) {
				String fulfillmentChannel = itemList.get(i).get("fulfillment_channel").getS();
				String orderId = itemList.get(i).get("amazon_order_id").getS();
				// メッセージを作成
				try {
					// メッセージを保存
					ThanksMessage msg = ThanksMessage.getInstance();
					msg.setOrderId(orderId);
					msg.setSellerId(sellerId);
					msg.setProductName(itemList.get(i).get("product_name").getS());
					msg.setItemQuantity(itemList.get(i).get("quantity_shipped").getS());
					msg.setToAddress(itemList.get(i).get("buyer_email").getS());
					msg.setFromAddress("kota.imai@firmimai.biz"); // TODO 送信元アドレスを変更する
					msg.setArrivalDate(itemList.get(i).get("estimated_arrival_date").getS());
					if ("AFN".contentEquals(fulfillmentChannel)) {
						msg.setKubun("FBAthanks");
					} else if ("MFN".contentEquals(fulfillmentChannel)) {
						msg.setKubun("FBMthanks");
					}
					new SaveThanksMessageDao().saveThanksMessage(msg);
					// 生成フラグを更新（DynamoDB）
					new CreateMessageDao().updateThanksCreatedFlg(orderId);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("***Failed to save messages***");
				}
			}
		} else {
			return;
		}
	}
}
