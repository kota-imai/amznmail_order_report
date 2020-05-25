package exec.notused;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.amazonaws.samples.AmazonSESSample;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import dataaccess.SendMessagesDao;

public class SendMessages {

	public static void main(String[] args) {
		AmazonSESSample mailer = new AmazonSESSample();
		SendMessagesDao dao = new SendMessagesDao();
		String seller_id = "A2G9KQ0CU8K2G9";
		
		List<Map<String, AttributeValue>> mailList = null;
		//DBから取得 SentFlg = 0
		try {
//			mailList = dao.scanMailList(seller_id);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("***Failed to get a template***");
		}
		//Send a message
		if(mailList.size()>0) {
			for(int i = 0; i < mailList.size(); i++) {
				String orderId = mailList.get(i).get("OrderId").getS();
				String from = mailList.get(i).get("From").getS();
//				String to = mailList.get(i).get("To").getS();
				String to = "k.ima003365@gmail.com";
				String configSet = mailList.get(i).get("ConfigSet").getS();
				String subject = mailList.get(i).get("Subject").getS();
				String html = mailList.get(i).get("HTML").getS();
				String flat = mailList.get(i).get("FLAT").getS();
				
				try {
					mailer.sendMessage(from, to, configSet, subject, html, flat);
					//SentFlgを1に更新
					try {
						dao.updateSentStatus(orderId);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("***Failed to update status (id = " + orderId + ")");
						return;
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("***Failed to send message (id = " + orderId + ")");
					return;
				}
			}
		}
	}
}