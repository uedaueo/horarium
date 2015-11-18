/**
 *
 */
package jp.horarium.specific;

/**
 * Browserごとに個別対応が必要な機能を表すインタフェイス
 * @author tueda
 *
 */
public interface Browser {

    /**
     * message で指定されたテキストを持つalert dialogを探し，Okボタンを押して閉じます．
     * @param alert dialgのメッセージ
     * @return windowの検索に失敗したらfalseを返します．
     */
    public boolean resolvAlertDialog(String message);

    /**
     * File Download Dialogを閉じます．
     * @param filename ダウンロードするファイル名．
     * @return 成功すれば true を返します．filenameがアプリケーションから渡されるファイル名と一致しない場合，このメソッドは失敗します．
     */
    public boolean resolvFileDownloadDialog(String filename);

    /**
     * ファイルダウンロードダイアログを閉じます．
     * @param filename ダウンロードするファイル名
     * @param isBrowserFileDownloadConfirm ダウンロード確認ダイアログが表示されることを期待する場合はtrueを渡します．
     * @param isBrowserFileDownloadDialog TODO
     * @param isBrowserFileDownloadOverrideConfirm 上書き確認ダイアログが表示されることを期待する場合はtrueを渡します．
     * @param isBrowserFileDownloadCompleteConfirm ダウンロード完了確認ダイアログが表示されることを期待する場合はtrueを渡します．
     * @return 成功すれば true を返します．filenameがアプリケーションから渡されるファイル名と一致しない場合，このメソッドは失敗します．
     */
    public boolean resolvFileDownloadDialog(
            String filename,
            boolean isBrowserFileDownloadConfirm,
            boolean isBrowserFileDownloadDialog,
            boolean isBrowserFileDownloadOverrideConfirm,
            boolean isBrowserFileDownloadCompleteConfirm);

    /**
     *
     * @param filename
     * @return
     */
    public boolean resolvFileUploadDialog(String filename);

}
