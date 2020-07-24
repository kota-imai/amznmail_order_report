package mail;

public class ThanksMessage {
	// 送信元アドレス
	private String fromAddress;
	// 送信先アドレス
	private String toAddress;
	// コンフィグファイル
	private String configSet;
	// タイトル
	private String subject;
	// HTML形式のメッセージ
	private String htmlText;
	// 平文のメッセージ
	private String flatText;
	
	public static ThanksMessage getInstance(){
		return new ThanksMessage();
	}
	public String getFromAddress() {
		return fromAddress;
	}
	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
	public String getToAddress() {
		return toAddress;
	}
	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}
	public String getConfigSet() {
		return configSet;
	}
	public void setConfigSet(String configSet) {
		this.configSet = configSet;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getHtmlText() {
		return htmlText;
	}
	public void setHtmlText(String htmlText) {
		this.htmlText = htmlText;
	}
	public String getFlatText() {
		return flatText;
	}
	public void setFlatText(String flatText) {
		this.flatText = flatText;
	}
}
