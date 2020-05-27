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
		
		final String SELLER_ID = SystemConfig.getSellerId(); // TODO DB取得に変更する
		final String MWS_AUTH_TOKEN = SystemConfig.getMwsAuthToken(); //
		
		List<String> ReportIdList = new ArrayList<String>();
		List<Map<String, AttributeValue>> resultmap = new ArrayList<Map<String, AttributeValue>>();
		
		// レポートID未発行の要求IDを検索する
		try {
			resultmap = dao.scanRequestIdWithSellerId(SELLER_ID);
			for(int i = 0; i < resultmap.size(); i++) {
				ReportIdList.add(resultmap.get(i).get("ReportRequestId").getS());
			}
			System.out.println("ReportIdList : "+ ReportIdList);
			resultmap.clear();
		} catch (NullPointerException nex) {
			System.out.println("NODATA FOUND WITH GIVEN QUERY");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (ReportIdList.isEmpty()) { // 対象の要求IDがなかったらログ出力して終了
			System.out.println("There are No requestId in the table");
			return;
		} else { // 対象のデータがあれば
			// MWS_APIをコールする
			GetReportRequestListSample sample = new GetReportRequestListSample();
			Map<String, Map<String, String>> map = sample.sendRequest(ReportIdList, SELLER_ID, MWS_AUTH_TOKEN);
			
			// レポートIDを保存する
			try {
				dao.saveGeneratedId(ReportIdList, map, SELLER_ID);
			} catch (Exception e) {
				System.out.println("***Failed to save generatedIds in DynamoDB***");
				e.printStackTrace();
			}
		}
	}
}