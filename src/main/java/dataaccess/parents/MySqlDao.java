package dataaccess.parents;

import config.MysqlConfig;

public class MySqlDao {
	private String SQL;
	protected MysqlConfig config;
	
	protected void init() {
		this.config = new MysqlConfig();
	}

	protected String getSQL() {
		return SQL;
	}

	protected void setSQL(String sql) {
		SQL = sql;
	}
}
