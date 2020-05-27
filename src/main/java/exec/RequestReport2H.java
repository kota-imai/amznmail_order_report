package exec;

import com.amazonaws.mws.samples.RequestReportSample;

import config.SystemConfig;

public class RequestReport2H {

	public static void main(String[] args) {
		String SellerId = SystemConfig.getSellerId(); // TODO データベース検索に変更
		String MWSAuthToken = SystemConfig.getMwsAuthToken(); //
		RequestReportSample sample = new RequestReportSample();
		sample.sendRequest2H(SellerId, MWSAuthToken);
	}
}
