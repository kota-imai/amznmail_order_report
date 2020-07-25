package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;

import dataaccess.parents.MySqlDao;
import mail.ThanksMessage;

public class SaveThanksMessageDao extends MySqlDao {

	// お礼メールを保存する
	public void saveThanksMessage(ThanksMessage msg) throws Exception {
		final String orderId = msg.getOrderId();
		final String query = "insert into MailThanks ( OrderId , SellerId , Kbn , ProductName , ItemQuantity , To_Email"
				+ ", From_Email , EstimatedArrivalDate ) values ("
				+ encloseSingleQuote(orderId) + ","
				+ encloseSingleQuote(msg.getSellerId()) + ","
				+ encloseSingleQuote(msg.getKubun()) + ","
				+ encloseSingleQuote(msg.getProductName()) + "," 
				+ msg.getItemQuantity() + "," 
				+ encloseSingleQuote(msg.getToAddress()) + ","
				+ encloseSingleQuote(msg.getFromAddress()) + ","
				+ formatArrivalDate(msg.getArrivalDate())
				+ ")";
		Connection conn = null;
		PreparedStatement ps = null;
		
		this.init(); // 初期化MySQL接続用
		setSQL(query); // クエリを設定
		try {
			conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPass());
			conn.setAutoCommit(false); // 自動コミットしない
			ps = conn.prepareStatement(getSQL());
			ps.executeUpdate(); // 更新実行
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
	// クラス内メソッド
	// シングルクォーテーションで囲む
	private String encloseSingleQuote(String str) {
		return "'" + str + "'";
	}
	// Amznサーバで許容されている日付型に変換する
	private String formatArrivalDate(String str) {
		return "'" + str.substring(0, 19).replace("T", " ") + "'";
	}
}
