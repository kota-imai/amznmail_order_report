package exec;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import config.SystemConfig;
import dataaccess.GetReportRequestListDao;
import mwssamples.GetReportRequestListSample;

public class GetReportRequestList {

	public static void main(String[] args) {
		GetReportRequestListDao dao = new GetReportRequestListDao();
		final String sellerId = SystemConfig.getSellerId(); // TODO DB取得に変更する
		final String mwsToken = SystemConfig.getMwsAuthToken(); //
		List<String> ReportIdList = new ArrayList<String>();
		List<Map<String, AttributeValue>> resultmap = new ArrayList<Map<String, AttributeValue>>();
		
		// レポートID未発行の要求IDを検索する
		try {
			resultmap = dao.scanRequestIdWithSellerId(sellerId);
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
			Map<String, Map<String, String>> map = sample.sendRequest(ReportIdList, sellerId, mwsToken);
			// レポートIDを保存する
			try {
				dao.saveGeneratedId(ReportIdList, map, sellerId);
			} catch (Exception e) {
				System.out.println("***Failed to save generatedIds in DynamoDB***");
				e.printStackTrace();
			}
		}
	}
}