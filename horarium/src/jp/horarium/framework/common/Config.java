/**
 *
 */
package jp.horarium.framework.common;

import java.util.Properties;

/**
 * @author tueda
 *
 */
public class Config {

    private static Properties props = new Properties();

    public static final String serverHostKey = "ServerHost";
    public static final String serverHostDefault = "localhost";

    public static final String serverPortKey = "ServerPort";
    public static final String serverPortDefault = "4444";

    public static final String browserURLKey = "BrowserURL";
    public static final String browserURLDefault = "http://dummytest.ueo.co.jp";

    public static final String jdbcDriverKey = "JdbcDriver";
    public static final String jdbcDriverDefault = "com.mysql.jdbc.Driver";

    public static final String jdbcUrlKey = "JdbcUrl";
    public static final String jdbcUrlDefault = "jdbc:mysql://localhost:3306/AUTO_2_1_02";

    public static final String jdbcUserKey = "JdbcUser";
    public static final String jdbcUserDefault = "autouser";

    public static final String jdbcPasswdKey = "JdbcPasswd";
    public static final String jdbcPasswdDefault = "autouser";

    public static final String defaultEncodingKey = "DefaultEncoding";
    public static final String defaultEncodingDefault = "MS932";

    public static final String waitForButtonToGetFocusKey ="WaitForButtonToGetFocus";
    public static final String waitForButtonToGetFocusDefault = "1000";

    public static final String waitForDialogToLoadKey ="WaitForDialogToLoad";
    public static final String waitForDialogToLoadDefault = "30000";

    public static final String logLevelKey = "LogLevel";
    public static final String legLevelDefault = "LOG_DEBUG";

    public static final String daoClassKey = "DaoClass";
    public static final String daoClassDefault = "jp.horarium.framework.dao.DaoBase";

    public static final String browserStartCommandKey = "BrowserStartCommand";
    public static final String browserStartCommandDefault = "*iexplore";

    public static final String browserExecutablePathKey = "BrowserExecutablePath";
    public static final String browserExecutablePathDefault = "C:\\Program Files\\Internet Explorer\\iexplore.exe";

    public static final String browserAlertWindowTextKey = "BrowserAlertWindowText";
    public static final String browserAlertWindowTextDefault = "Web ページからのメッセージ";

    public static final String browserAlertOkTextKey = "BrowserALertOkText";
    public static final String browserAlertOkTextDefault = "OK";

    public static final String browserFileDownloadConfirmKey = "BrowserFileDownloadConfirm";
    public static final String browserFileDownloadConfirmDefault = "true";

    public static final String browserFileDownloadConfirmTextKey = "BrowserFileDownloadConfirmText";
    public static final String browserFileDownloadConfirmTextDefault = "ファイルのダウンロード";

    public static final String browserFileDownloadConfirmButtonKey = "BrowserFileDownloadConfirmButton";
    public static final String browserFileDownloadConfirmButtonDefault = "保存(S)";

    public static final String browserFileDownloadDialogKey = "BrowserFileDownloadDialog";
    public static final String browserFileDownloadDialogDefault ="true";

    public static final String browserFileDownloadDialogTextKey = "BrowserFileDownloadDialogText";
    public static final String browserFileDownloadDialogTextDefault ="名前を付けて保存";

    public static final String browserFileDownloadDialogButtonKey = "BrowserFileDownloadDialogButton";
    public static final String browserFileDownloadDialogButtonDefault = "保存(S)";

    public static final String browserFileDownloadOverrideConfirmKey = "BrowserFileDownloadOverrideConfirm";
    public static final String browserFileDownloadOverrideConfirmDefault = "false";

    public static final String browserFileDownloadOverrideConfirmTextKey = "BrowserFileDownloadOverrideConfirmText";
    public static final String browserFileDownloadOverrideConfirmTextDefault = "名前を付けて保存の確認";

    public static final String browserFileDownloadOverrideConfirmButtonKey ="BrowserFileDownloadOverrideConfirmButton";
    public static final String browserFileDownloadOverrideConfirmButtonDefault = "はい(Y)";

    public static final String browserFileDownloadCompleteConfirmKey = "BrowserFileDownloadCompleteConfirm";
    public static final String browserFileDownloadCompleteConfirmDefault = "true";

    public static final String browserFileDownloadCompleteConfirmTextKey = "BrowserFileDownloadCompleteConfirmText";
    public static final String browserFileDownloadCompleteConfirmTextDefault = "ダウンロードの完了";

    public static final String browserFileDownloadCompleteConfirmButtonKey = "BrowserFileDownloadCompleteConfirmButton";
    public static final String browserFileDownloadCompleteConfirmButtonDefault = "閉じる";


    public static final String browserFileDownloadDirectoryKey = "BrowserFileDownloadDirectory";
    public static final String browserFileDownloadDirectoryDefault = "E:\\tueda\\archives\\";
    
    public static final String cygwinBinDirKey = "CygwinDir";
    public static final String cygwinBinDirDefault = "C:\\cygwin64\\bin\\";

    /**
     * @return props
     */
    public static Properties getProps() {
        return props;
    }

    /**
     * Property値を取得します．
     *
     * @param key Propertyのkey値
     * @return value値
     */
    public static String getProperty(String key){
    	return (String)props.get(key);
    }

    /**
     * Property値を取得します
     * @param key Propertyのkey値
     * @param defaultValue デフォルト値
     * @return
     */
    public static String getProperty(String key, String defaultValue) {
        return (String)props.getProperty(key, defaultValue);
    }

}
