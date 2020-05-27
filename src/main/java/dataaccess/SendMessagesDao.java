package dataaccess;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
/*
 * Copyright 2012-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.MysqlConfig;

public class SendMessagesDao {
	
	// 送信予定メール一覧から当日送信分を検索する
	public List<Map<String, String>> scanThanksMailList(String sellerid) throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
		List<Map<String, String>> items = new ArrayList<Map<String, String>>();
		MysqlConfig sqlconf = new MysqlConfig();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		final String SQL = "select * from MailThanks_Vi "
				+ "where Sent = 0 " // Sentフラグが0 (未送信)
				+ "AND DATE(ScheduleDateOfDelivery) = DATE(NOW() + INTERVAL 9 HOUR) " // 配信予定日（JST）が当日
				+ "AND SellerId = '" + sellerid + "'"; // SellerIdがおなじ
		try {
			conn = DriverManager.getConnection(sqlconf.URL, sqlconf.USER, sqlconf.PASS);
			ps = conn.prepareStatement(SQL);
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
	public void updateSentStatus(String orderId) throws Exception {
		final String SQL = "update MailThanks "
				+ "set Sent = 1, " // 1(送信済み)
				+ "SentTime = (NOW() + INTERVAL 9 HOUR) " // 送信時刻（JST）
				+ "where OrderId = '" + orderId + "'";
		MysqlConfig config = new MysqlConfig(); //MySql設定ファイル読み込み
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DriverManager.getConnection(config.URL, config.USER, config.PASS);
			conn.setAutoCommit(false);
			ps = conn.prepareStatement(SQL);
			ps.executeUpdate();
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			System.out.println("***failed to update sentflg : orderid = " + orderId + " ***");
		} finally {
			new Closer().closeConnection(conn, ps); // クローズ処理
		}
	}
}
