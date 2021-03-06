package exec;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import aws.AmazonSesMailer;
import config.SystemConfig;
import dataaccess.SendMessagesDao;
import mail.ThanksMessage;

public class SendThanksMessages {

	public static void main(String[] args) {
		AmazonSesMailer mailer = new AmazonSesMailer();
		SendMessagesDao dao = new SendMessagesDao();
		String seller_id = SystemConfig.getSellerId(); // TODO
		
		List<Map<String, String>> mailList = null;
		// SentFlgが0（未送信）のデータを取得する
		try {
			mailList = dao.scanThanksMailList(seller_id);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("***Failed to get a template***");
		}
		if (mailList.isEmpty()) {
			System.out.println("There is no Messages to send");
		}
		// メッセージを送信する
		if (mailList.size() > 0) {
			for (int i = 0; i < mailList.size(); i++) {
				String orderId = mailList.get(i).get("OrderId");
				// メールを作成
				
				ThanksMessage mail = ThanksMessage.getMsgInstance(
						mailList.get(i).get("From_Email"), //送信元
						"k.ima003365@gmail.com", //送信先
//						mailList.get(i).get("To_Email"),
						mailList.get(i).get("Subject"), //タイトル
						mailList.get(i).get("ConfigSet"), //SES設定ファイル
						mailList.get(i).get("HTML"), //本文HTML
						mailList.get(i).get("FLAT") //本文原文
						);
				
				try {
					if (mailer.sendMessage(mail)) { //メッセージ送信
						dao.updateSentStatus(orderId); //送信済みフラグを更新
					}
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("***Failed to send message (id = " + orderId + ")");
				}
			}
		}
	}
}