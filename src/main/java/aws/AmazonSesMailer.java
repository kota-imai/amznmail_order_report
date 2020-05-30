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

// AWSドキュメントSampleコードを引用
public class AmazonSesMailer {

  final static String FROM = "kota.imai@firmimai.biz"; // 送信元アドレス、必要なら変更

  public void sendMessage(String from, String to, String configset, String subject, String html, String flat)
		  												throws IOException {
    try {
      AmazonSimpleEmailService client = 
          AmazonSimpleEmailServiceClientBuilder.standard()
            .withRegion(Regions.AP_SOUTHEAST_2).build(); // Tokyo リージョン
      SendEmailRequest request = new SendEmailRequest()
          .withDestination(
              new Destination().withToAddresses(to))
          .withMessage(new Message()
              .withBody(new Body()
                  .withHtml(new Content()
                      .withCharset("UTF-8").withData(html))
                  .withText(new Content()
                      .withCharset("UTF-8").withData(flat)))
              .withSubject(new Content()
                  .withCharset("UTF-8").withData(subject)))
          .withSource(from)
          .withConfigurationSetName(configset);
      client.sendEmail(request);
      System.out.println("Email sent!");
    } catch (Exception ex) {
      System.out.println("The email was not sent. Error message: " 
          + ex.getMessage());
    }
  }
}