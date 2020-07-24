package dataaccess;

import java.sql.Connection;


import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataaccess.parents.MySqlDao;

public class SendMessagesDao extends MySqlDao{
	
	// 送信予定メール一覧から当日送信分を検索する
	public List<Map<String, String>> scanThanksMailList(String sellerid) throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		List<Map<String, String>> items = new ArrayList<Map<String, String>>();
		final String query = "select * from MailThanks_Vi "
				+ "where Sent = 0 " // Sentフラグが0 (未送信)
				+ "AND DATE(ScheduleDateOfDelivery) = DATE(NOW() + INTERVAL 9 HOUR) " // 配信予定日（JST）が当日
				+ "AND SellerId = '" + sellerid + "'"; // SellerIdがおなじ
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		this.init(); // 初期化MySQL接続用
		this.setSQL(query); // クエリを設定
		try {
			conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPass());
			ps = conn.prepareStatement(this.getSQL()); // クエリをセット
			rs = ps.executeQuery();
			while (rs.next()) {
				map.clear();
				map.put("OrderId", rs.getString("OrderId"));
				map.put("From_Email", rs.getString("From_Email"));
				map.put("To_Email", rs.getString("To_Email"));
				map.put("ConfigSet", rs.getString("ConfigSet"));
				map.put("Subject", rs.getString("Subject"));
				map.put("HTML", rs.getString("HTML"));
				map.put("FLAT", rs.getString("FLAT"));
				items.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			new Closer().closeConnection(conn, ps, rs); // クローズ処理追加
		}
		return items;
	}
	
	// 送信済みメールのステータスを反転する
	public void updateSentStatus(String orderId) {
		final String query = "update MailThanks "
				+ "set Sent = 1, " // 1(送信済み)
				+ "SentTime = (NOW() + INTERVAL 9 HOUR) " // 送信時刻（JST）
				+ "where OrderId = '" + orderId + "'";
		Connection conn = null;
		PreparedStatement ps = null;
		
		this.init(); // 初期化MySQL接続用
		this.setSQL(query); // クエリを設定
		try {
			conn = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPass());
			conn.setAutoCommit(false); // 自動コミットしない
			ps = conn.prepareStatement(this.getSQL()); //SQLをセット
			ps.executeUpdate(); // 更新
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} // 失敗したらロールバック
			System.out.println("***failed to update sentflg : orderid = " + orderId + " ***");
		} finally {
			new Closer().closeConnection(conn, ps); // クローズ処理
		}
	}
}
