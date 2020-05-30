package util;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.List;

//import com.google.common.base.Joiner;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;

// TSVファイル解析用クラス
// AMZNサーバから受け取る注文情報（TSV形式のバイトストリーム）をList型に変換する
public class TsvPerser {

	public List<String[]> parse(byte[] byteArray) throws Exception {
		TsvParserSettings settings = new TsvParserSettings();

		settings.getFormat().setLineSeparator("\r\n");
		settings.setHeaderExtractionEnabled(true);

		TsvParser parser = new TsvParser(settings);

		ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);
		InputStreamReader ir = new InputStreamReader(bais, "windows-31J");// Shift-JISで入力（UTF-8だと文字化けする）

		List<String[]> allRows = parser.parseAll(ir);
		return allRows;
	}
}