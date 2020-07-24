package aws;

import java.io.IOException;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

import mail.ThanksMessage;

// AWSドキュメントSampleコードを引用
public class AmazonSesMailer {

	public boolean sendMessage(ThanksMessage msg) throws IOException {
		// 戻り値用（送信成功true, 失敗false
		boolean rc = true;
		
		try {
			AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
					.withRegion(Regions.AP_SOUTHEAST_2).build(); // Tokyo リージョン
			SendEmailRequest request = new SendEmailRequest()
					.withDestination(
							new Destination().withToAddresses(msg.getToAddress()))
					.withMessage(new Message()
							.withBody(
									new Body().withHtml(new Content().withCharset("UTF-8").withData(msg.getHtmlText()))
											.withText(new Content().withCharset("UTF-8").withData(msg.getFlatText())))
							.withSubject(new Content().withCharset("UTF-8").withData(msg.getSubject())))
					.withSource(msg.getFromAddress()).withConfigurationSetName(msg.getConfigSet());
			client.sendEmail(request);
			System.out.println("Email sent!");
		} catch (Exception ex) {
			System.out.println("The email was not sent. Error message: " + ex.getMessage());
			rc = false;
		}
		return rc;
	}
}