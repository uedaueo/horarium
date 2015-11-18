package jp.horarium.framework.dao;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jp.horarium.framework.common.Config;
import jp.horarium.framework.common.LogLevel;
import jp.horarium.framework.common.Util;


/**
 * Data Access Object<BR>
 * このクラスを継承して作成したDAOは全て同じDBに接続することに注意．
 * @author tueda
 *
 */
public class DaoBase {
	static String jdbcDriver = null;
	static String jdbcUrl = null;
	static String dbAccount = null;
	static String dbPasswd = null;

	protected static boolean daoInitialized = false;

	/** DBへのコネクション */
	protected Connection conn = null;
	/** SQLの設定用 */
	PreparedStatement stmt = null;

	public boolean isInitialized() {
		boolean ret = false;
		if (daoInitialized)
			ret = true;
		return ret;
	}

	/**
	 * JDBC接続情報の初期化を行います．
	 * @return 初期化に成功すれば true
	 */
	public static void initDao() {
		jdbcDriver = Config.getProperty(Config.jdbcDriverKey);
		jdbcUrl = Config.getProperty(Config.jdbcUrlKey);
		dbAccount = Config.getProperty(Config.jdbcUserKey);
		dbPasswd = Config.getProperty(Config.jdbcPasswdKey);

		if (jdbcDriver != null && jdbcUrl != null && dbAccount != null
				&& dbPasswd != null) {
			daoInitialized = true;
		}
		Util.infoPrintln(LogLevel.LOG_DEBUG, "DaoBase#initDao: connect to: "
				+ jdbcDriver + ", "
				+ jdbcUrl + ", "
				+ dbAccount + ", "
				+ dbPasswd);
	}

	/**
	 * DBへの接続を開始します
	 *
	 * @return 接続に成功すればtrue，失敗すればfalseを返します．
	 */
	public boolean connect(){
		boolean ret = false;
		Util.infoPrintln(LogLevel.LOG_DEBUG, "DaoBase#connect: connecting to: " + jdbcUrl);

		if (!isInitialized()) {
			Util.infoPrintln(LogLevel.LOG_CRIT, "DaoBase: Not Initialized.");
			return ret;
		}

		try{
			Class.forName(jdbcDriver);

			conn = DriverManager.getConnection(jdbcUrl, dbAccount, dbPasswd);
			conn.setAutoCommit(true);

			if (conn != null)
				ret = true;
			else
				Util.infoPrintln(LogLevel.LOG_DEBUG, "DaoBase#connect: failed to connecting to: " + jdbcUrl);
		} catch (SQLException e1){
			e1.printStackTrace();
		} catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * DBへの接続を切断します
	 * 最後には、必ず切断します
	 *
	 * @return 常にtrueを返します．
	 */
	public boolean disconnect(){

		try{
			if(stmt != null){
				stmt.close();
				stmt = null;
			}
		}
		catch (SQLException e2){
			e2.printStackTrace();
		}

		try{
			if(conn != null){
				conn.close();
				conn = null;
			}
		}
		catch (SQLException e2){
			e2.printStackTrace();
		}
		return true;
	}

	protected String escape(String s) throws SQLException {
		if (conn == null) {
			Util.infoPrintln(LogLevel.LOG_ERR, "DaoBase#escape: 接続されていません．");
			return s;
		}

		DatabaseMetaData meta = conn.getMetaData();
		String escape = meta.getSearchStringEscape();

		String oneWild = "_";
		String oneReplaceTo = escape + oneWild;
		String multiWild = "%";
		String multiReplaceTo = escape + multiWild;
		String escaped = s.replace(oneWild, oneReplaceTo);
		String retStr = escaped.replace(multiWild, multiReplaceTo);
		Util.infoPrintln(LogLevel.LOG_DEBUG, "#DaoBase#escape: escape to: " + retStr);
		return retStr;
	}

	/**
	 * 終了時に確実にconnectionを切断する
	 */
	@Override
	protected void finalize() {
		disconnect();
	}

}
