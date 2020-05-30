package exec;

import config.SystemConfig;
import mwssamples.RequestReportSample;

// 2時間ごとにAMZNサーバに注文レポート作成の要求を送る
public class RequestReport2h {

	public static void main(String[] args) {
		String SellerId = SystemConfig.getSellerId(); // TODO データベース検索に変更
		String MWSAuthToken = SystemConfig.getMwsAuthToken(); //
		RequestReportSample sample = new RequestReportSample();
		sample.sendRequest2h(SellerId, MWSAuthToken);
	}
}
