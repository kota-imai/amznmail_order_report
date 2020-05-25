package slack;

import java.io.BufferedInputStream;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class SlackComunicate {
	final private static String CHANNEL_NAME	= "webstore-monitoring";
	final private static String USERNAME		= "maidoari";
	final private static String URL				= "https://hooks.slack.com/services/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";
	

	public static String invokeCurlGet(String _host, int _connectTimeout, int _maxTimeout, int _maxResLength, Charset _charset) throws IOException{
		byte[] res = execute("curl --connect-timeout " + _connectTimeout + " --max-time " + _maxTimeout + " -X GET " + _host, _maxResLength);
	        return new String(res, _charset);
	    }
	public static byte[] execute(String _cmd, int _maxResLength) throws IOException{
		Process process = Runtime.getRuntime().exec(_cmd);
		try{
			int result = process.waitFor();
			if(result != 0){
	                throw new IOException("Fail to execute cammand. Exit Value[" + result + "], cmd => " + _cmd);
			}
		} catch(InterruptedException e) {
			process.destroyForcibly();
	        throw new IOException(e);
		}
		BufferedInputStream in = null;
		try{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			in = new BufferedInputStream(process.getInputStream());
			byte[] buf = new byte[1024];
			int read = 0;
	        while((read = in.read(buf)) != -1){
	        	out.write(buf, 0, read);
	        	out.flush();
	        	if(_maxResLength > 0 && out.size() > _maxResLength){
	        		throw new IOException("Response length exceeded.");
	        	}
	        }
	        return out.toByteArray();
		} finally {
			if(in != null){
				in.close();
			}
		}
	}
	
	//Curlスクリプト作成
	private String makeCurlScript(String channel_name, String username, String text, String url) {
		StringBuilder sb = new StringBuilder();
		sb.append("curl -X POST --data-urlencode ");
		sb.append("\"payload=");
		sb.append("{\\\"channel\\\": ");
		sb.append(" \\\"#" + channel_name + "\\\"");
		sb.append(", \\\"username\\\": ");
		sb.append("\\\"" + username + "\\\"");
		sb.append(", \\\"text\\\": ");
		sb.append("\\\"" + text + "\\\"");
		sb.append(", \\\"icon_emoji\\\": \\\":ghost:\\\"");
		sb.append("}\"");
		sb.append(" " + url);
System.out.println(sb.toString());
		return sb.toString();
	}
	
	//message部分作成
	public String makeMessage(String price, String title) {
		StringBuilder sb 	= new StringBuilder();
		return sb.toString();
	}
	
	//POST処理
	public void postSlackBot(String title, String price) throws Exception{
		String text = makeMessage(price, title);
		String curlScript = makeCurlScript(CHANNEL_NAME, USERNAME, text, URL);
//System.out.println(curlScript);//DEBUG
	    System.out.println(invokeCurlGet(curlScript, 3, 10, 0, Charset.defaultCharset()));
	}
}