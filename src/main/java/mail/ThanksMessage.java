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
	// 注文ID
	private String orderId;
	// 出品者ID
	private String sellerId;
	// 区分
	private String kubun;
	// 商品名
	private String productName;
	// 数量
	private String itemQuantity;
	// 到着予定日
	private String arrivalDate;
	
	public static ThanksMessage getInstance(
			String orderId,
			String sellerId,
			String productName,
			String itemQuantity,
			String toAddress,
			String fromAddress,
			String arrivalDate,
			String kubun
			){
		ThanksMessage msg = new ThanksMessage();
		msg.orderId = orderId;
		msg.sellerId = sellerId;
		msg.productName = productName;
		msg.itemQuantity = itemQuantity;
		msg.toAddress = toAddress;
		msg.fromAddress = fromAddress;
		msg.arrivalDate = arrivalDate;
		msg.kubun = kubun;
		return msg;
	}
	
	public static ThanksMessage getMsgInstance(
			String fromAddress,
			String toAddress,
			String subject,
			String configSet,
			String htmlBody,
			String textBody) {
		ThanksMessage msg = new ThanksMessage();
		msg.fromAddress = fromAddress;
		msg.toAddress = toAddress;
		msg.subject = subject;
		msg.htmlText = htmlBody;
		msg.flatText = textBody;
		return msg;
	}
	
	public String getFromAddress() {
		return fromAddress;
	}
//	public void setFromAddress(String fromAddress) {
//		this.fromAddress = fromAddress;
//	}
	public String getToAddress() {
		return toAddress;
	}
//	public void setToAddress(String toAddress) {
//		this.toAddress = toAddress;
//	}
	public String getConfigSet() {
		return configSet;
	}
//	public void setConfigSet(String configSet) {
//		this.configSet = configSet;
//	}
	public String getSubject() {
		return subject;
	}
//	public void setSubject(String subject) {
//		this.subject = subject;
//	}
	public String getHtmlText() {
		return htmlText;
	}
//	public void setHtmlText(String htmlText) {
//		this.htmlText = htmlText;
//	}
	public String getFlatText() {
		return flatText;
	}
//	public void setFlatText(String flatText) {
//		this.flatText = flatText;
//	}
	public String getOrderId() {
		return orderId;
	}
//	public void setOrderId(String orderId) {
//		this.orderId = orderId;
//	}
	public String getSellerId() {
		return sellerId;
	}
//	public void setSellerId(String sellerId) {
//		this.sellerId = sellerId;
//	}
	public String getKubun() {
		return kubun;
	}
	public void setKubun(String kubun) {
		this.kubun = kubun;
	}
	public String getProductName() {
		return productName;
	}
//	public void setProductName(String productName) {
//		this.productName = productName;
//	}
	public String getItemQuantity() {
		return itemQuantity;
	}
//	public void setItemQuantity(String itemQuantity) {
//		this.itemQuantity = itemQuantity;
//	}
	public String getArrivalDate() {
		return arrivalDate;
	}
//	public void setArrivalDate(String arrivalDate) {
//		this.arrivalDate = arrivalDate;
//	}
}
