package exec;

import java.util.List;

import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import dataaccess.CreateMessageDao;
import dataaccess.GetShipmentInfoDao;

public class CreateThanksMessage {

	public static void main(String[] args) {
		String seller_id = "A2G9KQ0CU8K2G9";
		String from = new String();
		String to = new String();
		String kbn = new String();
		List<Map<String, AttributeValue>> itemList = null;

//		ユーザIDごとの注文情報を取得、なければおわり
		try {
			itemList = GetShipmentInfoDao.getInfoThanx(seller_id);
			System.out.println(itemList);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("***Failed to get a template***");
		}

		if(itemList.isEmpty()) {
			System.out.println("There is NO ORDER whose thanks-messages haven't created");
			return;
		}
		
		//Create Email
		if(itemList.size()>0) {
			for(int i = 0; i < itemList.size(); i++) {
				String amazon_order_id = itemList.get(i).get("amazon_order_id").getS();//orderid取得
				String buyer_email = itemList.get(i).get("buyer_email").getS();
				String fulfillment_channel = itemList.get(i).get("fulfillment_channel").getS();
				String product_name = itemList.get(i).get("product_name").getS();
				String quantity_shipped = itemList.get(i).get("quantity_shipped").getS();
				String estimated_arrival_date = itemList.get(i).get("estimated_arrival_date").getS();				
//				String html_v = new String();
//				String flat_v = new String();
//				templateList = mailDao.getTemplate(seller_id);
//				configset = templateList.get(0).get("ConfigSet").getS();
//				subject = templateList.get(0).get("Subject").getS();
				if ("AFN".contentEquals(fulfillment_channel)) {
//					html_v = templateList.get(0).get("ThanksFBA_html").getS();//FBAの場合
//					flat_v = templateList.get(0).get("ThanksFBA_flat").getS();
					kbn = "FBAthanks";
				} else if ("MFN".contentEquals(fulfillment_channel)){
//					html_v = templateList.get(0).get("ThanksFBA_html").getS();//自己発送の場合
//					flat_v = templateList.get(0).get("ThanksFBA_flat").getS();
					kbn = "FBMthanks";
				}
				to = buyer_email;
				from = "kota.imai@firmimai.biz";  //別のアドレスに変更する
				//Message Creation
				CreateMessageDao dao = new CreateMessageDao();
				try {
					dao.saveThanksMessage(amazon_order_id,seller_id,kbn,product_name,quantity_shipped,to,from,estimated_arrival_date);
					dao.updateThanksCreatedFlg(amazon_order_id);
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
