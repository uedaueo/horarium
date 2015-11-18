package jp.horarium.framework.dao;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import jp.horarium.framework.common.LogLevel;
import jp.horarium.framework.common.Util;


/**
 * 与えられたSQLスクリプトを読み込んで実行する．結果がある場合も捨ててしまいます．<br>
 * エラーが発生した場合はそこで中止．<br>
 * 重要：rollbackは行いません．<br>
 * @author tueda
 */
public class SimpleQuery extends DaoBase {

	/**
	 * SQLファイルを読み込んで実行する．<br>
	 * SQLの解析は手抜きなのであまり複雑なことをしてはいけません．<br>
	 * 一行ずつ読み込んで，<br>
	 * 1) 行先頭の -- をコメントと解釈<br>
	 * 2) 行終端の ; をSQL 文のデリミタと解釈<br>
	 * 行終端に ; を見つけるまで append して，実行します．<br>
	 * @param sqlscript SQLスクリプトファイル名（パス指定が必要）
	 * @return 成功の場合 true を返す．
	 */
	public boolean executeSqlFile(String sqlscript) {
		boolean ret = true;
		Util.infoPrintln(LogLevel.LOG_DEBUG, "SimpleQuery#executeSqlFile");

		try {
			FileInputStream is = new FileInputStream(sqlscript);
			String sql = null;
			while ((sql = readOneSQL(is)) != null) {
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	/**
	 * 単一のSQL文を実行します．
	 * @param sql SQL文
	 * @return
	 */
	public boolean executeSingleSQL(String sql) {
		boolean ret = true;

		return ret;
	}


	/**
	 *
	 * @param is 入力ファイルへのストリーム
	 * @return SQL文，それ以上SQL文がなければ null を返します．
	 * @throws IOException
	 */
	private String readOneSQL(FileInputStream is) throws IOException {
		String sql = null;

		return sql;
	}
}
