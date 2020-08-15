package exec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import config.SystemConfig;
import dataaccess.GetFbaShipmentReportDao;
import mwssamples.GetReportSample;
import util.TsvPerser;
import util.UtilityTools;

public class GetFbaShipmentReport {

	public static void main(String[] args) throws Exception {
		String sellerId = SystemConfig.getSellerId(); 
		String mwsToken = SystemConfig.getMwsAuthToken(); 
		
		// 未発行のレポートIDを取得する
		GetFbaShipmentReportDao dao = new GetFbaShipmentReportDao();
		List<Map<String, AttributeValue>> generatedIdList = new ArrayList<Map<String, AttributeValue>>();
		generatedIdList = dao.scanGeneratedId(sellerId);
		
		// amznサーバから注文情報レポートを取得してDB保存する
		if (generatedIdList.size()>0) {
			GetReportSample sample = new GetReportSample();
			TsvPerser parser = new TsvPerser();
			
			for (int i = 0 ;i<generatedIdList.size() ;i++) {
				// 有効なgeneratedId取得
				String generatedReportId = generatedIdList.get(i).get("GeneratedReportId").getS();
				List<String[]> itemList = null;
				try {
					itemList = parser.parse(sample.sendRequest(sellerId, mwsToken, generatedReportId));
				} catch (Exception e) {
					System.out.println("***Failed to TSV perse*** generatedId:" + generatedReportId);
					e.printStackTrace();
					return;
				}
				
				// 出荷済み情報としてデータベースに登録
				try {
					dao.saveShipmentInfo(sellerId, itemList);
					
				} catch (Exception e) {
					System.out.println("***Failed to store data in DB*** generatedId:" + generatedReportId);
					e.printStackTrace();
					return;
				}
				
				// 出荷情報発行済みフラグを更新
				dao.updateIssuedFlg(generatedReportId);
			}
		} else {
			// データが無かったら
			System.out.println("NO REPORT ARE THERE TO ISSUE");
			System.out.println("            TimeStamp : " + new UtilityTools().getCurrentTimeStamp());
		}
	}
}
