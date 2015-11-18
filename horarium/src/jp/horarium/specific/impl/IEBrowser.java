/**
 *
 */
package jp.horarium.specific.impl;

import java.io.IOException;
import java.util.List;

import jp.horarium.framework.common.Const;
import jp.horarium.framework.common.LogLevel;
import jp.horarium.framework.common.Util;
import jp.horarium.specific.windows.WinConst;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;

/**
 * @author tueda
 *
 */
public class IEBrowser extends WindowsBrowser {
    @Override
    public boolean resolvAlertDialog(String message) {
        Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowsBrowser#resolveAlertDialg: CALLED with: [" + message + "]");
        boolean ret = true;

        try {
            /* alert dialog を探す */
            StructBrowserInfo bInfo = searchWindows(null, browserAlertWindowText, Const.PREFIX_SEARCH);
            List<NativeLong> hWnds = bInfo.getWindowHandles();

            for (NativeLong hWnd : hWnds) {
                /* 得られた window handle の Window Text を表示してみる */
                printWindowTextByHandle("Got Window", hWnd, LogLevel.LOG_DEBUG);

                /* message が一致するものを探す */
                StructBrowserInfo cInfo = searchWindows(hWnd, message, Const.PREFIX_SEARCH);
                List<NativeLong> hChldWnds = cInfo.getWindowHandles();

                /* 得られたchild window の window text を表示してみる(debug) */
                printWindowTextByHandle("\tChild Window Text", hChldWnds, LogLevel.LOG_DEBUG);

                if (hChldWnds.size() != 0) {
                    Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowsBrowser#resolveAlertDialog: alert found! : " + message);
                } else {
                    Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowsBrowser#resolveAlertDialog: Not alert! : " + message);
                    hWnds.remove(hWnd);
                    if (hWnds.size() == 0) {
                        break;
                    }
                }
            }

            NativeLong alert = null;
            if (hWnds.size() == 1) {
                Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowsBrowser#resolveAlertDialog: alert is unique! : " + message);
                alert = hWnds.get(0);

                /* OK button を探す */
                StructBrowserInfo oInfo = searchWindows(alert, browserAlertOkText, Const.COMPLETE_SEARCH);
                List<NativeLong> hCand = oInfo.getWindowHandles();
                if (hCand.size() == 1) {
                    NativeLong hOk = hCand.get(0);

                    Util.infoPrintln(LogLevel.LOG_DEBUG, "*** button found: " + hOk);

                    ret = pushButtonByHandle(alert, hOk, bInfo.getWindowThread(alert), 1000);

                } else if (hCand.size() > 1) {
                    Util.infoPrintln(LogLevel.LOG_ERR, "multiple OK buttons found:" + hCand.size());
                } else {
                    Util.infoPrintln(LogLevel.LOG_ERR, "no OK button found.");
                }
            } else if (hWnds.size() > 1) {
                Util.infoPrintln(LogLevel.LOG_ERR, "multiple alerts found: " + hWnds.size() + " : "+ message);
            } else {
                Util.infoPrintln(LogLevel.LOG_ERR, "no such alert: " + message);
            }
        } catch (LastErrorException e) {
            Util.infoPrintln(LogLevel.LOG_CRIT, e);

            byte [] lpMsgBuf = new byte[2048];

            kernel32.FormatMessageA(
                    WinConst.FORMAT_MESSAGE_FROM_SYSTEM |
                    WinConst.FORMAT_MESSAGE_IGNORE_INSERTS,
                    null,
                    e.getErrorCode(),
                    WinConst.MAKELANGID(WinConst.LANG_NEUTRAL, WinConst.SUBLANG_DEFAULT), // 既定の言語
                    lpMsgBuf,
                    2048,
                    null
                    );

            String msgBuf = Native.toString(lpMsgBuf, "MS932");

            Util.infoPrintln(LogLevel.LOG_ERR,
                    "IEBrowser#resolveAlertDialog: system error : code : "
                            + e.getErrorCode() + " : " + msgBuf);

        } catch (IOException e) {
            Util.infoPrintln(LogLevel.LOG_CRIT, e);
        } catch (ClassCastException e) {
            Util.infoPrintln(LogLevel.LOG_CRIT, e);
        } catch (ClassNotFoundException e) {
            Util.infoPrintln(LogLevel.LOG_CRIT, e);
        }

        return ret;
    }

    @Override
    public boolean resolvFileDownloadDialog(String filename) {
        return resolvFileDownloadDialog(
                filename,
                isBrowserFileDownloadConfirm(),
                isBrowserFileDownloadDialog(),
                isBrowserFileDownloadOverrideConfirm(), isBrowserFileDownloadCompleteConfirm());
    }

    @Override
    public boolean resolvFileDownloadDialog(
            String filename,
            boolean isBrowserFileDownloadConfirm,
            boolean isBrowserFileDownloadDialog,
            boolean isBrowserFileDownloadOverrideConfirm, boolean isBrowserFileDownloadCompleteConfirm) {
        Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowsBrowser#resolvFileDownloadDialog: CALLED with: [" + filename + "]");
        boolean ret = true;

        /* FileDialog を探す */
        try {
            if (isBrowserFileDownloadConfirm) {
                // ダウンロード確認ダイアログを処理する
                ret = processDialogWindow(browserFileDownloadConfirmText, Const.PREFIX_SEARCH, browserFileDownloadConfirmButton, Const.COMPLETE_SEARCH);

            }

            if (ret && isBrowserFileDownloadDialog) {
                // File Dialog を処理する
                ret = processDialogWindow(browserFileDownloadDialogText, Const.PREFIX_SEARCH, browserFileDownloadDialogButton, Const.COMPLETE_SEARCH);
            }

            if (ret && isBrowserFileDownloadOverrideConfirm) {
                // 上書き確認ダイアログを処理する
                ret = processDialogWindow(browserFileDownloadOverrideConfirmText, Const.PREFIX_SEARCH,
                        browserFileDownloadOverrideConfirmButton, Const.COMPLETE_SEARCH);
            }

            if (ret && isBrowserFileDownloadCompleteConfirm) {
                // 完了確認ダイアログを処理する
                ret = processDialogWindow(browserFileDownloadCompleteConfirmText, Const.PREFIX_SEARCH,
                        browserFileDownloadCompleteConfirmButton, Const.COMPLETE_SEARCH);
            }

        } catch (ClassCastException e) {
            Util.infoPrintln(LogLevel.LOG_CRIT, e);
        } catch (IOException e) {
            Util.infoPrintln(LogLevel.LOG_CRIT, e);
        } catch (ClassNotFoundException e) {
            Util.infoPrintln(LogLevel.LOG_CRIT, e);
        } catch (LastErrorException e) {
            Util.infoPrintln(LogLevel.LOG_CRIT, e);

            byte [] lpMsgBuf = new byte[2048];

            kernel32.FormatMessageA(
                    WinConst.FORMAT_MESSAGE_FROM_SYSTEM |
                    WinConst.FORMAT_MESSAGE_IGNORE_INSERTS,
                    null,
                    e.getErrorCode(),
                    WinConst.MAKELANGID(WinConst.LANG_NEUTRAL, WinConst.SUBLANG_DEFAULT), // 既定の言語
                    lpMsgBuf,
                    2048,
                    null
                    );

            String msgBuf = Native.toString(lpMsgBuf, "MS932");

            Util.infoPrintln(LogLevel.LOG_ERR,
                    "IEBrowser#resolvFileDownloadDialog: system error : code : "
                            + e.getErrorCode() + " : " + msgBuf);

            ret = false;
        }


        return ret;
    }

    @Override
    public boolean resolvFileUploadDialog(String filename) {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }
}
