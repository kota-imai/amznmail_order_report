package util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class UtilityTools {
	public String replaceVariable(String str, String orderId, String shopName, String productName, String itemQuantity) {
		String replaced_str = new String();
		replaced_str = str.replace("{{feedback_url}}", "https://www.amazon.co.jp/hz/feedback/?_encoding=UTF8&orderID={{amazon_order_id}}");
		replaced_str = replaced_str.replace("{{shop_name}}", shopName);
		replaced_str = replaced_str.replace("{{product_name}}", productName);
		replaced_str = replaced_str.replace("{{item_quantity}}", itemQuantity);
		replaced_str = replaced_str.replace("{{amazon_order_id}}", orderId);
		return replaced_str;
	}
	
	public String getCurrentTimeStamp() {
		Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf_day = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(sdf_day.format(cal.getTime()));
        sb.append("T");
        sb.append(sdf_time.format(cal.getTime()));
        return sb.toString();
	}
	
	public XMLGregorianCalendar getXMLGregCalendar(int year, int month, int day, int hour , int minute) throws DatatypeConfigurationException {
        Date date = new Date();
        String formatter = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        DateFormat format = new SimpleDateFormat(formatter);
        XMLGregorianCalendar xmlGregCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(format.format(date));
        
        xmlGregCal.setYear(year);
        xmlGregCal.setMonth(month);
        xmlGregCal.setDay(day);
        xmlGregCal.setHour(hour);
        xmlGregCal.setMinute(00);
        xmlGregCal.setSecond(00);
        xmlGregCal.setTimezone(0);
		return xmlGregCal;
	}
    public String Cal2StrFormatter (XMLGregorianCalendar cal) {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	GregorianCalendar gc = cal.toGregorianCalendar();
    	String formatted_string = sdf.format(gc.getTime());
		return formatted_string;
    }
}
