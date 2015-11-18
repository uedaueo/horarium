/**
 *
 */
package jp.horarium.specific.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import jp.horarium.framework.common.Config;
import jp.horarium.framework.common.LogLevel;
import jp.horarium.framework.common.Util;
import jp.horarium.specific.Browser;
import jp.horarium.specific.windows.Kernel32;
import jp.horarium.specific.windows.Psapi;
import jp.horarium.specific.windows.User32;
import jp.horarium.specific.windows.WinConst;

import com.sun.jna.LastErrorException;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

/**
 * InternetExplorerに特化した操作を行うクラスです．
 *
 * @author tueda
 *
 */
public abstract class WindowsBrowser implements Browser {

    /** user32.dll で提供されるWindows API へのインタフェイス */
    final User32 user32 = User32.INSTANCE;
    /** kernel32.dll で提供されるWindows API へのインタフェイス */
    final Kernel32 kernel32 = Kernel32.INSTANCE;
    /** psapi.dll で提供されるWindows API へのインタフェイス */
    final Psapi psapi = Psapi.INSTANCE;

    /** client の default encoding */
    protected String defaultEncoding = null;

    /** ブラウザの実行イメージのフルパス */
    protected String browserExecutablePath = null;
    /** Alert Dialog の Window Text */
    protected String browserAlertWindowText = null;
    /** Alert Dialog の OK ボタンキャプション */
    protected String browserAlertOkText = null;

    /** ファイルをダウンロードするかどうかの確認ダイアログが表示される場合にはtrueを設定する */
    protected String browserFileDownloadConfirm = null;
    /** ファイルをダウンロードするかどうかの確認ダイアログの Window Text */
    protected String browserFileDownloadConfirmText = null;
    /** ファイルをダウンロードするかどうかの確認ダイアログに対して押下するボタン */
    protected String browserFileDownloadConfirmButton = null;
    /** File Donwload dialogが表示される場合にはtrueを設定する */
    protected String browserFileDownloadDialog = null;
    /** FileDialog の Window Text */
    protected String browserFileDownloadDialogText = null;
    /** FileDialog に対して押下するボタン */
    protected String browserFileDownloadDialogButton = null;
    /** 保存ファイルの上書き確認ダイアログが表示される場合にはtrueを設定する */
    protected String browserFileDownloadOverrideConfirm = null;
    /** 上書き確認ダイアログのwindow text */
    protected String browserFileDownloadOverrideConfirmText = null;
    /** 上書き確認ダイアログに対して押下するボタン */
    protected String browserFileDownloadOverrideConfirmButton = null;
    /** ダウンロード完了確認ダイアログが表示される場合にはtrueを設定する */
    protected String browserFileDownloadCompleteConfirm = null;
    /** ダウンロード完了確認ダイアログのwindow text */
    protected String browserFileDownloadCompleteConfirmText = null;
    /** ダウンロード完了確認ダイアログに対して押下するボタン */
    protected String browserFileDownloadCompleteConfirmButton = null;
    /** ダウンロード先ディレクトリ */
    protected String browserFileDownloadDirectory = null;

    /**
     * デフォルトコンストラクタ
     */
    public WindowsBrowser() {

        setDefaultEncoding(Config.getProperty(
                Config.defaultEncodingKey, Config.defaultEncodingDefault));

        /* ここで指定するパスは主に，ブラウザの識別のために使用される */
        setBrowserExecutablePath(Config.getProperty(
                Config.browserExecutablePathKey,
                Config.browserExecutablePathDefault));

        setBrowserAlertWindowText(Config.getProperty(
                Config.browserAlertWindowTextKey,
                Config.browserAlertWindowTextDefault));

        setBrowserAlertOkText(Config.getProperty(
                Config.browserAlertOkTextKey,
                Config.browserAlertOkTextDefault));

        setBrowserFileDownloadConfirm(Config.getProperty(
                Config.browserFileDownloadConfirmKey,
                Config.browserFileDownloadConfirmDefault));

        setBrowserFileDownloadConfirmText(Config.getProperty(
                Config.browserFileDownloadConfirmTextKey,
                Config.browserFileDownloadConfirmTextDefault));

        setBrowserFileDownloadConfirmButton(Config.getProperty(
                Config.browserFileDownloadConfirmButtonKey,
                Config.browserFileDownloadConfirmButtonDefault));

        setBrowserFileDownloadDialog(Config.getProperty(
                Config.browserFileDownloadDialogKey,
                Config.browserFileDownloadDialogDefault));

        setBrowserFileDownloadDialogText(Config.getProperty(
                Config.browserFileDownloadDialogTextKey,
                Config.browserFileDownloadDialogTextDefault));

        setBrowserFileDownloadDialogButton(Config.getProperty(
                Config.browserFileDownloadDialogButtonKey,
                Config.browserFileDownloadDialogButtonDefault));

        setBrowserFileDownloadOverrideConfirm(Config.getProperty(
                Config.browserFileDownloadOverrideConfirmKey,
                Config.browserFileDownloadOverrideConfirmDefault));

        setBrowserFileDownloadOverrideConfirmText(Config.getProperty(
                Config.browserFileDownloadOverrideConfirmTextKey,
                Config.browserFileDownloadOverrideConfirmTextDefault));

        setBrowserFileDownloadOverrideConfirmButton(Config.getProperty(
                Config.browserFileDownloadOverrideConfirmButtonKey,
                Config.browserFileDownloadOverrideConfirmButtonDefault));

        setBrowserFileDownloadCompleteConfirm(Config.getProperty(
                Config.browserFileDownloadCompleteConfirmKey,
                Config.browserFileDownloadCompleteConfirmDefault));

        setBrowserFileDownloadCompleteConfirmText(Config.getProperty(
                Config.browserFileDownloadCompleteConfirmTextKey,
                Config.browserFileDownloadCompleteConfirmTextDefault));

        setBrowserFileDownloadCompleteConfirmButton(Config.getProperty(
                Config.browserFileDownloadCompleteConfirmButtonKey,
                Config.browserFileDownloadCompleteConfirmButtonDefault));

        setBrowserFileDownloadDirectory(Config.getProperty(
                Config.browserFileDownloadDirectoryKey,
                Config.browserFileDownloadDirectoryDefault));

    }

    /**
     * BrowserのWindowハンドルを探します．Window Textを指定することで確実性を上げることができます．
     * @param parent 親Windowのハンドル
     * @param windowTitle 検索するWindow Text（前方一致）
     * @param searchMode TODO
     * @return 見つかったWindow HandleのList
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws ClassCastException
     */
    protected StructBrowserInfo searchWindows(NativeLong parent, String windowTitle, int searchMode) throws IOException, ClassCastException, ClassNotFoundException {
        Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowBrowser#searchWindows: CALLED " +
                ": parent=" + parent +
                ": text  =[" + windowTitle + "]" +
                ": mode  =" + searchMode);
        /*
         * StructBrowserInfoにWindow Titleをセットし，シリアライズした上でPointer型に変換する．
         */
        StructBrowserInfo bInfo = new StructBrowserInfo();
        bInfo.setWindowTitle(windowTitle);
        bInfo.setWindowHandles(new ArrayList<NativeLong>());
        bInfo.setWindowThreads(new HashMap<NativeLong, Integer>( ));
        bInfo.setSearchMode(searchMode);
        byte [] sInfo;
        sInfo = bInfo.serialize();

        Pointer pInfo = new Memory(sInfo.length);
        pInfo.write(0, sInfo, 0, sInfo.length);

        /*
         * データを受け取りたいので，Pointerへの参照として引き渡す
         */
        PointerByReference ppInfo = new PointerByReference(pInfo);

        /* 指定レベルの子Windowを検索する */
        user32.EnumChildWindows(parent, new FindBrowser(), ppInfo);


        /* ポインタ経由で戻ってきた情報をdeserializeする */
        pInfo = ppInfo.getValue();
        int pSize = WindowsBrowser.getHeadingIntFromPointer(pInfo);
        sInfo = pInfo.getByteArray(0, pSize);
        bInfo = StructBrowserInfo.deserialize(sInfo);
        //        List<NativeLong> hWnds = bInfo.getWindowHandles();

        return bInfo;

    }

    /**
     * EnumWindowsでIEを探すためのデータブロックの先頭4バイトからデータ列の長さを取得します
     * @param ptr
     * @return
     * @throws IOException
     */
    public static int getHeadingIntFromPointer(Pointer ptr) throws IOException {
        byte [] headings = ptr.getByteArray(0, WinConst.BROWSER_INFO_HEADING_LEN);
        ByteArrayInputStream bais = new ByteArrayInputStream(headings);
        DataInputStream dis = new DataInputStream(bais);

        return dis.readInt();
    }


    /**
     *
     * @return defaultEncodingを返します
     */
    public String getDefaultEncoding() {
        return defaultEncoding;
    }

    /**
     * defaultEncoding をセットします
     * @param defaultEncoding
     */
    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    /**
     *
     * @return browserExecutablePathを返します．
     */
    public String getBrowserExecutablePath() {
        return browserExecutablePath;
    }

    /**
     * browserExecutablePathをセットします．
     * @param browserExecutablePath
     */
    public void setBrowserExecutablePath(String browserExecutablePath) {
        this.browserExecutablePath = browserExecutablePath;
    }

    /**
     *
     * @return browserAlertWindowTextを返します
     */
    public String getBrowserAlertWindowText() {
        return browserAlertWindowText;
    }


    /**
     * browserAlertWindowText をセットします
     * @param str
     */
    public void setBrowserAlertWindowText(String str) {
        this.browserAlertWindowText = str;
    }

    /**
     * browserAlertOkTextを返します
     * @return
     */
    public String getBrowserAlertOkText() {
        return browserAlertOkText;
    }

    /**
     * browserAlertOkTextをセットします
     * @param str
     */
    public void setBrowserAlertOkText(String str) {
        this.browserAlertOkText = str;
    }

    /**
     * browserFileDownloadConfirmを返します
     * @return
     */
    public String getBrowserFileDownloadConfirm() {
        return browserFileDownloadConfirm;
    }

    /**
     * browserFileDownloadConfirmをセットします
     * @param browserFileDownloadConfirm
     */
    public void setBrowserFileDownloadConfirm(String browserFileDownloadConfirm) {
        this.browserFileDownloadConfirm = browserFileDownloadConfirm;
    }

    /**
     * browserFileDownloadConfirmTextを返します
     * @return
     */
    public String getBrowserFileDownloadConfirmText() {
        return browserFileDownloadConfirmText;
    }

    /**
     * browserFileDownloadConfirmTextをセットします
     * @param browserFileDownloadConfirmText
     */
    public void setBrowserFileDownloadConfirmText(
            String browserFileDownloadConfirmText) {
        this.browserFileDownloadConfirmText = browserFileDownloadConfirmText;
    }

    /**
     * browserFileDownloadDialogを返します
     * @return
     */
    public String getBrowserFileDownloadDialog() {
        return browserFileDownloadDialog;
    }

    /**
     * browserFileDownloadDialogをセットします．
     * @param browserFileDownloadDialog
     */
    public void setBrowserFileDownloadDialog(String browserFileDownloadDialog) {
        this.browserFileDownloadDialog = browserFileDownloadDialog;
    }

    /**
     * browserFileDownloadDialogTextを返します
     * @return
     */
    public String getBrowserFileDownloadDialogText() {
        return browserFileDownloadDialogText;
    }

    /**
     * browserFileDownloadDialogTextを返します
     * @param browserFileDownloadDialogTex
     */
    public void setBrowserFileDownloadDialogText(String browserFileDownloadDialogTex) {
        this.browserFileDownloadDialogText = browserFileDownloadDialogTex;
    }

    /**
     * browserFileDownloadOverrideConfirmを返します
     * @return
     */
    public String getBrowserFileDownloadOverrideConfirm() {
        return browserFileDownloadOverrideConfirm;
    }

    /**
     * browserFileDownloadOverrideConfirmをセットします
     * @param browserFileDownloadOverrideConfirm
     */
    public void setBrowserFileDownloadOverrideConfirm(
            String browserFileDownloadOverrideConfirm) {
        this.browserFileDownloadOverrideConfirm = browserFileDownloadOverrideConfirm;
    }

    /**
     * browserFileDownloadOverrideConfirmTextを返します
     * @return
     */
    public String getBrowserFileDownloadOverrideConfirmText() {
        return browserFileDownloadOverrideConfirmText;
    }

    /**
     * browserFileDownloadOverrideConfirmTextをセットします
     * @param browserFileDownloadOverrideConfirmText
     */
    public void setBrowserFileDownloadOverrideConfirmText(
            String browserFileDownloadOverrideConfirmText) {
        this.browserFileDownloadOverrideConfirmText = browserFileDownloadOverrideConfirmText;
    }

    /**
     * browserFileDownloadCompleteConfirmを返します
     * @return
     */
    public String getBrowserFileDownloadCompleteConfirm() {
        return browserFileDownloadCompleteConfirm;
    }

    /**
     * browserFileDownloadCompleteConfirmをセットします
     * @param browserFileDownloadCompleteConfirm
     */
    public void setBrowserFileDownloadCompleteConfirm(
            String browserFileDownloadCompleteConfirm) {
        this.browserFileDownloadCompleteConfirm = browserFileDownloadCompleteConfirm;
    }

    /**
     * browserFileDownloadCompleteConfirmTextを返します
     * @return
     */
    public String getBrowserFileDownloadCompleteConfirmText() {
        return browserFileDownloadCompleteConfirmText;
    }

    /**
     * browserFileDownloadCompleteConfirmTextをセットします
     * @param browserFileDownloadCompleteConfirmText
     */
    public void setBrowserFileDownloadCompleteConfirmText(
            String browserFileDownloadCompleteConfirmText) {
        this.browserFileDownloadCompleteConfirmText = browserFileDownloadCompleteConfirmText;
    }

    /**
     * browserFileDownloadDirectoryを返します
     * @return
     */
    public String getBrowserFileDownloadDirectory() {
        return browserFileDownloadDirectory;
    }

    /**
     * browserFileDownloadDirectoryをセットします
     * @param browserFileDownloadDirectory
     */
    public void setBrowserFileDownloadDirectory(String browserFileDownloadDirectory) {
        this.browserFileDownloadDirectory = browserFileDownloadDirectory;
    }

    /**
     * browserFileDownloadConfirmButtonを返します
     * @return
     */
    public String getBrowserFileDownloadConfirmButton() {
        return browserFileDownloadConfirmButton;
    }

    /**
     * browserFileDownloadConfirmButtonをセットします
     * @param browserFileDownloadConfirmButton
     */
    public void setBrowserFileDownloadConfirmButton(
            String browserFileDownloadConfirmButton) {
        this.browserFileDownloadConfirmButton = browserFileDownloadConfirmButton;
    }

    /**
     * browserFileDownloadDialogButtonを返します
     * @return
     */
    public String getBrowserFileDownloadDialogButton() {
        return browserFileDownloadDialogButton;
    }

    /**
     * browserFileDownloadDialogButtonをセットします
     * @param browserFileDownloadDialogButton
     */
    public void setBrowserFileDownloadDialogButton(
            String browserFileDownloadDialogButton) {
        this.browserFileDownloadDialogButton = browserFileDownloadDialogButton;
    }

    /**
     * browserFileDownloadOverrideConfirmButtonを返す
     * @return
     */
    public String getBrowserFileDownloadOverrideConfirmButton() {
        return browserFileDownloadOverrideConfirmButton;
    }

    /**
     * browserFileDownloadOverrideConfirmButtonをセットする
     * @param browserFileDownloadOverrideConfirmButton
     */
    public void setBrowserFileDownloadOverrideConfirmButton(
            String browserFileDownloadOverrideConfirmButton) {
        this.browserFileDownloadOverrideConfirmButton = browserFileDownloadOverrideConfirmButton;
    }

    /**
     * browserFileDownloadCompleteConfirmButtonを返す
     * @return
     */
    public String getBrowserFileDownloadCompleteConfirmButton() {
        return browserFileDownloadCompleteConfirmButton;
    }

    /**
     * browserFileDownloadCompleteConfirmButtonをセットする
     * @param browserFileDownloadCompleteConfirmButton
     */
    public void setBrowserFileDownloadCompleteConfirmButton(
            String browserFileDownloadCompleteConfirmButton) {
        this.browserFileDownloadCompleteConfirmButton = browserFileDownloadCompleteConfirmButton;
    }

    /**
     * Window Handle の List から Window Text を表示する．
     * おもにdebug用途を想定しているので，引数にLogLevelをとる．
     * @param hWnds Window HandleのList
     * @param level ログレベル
     */
    protected void printWindowTextByHandle(String header, List <NativeLong> hWnds, LogLevel level) {
        for (NativeLong hWnd : hWnds) {
            printWindowTextByHandle(header, hWnd, level);
        }
    }

    protected void printWindowTextByHandle(String header, NativeLong hWnd, LogLevel level) {
        String wText = getWindowTextByHandle(hWnd);
        Util.infoPrintln(level, header + ": [" + wText + "]");
    }

    /**
     * Window handle を指定して Window Text を取得します．
     * @param hWnd Window Handle
     * @return Window Text
     */
    protected String getWindowTextByHandle(NativeLong hWnd) {
        byte[] windowText = new byte[1024];
        user32.GetWindowTextA(hWnd, windowText, 1024);
        return Native.toString(windowText, defaultEncoding);
    }

    /**
     * Dialog の Button を押します．
     * @param hParent dialogのwindo handle
     * @param hButton buttonのwindow handle
     * @param threadId dialog の threadId
     * @return 成功すれば true をかえします．
     */
    protected boolean pushButtonByHandle(NativeLong hParent, NativeLong hButton, Integer threadId, long waitfor) throws LastErrorException{
        boolean ret = false;

        int idButton = user32.GetDlgCtrlID(hButton);
        NativeLong wParam, lParam, hTo;
        int command;
        Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowsBrowser#pushButtonByHandle: idButton=" + idButton);
        /*
         * 上書き確認ダイアログで idButton の値がゼロになる．このときボタンは押せるが動作がおかしい．
         * else 側の処理なら常にうまくいくが，本来は true 側の処理が正しいと思うので．．．
         */
        if (idButton >= 1) {
            wParam = new NativeLong((long)WinConst.MAKEWPARAM(idButton, WinConst.BN_CLICKED));
            lParam = new NativeLong(hButton.longValue());
            command = WinConst.WM_COMMAND;
            hTo = hParent;
        } else {
            wParam = new NativeLong(0);
            lParam = new NativeLong(0);
            command = WinConst.BM_CLICK;
            hTo = hButton;
        }

        /* dialog に focus をあてる */
        NativeLong hBak = null;
        int idAttachTo = 0;
        int idAttach = 0;
        if (threadId != null) {
            idAttachTo = threadId.intValue();
            idAttach = kernel32.GetCurrentThreadId();
            user32.AttachThreadInput(idAttach, idAttachTo, true);
            hBak = user32.SetFocus(hParent);
            user32.SetActiveWindow(hButton);

            /*
             * ちょっとまったほうがいいかもしれない
             */
            try {
                Thread.sleep(waitfor);
            } catch (InterruptedException e) {
                Util.infoPrintln(LogLevel.LOG_WARNING, e);
            }

        } else {
            Util.infoPrintln(LogLevel.LOG_ERR, "*** No thread found.");
        }

        /* SendMessage だと一生戻ってこない場合がある */
        if (user32.PostMessageA(hTo, command, wParam, lParam)) {
            Util.infoPrintln(LogLevel.LOG_DEBUG, "PostMessage success.");
            ret = true;
        } else {
            Util.infoPrintln(LogLevel.LOG_DEBUG, "PostMessage failed.");
        }
//        user32.SendMessageA(hParent, WinConst.WM_COMMAND, wParam, lParam);

        ret = true;

        /* focusを戻しておく... SetFocusは要らない気がする */
        if (hBak != null) {
//            user32.SetFocus(hBak);
            user32.AttachThreadInput(idAttach, idAttachTo, false);
        }

        return ret;
    }

    protected boolean processDialogWindow(String dialogText, int dialogSearchMode, String buttonText, int buttonSearchMode) throws ClassCastException, IOException, ClassNotFoundException, LastErrorException {
        boolean ret = false;

        StructBrowserInfo dialogInfo = null;
        List<NativeLong> hWnds = null;
        NativeLong hDialog = null;
        StructBrowserInfo buttonInfo = null;
        List<NativeLong> hButtonWnds = null;

        /* Dialog の表示に時間がかかるかも知れないのでリトライする */
        boolean isRetry = true;
        long timeout = 0;
        try {
            timeout = Long.parseLong(Config.getProperty(
                    Config.waitForDialogToLoadKey, Config.waitForDialogToLoadDefault));
        } catch (NumberFormatException e) {
            Util.infoPrintln(LogLevel.LOG_ERR, e);
            timeout = 1000;
        }
        while (isRetry) {
            dialogInfo = searchWindows(null, dialogText, dialogSearchMode);
            timeout -= 1000;

            Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowBrowser#processDialogWindow " + dialogInfo.getNumHandles() + " window(s) found");

            if (dialogInfo.getNumHandles() > 0) {
                isRetry = false;
            } else {
                if (timeout <= 0) {
                    Util.infoPrintln(LogLevel.LOG_ERR, "WindowBrowser#processDialogWindow: searching dialog is timed out.");
                    isRetry = false;
                } else {

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Util.infoPrintln(LogLevel.LOG_WARNING, e);
                    }
                }
            }
        }


        hWnds = dialogInfo.getWindowHandles();
        /* 得られた window handle の Window Text を表示してみる */
        printWindowTextByHandle("Window Text", hWnds, LogLevel.LOG_DEBUG);

        hDialog = null;
        if (hWnds.size() == 0) {
            Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowBrowser#processDialogWindw: !!!No download confirmation dialog!!!");
        } else if (hWnds.size() == 1) {
            Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowBrowser#processDialogWindw: found download confirmation dialog.");
            hDialog = hWnds.get(0);
        } else {
            Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowBrowser#processDialogWindw: !!!Too many download confirmation dialog : " + hWnds.size());
            if (Util.isDebugLevel(LogLevel.LOG_DEBUG)) {
                for (NativeLong hWnd : hWnds) {
                    buttonInfo = searchWindows(hWnd, null, buttonSearchMode);
                    hButtonWnds = buttonInfo.getWindowHandles();
                    /* 得られた window handle の Window Text を表示してみる */
                    printWindowTextByHandle("Button Text", hButtonWnds, LogLevel.LOG_DEBUG);
                }
            }
        }
        if (hDialog != null) {
            /* ボタンを探す */
            buttonInfo = searchWindows(hDialog, buttonText, buttonSearchMode);

            Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowBrowser#processDialogWindw " + buttonInfo.getNumHandles() + " button(s) found");

            hButtonWnds = buttonInfo.getWindowHandles();
            /* 得られた window handle の Window Text を表示してみる */
            printWindowTextByHandle("Button Text", hButtonWnds, LogLevel.LOG_DEBUG);

            if (hButtonWnds.size() == 1) {
                long waitfor = 0;
                try {
                    waitfor = Long.parseLong(Config.getProperty(
                            Config.waitForButtonToGetFocusKey,
                            Config.waitForButtonToGetFocusDefault));
                } catch (NumberFormatException e) {
                    Util.infoPrintln(LogLevel.LOG_ERR, e);
                    waitfor = 1000;
                }

                if (!pushButtonByHandle(hDialog, hButtonWnds.get(0), dialogInfo.getWindowThread(hDialog), waitfor)) {
                    Util.infoPrintln(LogLevel.LOG_ERR, "WindowBrowser#processDialogWindow: Cannot push Download Confirm dialog's button.: " + browserFileDownloadConfirmButton);
                } else {
                    ret = true;
                }
            } else {
                Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowBrowser#processDialogWindw Cannot get Download Confirm dialog's button.: " + browserFileDownloadConfirmButton);
            }
        }

        return ret;
    }

    /**
     * ダウンロード確認ダイアログが表示されるかどうかを確認する
     * @return 表示される場合は true を返す
     */
    protected boolean isBrowserFileDownloadConfirm() {
        return "true".equalsIgnoreCase(browserFileDownloadConfirm);
    }

    /**
     * ダウンロードダイアログが表示されるかどうかを確認する
     * @return 表示される場合は true を返す
     */
    protected boolean isBrowserFileDownloadDialog() {
        return "true".equalsIgnoreCase(browserFileDownloadDialog);
    }

    /**
     * ファイルの上書き確認ダイアログが表示されるかどうかを確認する
     * @return 表示される場合は true を返す
     */
    protected boolean isBrowserFileDownloadOverrideConfirm() {
        return "true".equalsIgnoreCase(browserFileDownloadOverrideConfirm);
    }

    /**
     * ダウンロード完了確認ダイアログが表示されるかどうかを確認する
     * @return 表示される場合は true を返す
     */
    protected boolean isBrowserFileDownloadCompleteConfirm() {
        return "true".equalsIgnoreCase(browserFileDownloadCompleteConfirm);
    }

    /**
     * 実行イメージファイル名をもとにTopレベルwindowからbrowserのwindowを探すcallback
     * @author tueda
     *
     */
    class FindBrowser implements User32.WNDENUMPROC {

        Pointer savedPointer = null;

        @Override
        synchronized public boolean callback(NativeLong hWnd, PointerByReference arg) {
            //            Util.infoPrintln(LogLevel.LOG_DEBUG, "FindBrowser#callback: CALLED!");

            /*
             * argからStructBrowserInfoを復元する．先頭4バイトにメモリサイズが入っていることに注意．
             */
            if (arg == null) {
                Util.infoPrintln(LogLevel.LOG_ERR, "FindBrowser#callback: no data specified.");
                return false;
            }
            Pointer ptr = arg.getValue();
//            Util.infoPrintln(LogLevel.LOG_DEBUG, "callback: pointer=" + ptr);
            StructBrowserInfo bInfo;
            try {
                int pSize = WindowsBrowser.getHeadingIntFromPointer(ptr);
                //                Util.infoPrintln(LogLevel.LOG_DEBUG, "pSize=" + pSize);
                byte [] data = ptr.getByteArray(0, pSize);
                bInfo = StructBrowserInfo.deserialize(data);
            } catch (IOException e1) {
                Util.infoPrintln(LogLevel.LOG_CRIT, e1);
                return false;
            } catch (ClassCastException e2) {
                Util.infoPrintln(LogLevel.LOG_CRIT, e2);
                return false;
            } catch (ClassNotFoundException e3) {
                Util.infoPrintln(LogLevel.LOG_CRIT, e3);
                return false;
            }

            /*
             * まずWindow Titleで絞り込む
             */
            String searchWindowText = bInfo.getWindowTitle();
            int searchMode = bInfo.getSearchMode();
            if (searchWindowText != null) {
                String wText = getWindowTextByHandle(hWnd);
                if (!Util.isStringMatched(wText, searchWindowText, searchMode)) {
                    return true;
                } else {
                    Util.infoPrintln(LogLevel.LOG_DEBUG, "%%% match : " + wText);
                }
            }

            /* そのWindowを所有するプロセスの実行イメージを取得する */
            IntByReference ptrProcId = new IntByReference();

            int threadId = user32.GetWindowThreadProcessId(hWnd, ptrProcId);

            Pointer hProc = null;
            try {
                /*
                 * PROCESS_QUERY_INFORMATION (0x0400)
                 * PROCESS_QUERY_LIMITED_INFORMATION (0x1000)
                 * PROCESS_VM_READ
                 * (0x0010)
                 */
                hProc = kernel32.OpenProcess(
                        WinConst.PROCESS_QUERY_INFORMATION
                        | WinConst.PROCESS_QUERY_LIMITED_INFORMATION
                        | WinConst.PROCESS_VM_READ
                        | WinConst.PROCESS_VM_WRITE,
                        false,
                        ptrProcId.getValue());
            } catch (LastErrorException e) {
                //                Util.infoPrintln(LogLevel.LOG_ERR, e);

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

                String msgBuf = Native.toString(lpMsgBuf, defaultEncoding);

                Util.infoPrintln(LogLevel.LOG_ERR,
                        "WindowsBrowser.FindBrowser#callback: system err for procid: "
                                + ptrProcId.getValue() + ": code : " + e.getErrorCode() + " : " + msgBuf);

                return true;
            }

            if (hProc == null) {
                Util.infoPrintln(LogLevel.LOG_ERR,
                        "WindowsBrowser.FindBrowser#callback: fail to get Process Handler for ptrProcId: " + ptrProcId.getValue());
                return true;
            }

            Pointer[] hMods = new Pointer[2048];
            IntByReference cbNeeded = new IntByReference();

            try {
            		psapi.EnumProcessModules(hProc, hMods, hMods.length, cbNeeded);

                //                Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowBrowser#callback: EnumProcModules:  " + (cbNeeded.getValue() / Pointer.SIZE));

                for (int i = 0; i < (cbNeeded.getValue() / Pointer.SIZE); i++) {
                    byte[] modulePath = new byte[WinConst.MAX_PATH];
                    int pathLen = psapi.GetModuleFileNameExA(hProc, hMods[i],
                            modulePath, WinConst.MAX_PATH);
                    if (pathLen > 0) {
                        String pText = Native.toString(modulePath, defaultEncoding);

                        /* browserの実行イメージによって起動されたプロセスのWindow Handle一覧を作成する */
                        if (browserExecutablePath.equalsIgnoreCase(pText)) {
                            Util.infoPrintln(LogLevel.LOG_DEBUG, "*** found module: " + i + " : " + pText);

                            bInfo.addWindowHandle(hWnd);
                            Util.infoPrintln(LogLevel.LOG_DEBUG, "### threadId:" + threadId);
                            if (bInfo.getWindowThreads() == null) {
                                Util.infoPrintln(LogLevel.LOG_DEBUG, "$$$ hoge");
                            }
                            bInfo.putWindowThread(hWnd, threadId);
                            byte [] data;
                            try {
                                data = bInfo.serialize();
                            } catch (IOException e) {
                                Util.infoPrintln(LogLevel.LOG_CRIT, e);
                                return false;
                            }
                            /*
                             * setValue(pointer)とすると，jnaはpointer値をNativeメソッドにそのまま渡している．<br>
                             * つまりJava的には，callbackメソッドから抜けた時点でこのPointerオブジェクトは
                             * どこでも保持されていないことになる．このため，GC時に領域がこわされてしまう場合が
                             * あるようなので，FindBrowserクラスのフィールド変数に保持することにする．
                             * この方式では，同じFindBrowserクラスのインスタンスを使って別スレッドからEnumWindowsを
                             * 呼び出した際に破たんすることに注意．2011/08/24 by tueda
                             *
                             */
                            savedPointer = new Memory(data.length);
                            savedPointer.write(0, data, 0, data.length);
                            arg.setValue(savedPointer);
                            Util.infoPrintln(LogLevel.LOG_DEBUG, "new pointer=" + savedPointer);
                            /* just for debuggin
                            try {
                                Util.infoPrintln(LogLevel.LOG_DEBUG, "size=" + WindowsBrowser.getHeadingIntFromPointer(savedPointer));
                            } catch (IOException e) {
                                // TODO 自動生成された catch ブロック
                                e.printStackTrace();
                            }
                             */
                        } else {
                           // Util.infoPrintln(LogLevel.LOG_DEBUG, "%%% not found : " + pText);
                        }
                    }

                }
                //                Util.infoPrintln(LogLevel.LOG_DEBUG, "WindowBrowser#callback: EnumProcModules: done");
            } catch(LastErrorException e) {
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

                 String msgBuf = Native.toString(lpMsgBuf, defaultEncoding);

                 Util.infoPrintln(LogLevel.LOG_CRIT,
                         "WindowsBrowser#resolveAlertDialog: fail to get process modules.: "
                                 + ptrProcId.getValue() + ": code : " + e.getErrorCode() + " : " + msgBuf);
            }

            kernel32.CloseHandle(hProc);

            //            Util.infoPrintln(LogLevel.LOG_DEBUG, "FindBrowser#callback: RETURN");
            return true;
        }

    }

    /**
     * Window Text を列挙するcallbackクラス（debu用)
     * @author tueda
     *
     */
    class EnumWindowTextProc implements User32.WNDENUMPROC {

        @Override
        public boolean callback(NativeLong hWnd, PointerByReference arg) {
            String wText = getWindowTextByHandle(hWnd);
            Util.infoPrintln(LogLevel.LOG_DEBUG, "\tChild: " + wText);
            return true;
        }

    }

    /**
     * Browserに関連するWindowを検索する際に必要な情報と検索結果を格納するクラス
     * @author tueda
     *
     */
    public static class StructBrowserInfo implements Serializable {
        /**
         * generated on 2011/08/24 by tueda
         */
        private static final long serialVersionUID = -7956543713666518855L;

        /** 検索するwindow text */
        String windowTitle = null; /* Window title to be searched */
        /** window text検索モード．0: 完全一致, 1: 前方一致, 2: 後方一致 */
        int searchMode = 0; /* search mode for window text, complete:0, prefix:1, suffix:2 */
        /** 発見したwindow handleのList */
        List<NativeLong> windowHandles = null; /* Window Handles */
        /** 発見したwindow handleの合計数 */
        int numHandles = 0; /* number of window handles */
        /** 発見したwindow handleにひも付くthreadId */
        Map<NativeLong, Integer> windowThreads = null;

        /**
         * このオブジェクトをbyte配列にserializeします．<br>
         * <em>先頭4バイトにはbyte配列のサイズが入っていることに注意！</em>
         * @return
         * @throws IOException
         */
        public byte [] serialize() throws IOException {
            // オブジェクトを符号化し、バイト配列に書き込み
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);

            byte [] body = baos.toByteArray();

            /*
             * Pointer型からbyte配列に戻すために，先頭の4バイトにサイズを埋めておく
             */
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos2);
            dos.writeInt(body.length + WinConst.BROWSER_INFO_HEADING_LEN);

            byte [] headings = baos2.toByteArray();

            byte [] data = new byte[headings.length + body.length];

            System.arraycopy(headings, 0, data, 0, headings.length);
            System.arraycopy(body, 0, data, headings.length, body.length);

            return data;
        }

        /**
         * byte配列からこのオブジェクトにdesirializeします．<br>
         * <em>先頭4バイトにはbyte配列のサイズが入っていることに注意！</em>
         * @return
         * @throws IOException
         * @throws ClassNotFoundException
         */
        public static StructBrowserInfo deserialize(byte [] buff) throws IOException, ClassNotFoundException, ClassCastException {

            /* 先頭の4バイトを捨てる */
            byte [] body;
            if (buff != null && buff.length > WinConst.BROWSER_INFO_HEADING_LEN) {
                body = new byte[buff.length - WinConst.BROWSER_INFO_HEADING_LEN];
            } else {
                Util.infoPrintln(LogLevel.LOG_ERR, "StructBrowser#deserialize: buffer is too short.");
                throw new ClassNotFoundException("buffer is too short.");
            }

            System.arraycopy(buff, WinConst.BROWSER_INFO_HEADING_LEN, body, 0, body.length);

            // バイト配列から、オブジェクトを複合化
            ByteArrayInputStream bais = new ByteArrayInputStream(body);
            ObjectInputStream os = new ObjectInputStream(bais);

            return (StructBrowserInfo)os.readObject();
        }

        /**
         * @return windowTitle
         */
        public String getWindowTitle() {
            return windowTitle;
        }

        /**
         * @param windowTitle セットする windowTitle
         */
        public void setWindowTitle(String windowTitle) {
            this.windowTitle = windowTitle;
        }

        public boolean isEmptyWindowHandles() {
            return windowHandles.isEmpty();
        }

        public boolean containsWindowHandles(Object o) {
            return windowHandles.contains(o);
        }

        public boolean addWindowHandle(NativeLong e) {
            numHandles++;
            return windowHandles.add(e);
        }

        public boolean removeWindowHandle(NativeLong o) {
            numHandles--;
            return windowHandles.remove(o);
        }

        public NativeLong getWindowHandle(int index) {
            return windowHandles.get(index);
        }

        public NativeLong setWindowHandle(int index, NativeLong element) {
            return windowHandles.set(index, element);
        }

        public void addWindowHandle(int index, NativeLong element) {
            windowHandles.add(index, element);
            numHandles++;
        }

        public NativeLong removeWindowHandle(int index) {
            numHandles--;
            return windowHandles.remove(index);
        }

        public int indexOf(NativeLong o) {
            return windowHandles.indexOf(o);
        }

        public int lastIndexOf(NativeLong o) {
            return windowHandles.lastIndexOf(o);
        }

        public ListIterator<NativeLong> listIterator() {
            return windowHandles.listIterator();
        }

        public ListIterator<NativeLong> listIterator(int index) {
            return windowHandles.listIterator(index);
        }

        public List<NativeLong> subList(int fromIndex, int toIndex) {
            return windowHandles.subList(fromIndex, toIndex);
        }

        public boolean add(NativeLong e) {
            numHandles++;
            return windowHandles.add(e);
        }

        public void setWindowHandles(List<NativeLong> windowHandles) {
            this.windowHandles = windowHandles;
            numHandles = this.windowHandles.size();
        }

        public List<NativeLong> getWindowHandles() {
            return windowHandles;
        }


        /**
         * @return numHandles
         */
        public int getNumHandles() {
            return numHandles;
        }

        /**
         * @param numHandles セットする numHandles
         */
        public void setNumHandles(int numHandles) {
            this.numHandles = numHandles;
        }

        /**
         * @return windowThreads
         */
        public Map<NativeLong, Integer> getWindowThreads() {
            return windowThreads;
        }

        /**
         * @param windowThreads セットする windowThreads
         */
        public void setWindowThreads(Map<NativeLong, Integer> windowThreads) {
            this.windowThreads = windowThreads;
        }

        /**
         * window handleをキーにしてthreadIdをputする
         * @param hWnd 検索で発見されたwindow handle
         * @param threadId hWndにひも付くthreadId
         * @return 直前に格納されていたthreadId，なかった場合はnullが返る．
         */
        public Integer putWindowThread(NativeLong hWnd, int threadId) {
            return windowThreads.put(hWnd, new Integer(threadId));
        }

        /**
         * window handle をキーにしてthreadIdを取得する
         * @param hWnd 検索で発見されたwindow handle
         * @return hWndにひも付くthreadId
         */
        public Integer getWindowThread(NativeLong hWnd) {
            return windowThreads.get(hWnd);
        }

        /**
         * @return searchMode
         */
        public int getSearchMode() {
            return searchMode;
        }

        /**
         * @param searchMode セットする searchMode
         */
        public void setSearchMode(int searchMode) {
            this.searchMode = searchMode;
        }

    }

}
