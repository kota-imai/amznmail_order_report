package exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.mws.samples.GetReportSample;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import config.SystemConfig;
import dataaccess.GetFbaShipmentReportDao;
import util.TsvPerser;
import util.UtilityTools;

public class GetFbaShipmentReport {

	public static void main(String[] args) throws Exception {
		String SellerId = SystemConfig.getSellerId(); //DB取得に変更する
		String MWSAuthToken = SystemConfig.getMwsAuthToken(); //
		
		//Get GeneratedIdList
		GetFbaShipmentReportDao dao = new GetFbaShipmentReportDao();
		List<Map<String, AttributeValue>> generatedIdList = new ArrayList<Map<String, AttributeValue>>();
		generatedIdList = dao.scanGeneratedId(SellerId);
		
		//Save Order Info in database
		if (generatedIdList.size()>0) {
			GetReportSample sample = new GetReportSample();
			TsvPerser parser = new TsvPerser();
			for (int i = 0 ;i<generatedIdList.size() ;i++) {
				String generatedReportId = generatedIdList.get(i).get("GeneratedReportId").getS();//generatedId取得
				List<String[]> itemList = null;
				try {
					itemList = parser.parse(sample.sendRequest(SellerId, MWSAuthToken, generatedReportId));
				} catch (Exception e) {
					System.out.println("***Failed to TSV perse*** generatedId:" + generatedReportId);
					e.printStackTrace();
					return;
				}
				try {
					dao.saveShipmentInfo(SellerId, itemList);
				} catch (Exception e) {
					System.out.println("***Failed to store data in DB*** generatedId:" + generatedReportId);
					e.printStackTrace();
					return;
				}
				dao.updateIssuedFlg(generatedReportId);
			}	
		} else {
			System.out.println("NO REPORT ARE THERE TO ISSUE");
			System.out.println("            TimeStamp : " + new UtilityTools().getCurrentTimeStamp());
		}
	}
}
