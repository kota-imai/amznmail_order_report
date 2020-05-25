package exec;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import com.amazonaws.samples.AmazonSESSample;

import dataaccess.SendMessagesDao;

public class SendThanksMessages {

	public static void main(String[] args) {
		AmazonSESSample mailer = new AmazonSESSample();
		SendMessagesDao dao = new SendMessagesDao();
		String seller_id = "A2G9KQ0CU8K2G9"; //TODO

		List<Map<String, String>> mailList = null;
    	//DBから取得 SentFlgが0（未送信）のデータ
		try {
			mailList = dao.scanThanksMailList(seller_id);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("***Failed to get a template***");
		}	
		if (mailList.isEmpty()) {
			System.out.println("There is no Messages to send");
		}
		//Send a message
		if (mailList.size()>0) {
			for(int i = 0; i < mailList.size(); i++) {
				String orderId = mailList.get(i).get("OrderId");
				String from = mailList.get(i).get("From_Email");
//				String to = mailList.get(i).get("To_Email"); TODO
				String to = "k.ima003365@gmail.com";
				String configSet = mailList.get(i).get("ConfigSet");
				String subject = mailList.get(i).get("Subject");
				String html = mailList.get(i).get("HTML");
    			String flat = mailList.get(i).get("FLAT");
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