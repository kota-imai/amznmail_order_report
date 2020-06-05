package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;

import dataaccess.parents.MySqlDao;

public class SaveThanksMessageDao extends MySqlDao {

	// お礼メールを保存する
	public void saveThanksMessage(String orderId, String sellerId, String kbn, String productName, String itemQuantity,
			String to, String from, String arrival) throws Exception {
		
		final String query = "insert into MailThanks" + "( OrderId" + ", SellerId" + ", Kbn" + ", ProductName"
				+ ", ItemQuantity" + ", To_Email" + ", From_Email" + ", EstimatedArrivalDate" + " ) values ("
				+ encloseSingleQuote(orderId) + "," + encloseSingleQuote(sellerId) + "," + encloseSingleQuote(kbn) + ","
				+ encloseSingleQuote(productName) + "," + itemQuantity + "," + encloseSingleQuote(to) + ","
				+ encloseSingleQuote(from) + "," + formatArrivalDate(arrival) + ")";
		Connection conn = null;
		PreparedStatement ps = null;
		
		this.init(); // 初期化MySQL接続用
		setSQL(query); // クエリを設定
		try {
			conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPass());
			conn.setAutoCommit(false); // 自動コミットしない
			ps = conn.prepareStatement(getSQL());
			ps.executeUpdate(); // 検索実行
			conn.commit();
			
		} catch (SQLIntegrityConstraintViolationException de) { // 同一の注文IDがあった場合
			conn.rollback(); // 更新に失敗したらロールバック
			System.out.println("There is the record in MailThanks table / OrderId = " + orderId);
			return;
			
		} catch (Exception e) {
			conn.rollback(); // 更新に失敗したらロールバック
			System.out.println("***failed to Insert MailThanks : orderid = " + orderId + " ***");
			e.printStackTrace();
			throw new Exception(); // 実行クラスでエラーハンドルあり
			
		} finally {
			new Closer().closeConnection(conn, ps); // クローズ処理
		}
	}
	
	// クラス内共有メソッド
	// シングルクォーテーションで囲む
	private String encloseSingleQuote(String str) {
		return "'" + str + "'";
	}

	// Amznサーバで許容されている日付型に変換する
	private String formatArrivalDate(String str) {
		return "'" + str.substring(0, 19).replace("T", " ") + "'";
	}
}
