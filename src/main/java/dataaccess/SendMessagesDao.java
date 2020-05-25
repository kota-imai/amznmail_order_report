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
	public List<Map<String, String>> scanThanksMailList(String sellerid) throws Exception {
		HashMap<String, String> map = new HashMap<String, String>();
    	List<Map<String, String>> items = new ArrayList<Map<String, String>>();
    	MysqlConfig sqlconf = new MysqlConfig();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        final String SQL = "select * from MailThanks_Vi where Sent = 0 " // Not Sent yet
        		+ "AND DATE(ScheduleDateOfDelivery) = DATE(NOW() + INTERVAL 9 HOUR) " // ScheduleDate = TODAY
        		+ "AND SellerId = '" + sellerid + "'" ;// SellerId
        try {
        	conn = DriverManager.getConnection(sqlconf.URL, sqlconf.USER, sqlconf.PASS);
        	ps = conn.prepareStatement(SQL);
        	rs = ps.executeQuery();
        	while (rs.next()) {
        		map.clear();
                map.put("OrderId",rs.getString("OrderId"));
                map.put("From_Email",rs.getString("From_Email"));
                map.put("To_Email",rs.getString("To_Email"));
                map.put("ConfigSet",rs.getString("ConfigSet"));
                map.put("Subject",rs.getString("Subject"));
                map.put("HTML",rs.getString("HTML"));
                map.put("FLAT",rs.getString("FLAT"));
                items.add(map);
        	}
        } catch (Exception e) {
            e.printStackTrace();
            //slackThrow
        } finally {
        	if (rs != null) {
        		rs.close();
        	}
        	if (ps != null) {
        		ps.close();
        	}
        	if (conn != null) {
        		conn.close();
        	}
        }
        return items;
    }
	
    public void updateSentStatus(String orderId) throws Exception {
        final String SQL = "update MailThanks set Sent = 1, SentTime = (NOW() + INTERVAL 9 HOUR) where OrderId = '" + orderId + "'";
        Connection conn = null;
        PreparedStatement ps = null;
    	MysqlConfig sqlconf = new MysqlConfig();
        try{
        	conn = DriverManager.getConnection(sqlconf.URL, sqlconf.USER, sqlconf.PASS);
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(SQL);
            ps.executeUpdate();
            conn.commit();
        } catch (Exception e) {
        	conn.rollback();
        	System.out.println("***failed to update sentflg : orderid = " + orderId + " ***");
        }finally {
        	if (ps != null) {
        		ps.close();
        	}
        	if (conn != null) {
        		conn.close();
        	}
        }
    }
}
