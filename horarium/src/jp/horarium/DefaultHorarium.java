/**
 *
 */
package jp.horarium;

import jp.horarium.specific.Browser;
import jp.horarium.specific.impl.IEBrowser;

import com.thoughtworks.selenium.DefaultSelenium;

/**
 * seleniumの動作を拡張するためのクラス
 *
 * @author tueda
 *
 */
public class DefaultHorarium extends DefaultSelenium {

    /**
     * Browser固有の機能を実装するクラス
     */
    Browser browser = null;

    public DefaultHorarium(String serverHost, int serverPort,
            String browserStartCommand, String browserURL) {
        super(serverHost, serverPort, browserStartCommand, browserURL);

        // TODO 将来的にはここでplatoformの種別を見て，呼び出すクラスを変更すること
        browser = new IEBrowser();

    }

    /**
     * onLoadから呼び出されたalert dialogをseleniumで扱えないため， 指定したメッセージを
     * 表示しているdialogを探し，OKボタンを押してalertを解決します．
     *
     * @param message
     *            alert dialgのメッセージ
     * @return 失敗したらfalseを返します．
     */
    public boolean getAlertOnLoad(String message) {
        boolean ret = true;

        ret = browser.resolvAlertDialog(message);
        return ret;
    }

    /**
     * ファイルダウンロードダイアログを処理するメソッドです．
     * ダウンロード先はBrowswerFileDownloadDirectoryプロパティで指定します．
     * @param filename downloadファイル名
     * @return
     */
    public boolean getFileDownloadDialog(String filename) {
        boolean ret = true;

        ret = browser.resolvFileDownloadDialog(filename);

        return ret;
    }

    /**
     * ファイルアップロードダイアログを処理するメソッドです．
     * アップロードファイルの保管ディレクトリはBrowserFileUploadDirectoryで指定します．
     * @param filename upload対象ファイル名
     * @return
     */
    public boolean getFileUploadDialog(String filename) {
        boolean ret = true;

        return ret;
    }

}
