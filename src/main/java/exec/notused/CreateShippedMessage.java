package exec.notused;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import dataaccess.CreateMessageDao;
import dataaccess.GetShipmentInfoDao;
import util.UtilityTools;

public class CreateShippedMessage {

	public static void main(String[] args) {
		String seller_id = "A2G9KQ0CU8K2G9";
		
		String from = new String();
		String to = new String();
		String shopName = new String();
		String configset = new String();
		String subject = new String();
		String html = new String();
		String flat = new String();
		String kbn = new String();
		
		List<Map<String, AttributeValue>> itemList = null;
		List<Map<String, AttributeValue>> templateList = null;

//		ユーザIDごとの注文情報を取得、なければおわり
		try {
			itemList = GetShipmentInfoDao.getInfo(seller_id);
			System.out.println(itemList);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("***Failed to get a template***");
		}
		
		//Create Email
		if(itemList.size()>0) {
			for(int i = 0; i < itemList.size(); i++) {
				String amazon_order_id = itemList.get(i).get("amazon_order_id").getS();//orderid取得
				String buyer_email = itemList.get(i).get("buyer_email").getS();
				String fulfillment_channel = itemList.get(i).get("fulfillment_channel").getS();
				String product_name = itemList.get(i).get("product_name").getS();
				String quantity_shipped = itemList.get(i).get("product_name").getS();
				
				CreateMessageDao mailDao = new CreateMessageDao();

				try {
					String html_v = new String();
					String flat_v = new String();
					templateList = mailDao.getTemplate(seller_id);
					shopName = templateList.get(0).get("ShopName").getS();
					configset = templateList.get(0).get("ConfigSet").getS();
					subject = templateList.get(0).get("Subject").getS();
					if ("AFN".contentEquals(fulfillment_channel)) {
						html_v = templateList.get(0).get("ThanksFBA_html").getS();//FBAの場合
						flat_v = templateList.get(0).get("ThanksFBA_flat").getS();
						kbn = "FBAth";
					} else {
						html_v = templateList.get(0).get("ThanksFBA_html").getS();//自己発送の場合
						flat_v = templateList.get(0).get("ThanksFBA_flat").getS();
						kbn = "FBMth";
					}
					UtilityTools tools = new UtilityTools();
					html = tools.replaceVariable(html_v, amazon_order_id, shopName, product_name, quantity_shipped);
					flat = tools.replaceVariable(flat_v, amazon_order_id, shopName, product_name, quantity_shipped);
				} catch (Exception e) {
					e.printStackTrace();
				}
				to = buyer_email;
				from = "kota.imai@firmimai.biz";
				
				//Message Creation
				CreateMessageDao dao = new CreateMessageDao();
				try {
					dao.saveMessage(amazon_order_id,seller_id,configset,from,to,subject,html,flat,kbn);
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
