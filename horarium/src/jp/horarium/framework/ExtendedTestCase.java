/**
 *
 */
package jp.horarium.framework;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import jp.horarium.framework.common.Config;
import jp.horarium.framework.common.LogLevel;
import jp.horarium.framework.common.Util;
import jp.horarium.framework.dao.DaoBase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;



/**
 * ExtendedSeleniumクラスを使用して自動テストを実行するためのframework<br>
 * <br>
 * JUnit4 フレームワークでは次の順序で実行される<br>
 * 1. Testクラスがロードされてstaticイニシャライザが走る<br>
 * 2. JUnitのバージョン表示がされる<br>
 * 3. @BeforeClassアノテーションのメソッドが呼ばれる。<br>
 * 4. 以下の内容をテストメソッドごとに繰り返す。<br>
 *   a. インスタンスイニシャライザとコンストラクタが呼ばれる。<br>
 *	 b. @Beforeアノテーションのメソッドが呼ばれる。<br>
 *	 c. テストメソッドが呼ばれる。<br>
 *	 d. @Afterアノテーションのメソッドが呼ばれる。<br>
 * 5. @AfterClassアノテーションのメソッドが呼ばれる。<br>
 * <br>
 * @author tueda
 *
 */
public abstract class ExtendedTestCase {

    static final String defSetting = "testSettings.xml";

    /** selenium serverが起動しているホスト名 */
    protected String serverHost = null;
    /** seleniumサーバのポート番号 */
    protected int serverPort = 0;
    /** 使用するbrowserを指定 */
    protected String browserStartCommand = null;
    /** テスト対象URL */
    protected String browserURL = null;

    /** DBエラー等テストの進行を妨げる要因が発生した場合はこのフラグをfalseにする */
    protected static boolean canProgress = true;

    /**
     * テストケースで使用するdaoのインスタンスを保持 <br>
     * daoはxmlファイルで設定することで，パッケージごと，クラスごとに設定できるが<br>
     * かならず DaoBase を継承していることが必要．
     */
    protected DaoBase dao = null;

    /**
     * デフォルトコンストラクタ
     */
	public ExtendedTestCase() {
    	Util.infoPrintln(LogLevel.LOG_DEBUG, "ExtendedTestCase#constructor");
        String myname = this.getClass().getName();
        System.out.println("myname: " + myname);
        String [] hierarchy = myname.split("\\.");

        /*
         * パッケージ・クラス毎の設定ファイルを読み込む
         * 期待している動作:<br>
         * 上位から順に読み込んで，重複するプロパティは下位が優先<br>
         * TODO そうなっているか，後で確認するとこ
         */
        String filename = "";
        for (int i = 0; i < hierarchy.length; i++) {
        	filename += hierarchy[i] + ".";
            try {
                read(filename + "xml");
            } catch (IOException e) {
            	Util.infoPrintln(LogLevel.LOG_ERR, e.getMessage());
            }
        }

        // just for debugging
        Config.getProps().list(System.out);

        serverHost = Config.getProperty(Config.serverHostKey, Config.serverHostDefault);
        serverPort = Integer.parseInt(Config.getProperty(Config.serverPortKey, Config.serverPortDefault));
        browserStartCommand = Config.getProperty(Config.browserStartCommandKey, Config.browserStartCommandDefault);
        browserURL = Config.getProperty(Config.browserURLKey, Config.browserURLDefault);

        // daoクラスのインスタンス化
        String daoClass = Config.getProperty(Config.daoClassKey, Config.daoClassDefault);
        try {
            @SuppressWarnings("unchecked")
			Class <? extends DaoBase> clazz = (Class<? extends DaoBase>)Class.forName(daoClass);
            dao = clazz.newInstance();
		} catch (ClassNotFoundException e) {
			Util.infoPrintln(LogLevel.LOG_ERR, "ExtendedTestClass#ExtendedTestClass: " + daoClass);
			Util.infoPrintln(LogLevel.LOG_ERR, e);
		} catch (InstantiationException e) {
			Util.infoPrintln(LogLevel.LOG_ERR, "ExtendedTestClass#ExtendedTestClass: " + daoClass);
			Util.infoPrintln(LogLevel.LOG_ERR, e);
		} catch (IllegalAccessException e) {
			Util.infoPrintln(LogLevel.LOG_ERR, "ExtendedTestClass#ExtendedTestClass: " + daoClass);
			Util.infoPrintln(LogLevel.LOG_ERR, e);
		}
    }

    @BeforeClass
    public static void prepare() {
        Util.infoPrintln(LogLevel.LOG_DEBUG, "ExtendedTestCase#prepare!");

        try {
            read(defSetting);
        } catch (FileNotFoundException ex) {
            System.err.println(ex.getMessage());
            canProgress = false;
        } catch (IOException ex) {
            ex.printStackTrace();
            canProgress = false;
        }

        /*
         * つまりDB接続情報はテスト全体で共通という前提
         */
        DaoBase.initDao();

        /*
         * 本当はここでパッケージごとの設定ファイルも読み込みたいが，派生クラスの
         * クラス名を取得することが困難なので，保留する．
         */
    }

    @Before
    public void setUp() {
        Util.infoPrintln(LogLevel.LOG_DEBUG, "ExtendedTestCase#setUp!");
    }

    @After
    public void tearDown() {
        Util.infoPrintln(LogLevel.LOG_DEBUG, "tearDown!");
    }

    @AfterClass
    public static void postposition() {
        Util.infoPrintln(LogLevel.LOG_DEBUG, "postposition!");
    }

    /**
     * 設定ファイルを読み込みます
     * @param filename 設定ファイル
     */
    private static void read(String filename) throws IOException {
    	Util.infoPrintln(LogLevel.LOG_DEBUG, "ExtendedTestCase#read");
        InputStream stream = ExtendedTestCase.class.getResourceAsStream("/" + filename);
        if (stream != null) {
            Config.getProps().loadFromXML(stream);
            stream.close();
        } else {
            throw new FileNotFoundException(filename + "がみつかりません");
        }
    }

}
