package util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class UtilityTools {

	// 打刻（amznサーバ時間フォーマット）
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

	// amznサーバの時間フォーマットに合わせて時間オブジェクトを生成する
	public XMLGregorianCalendar getXMLGregCalendar(int year, int month, int day, int hour, int minute)
			throws DatatypeConfigurationException {
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

	// グリニッジカレンダ→文字列（"yyyy-MM-dd hh:mm:ss"）
	public String calendar2StrFormatter(XMLGregorianCalendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		GregorianCalendar gc = cal.toGregorianCalendar();
		String strCal = sdf.format(gc.getTime());
		return strCal;
	}

}
