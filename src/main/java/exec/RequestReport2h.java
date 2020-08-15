package exec;

import config.SystemConfig;
import mwssamples.RequestReportSample;

// 2時間ごとにamazonサーバに注文レポート作成の要求を送る
public class RequestReport2h {
	public static void main(String[] args) {
		final String SellerId = SystemConfig.getSellerId(); // TODO データベース検索に変更
		final String MWSAuthToken = SystemConfig.getMwsAuthToken(); // 秘密鍵をセット
		new RequestReportSample().sendRequest2h(SellerId, MWSAuthToken);
	}
}
