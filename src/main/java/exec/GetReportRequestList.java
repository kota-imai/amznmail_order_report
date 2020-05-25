package exec;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import com.amazonaws.mws.samples.GetReportRequestListSample;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import config.SystemConfig;
import dataaccess.GetReportRequestListDao;

public class GetReportRequestList {

	public static void main(String[] args) {
		GetReportRequestListDao dao = new GetReportRequestListDao();
		final String SELLER_ID = SystemConfig.getSellerId(); //DB取得に変更する
		final String MWS_AUTH_TOKEN = SystemConfig.getMwsAuthToken(); //
		
		List<String> ReportIdList = new ArrayList<String>();
		List<Map<String, AttributeValue>> resultmap = new ArrayList<Map<String, AttributeValue>>();
		
		//Get ReportIds from dynamoDB
		try {
			resultmap = dao.scanRequestIdWithSellerId(SELLER_ID);
			for(int i = 0; i < resultmap.size(); i++) {
				ReportIdList.add(resultmap.get(i).get("ReportRequestId").getS());
			}
			System.out.println("ReportIdList : "+ ReportIdList);
			resultmap.clear();//release memory
		} catch (NullPointerException nex) {
			System.out.println("NODATA FOUND WITH GIVEN QUERY");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//End the process if there is no active RequestId 
		if (ReportIdList.isEmpty()) {
			System.out.println("No requestId are there in the table");
			return;
		}
		
		//API Call
		GetReportRequestListSample sample = new GetReportRequestListSample();
		Map<String, Map<String, String>> map = sample.sendRequest(ReportIdList, SELLER_ID, MWS_AUTH_TOKEN);

		try {
			dao.saveGeneratedId(ReportIdList, map, SELLER_ID);
		} catch (Exception e) {
			System.out.println("***Failed to save generatedIds in DynamoDB***");
			e.printStackTrace();
		}
	}
}