/**
 *
 */
package jp.horarium.specific.windows;

/**
 * @author tueda
 *
 */
public class WinConst {

    /* for OpenProess */
    public final static int PROCESS_QUERY_INFORMATION = 0x0400;
    public final static int PROCESS_QUERY_LIMITED_INFORMATION = 0x1000;
    public final static int PROCESS_VM_READ = 0x0010;
    public final static int PROCESS_VM_WRITE = 0x0010;

    /* for FormatMessage */
    public final static int FORMAT_MESSAGE_ALLOCATE_BUFFER = 0x00000100;
    public final static int FORMAT_MESSAGE_ARGUMENT_ARRAY = 0x00002000;
    public final static int FORMAT_MESSAGE_FROM_HMODULE = 0x00000800;
    public final static int FORMAT_MESSAGE_FROM_STRING = 0x00000400;
    public final static int FORMAT_MESSAGE_FROM_SYSTEM = 0x00001000;
    public final static int FORMAT_MESSAGE_IGNORE_INSERTS = 0x00000200;

    /* Window Message */
    public final static int WM_CHAR = 0x0102;
    public final static int WM_COMMAND = 0x0111;
    public final static int WM_LBUTTONDOWN = 0x0201;
    public final static int WM_LBUTTONUP = 0x0202;

    /* Button Message */
    public final static int BM_GETCHECK = 0x00F0;
    public final static int BM_SETCHECK = 0x00F1;
    public final static int BM_GETSTATE = 0x00F2;
    public final static int BM_SETSTATE = 0x00F3;
    public final static int BM_SETSTYLE = 0x00F4;
    /* WINVER >= 0x0400 */
    public final static int BM_CLICK = 0x00F5;
    public final static int BM_GETIMAGE = 0x00F6;
    public final static int BM_SETIMAGE = 0x00F7;

    public final static int BST_UNCHECKED = 0x0000;
    public final static int BST_CHECKED = 0x0001;
    public final static int BST_INDETERMINATE = 0x0002;
    public final static int BST_PUSHED = 0x0004;
    public final static int BST_FOCUS = 0x0008;

    /* Button Notification */
    public final static int BN_CLICKED = 0;

    /* Button Id */
    public final static int IDOK = 1;
    public final static int IDCANCEL = 2;
    public final static int IDABORT = 3;
    public final static int IDRETRY = 4;
    public final static int IDIGNORE = 5;
    public final static int IDYES = 6;
    public final static int IDNO = 7;
    /* just for WINVER &gt;= 0x0400 */
    public final static int IDCLOSE = 8;
    public final static int IDHELP = 9;

    /**
     * macro: SendMessage,PostMessageの WPARAM を作成します
     * @param id
     * @param flag
     * @return
     */
    public final static int MAKEWPARAM(int id, int flag) {
       return ((flag << 16) | id);
    }

    /*
     * LangId = (SUBLANG_ID << 10) | LANG_ID
     */
    public final static int LANG_NEUTRAL = 0x00;
    public final static int LANG_ENGLISH = 0x09;
    public final static int SUBLANG_DEFAULT = 0x01;
    public final static int SUBLANG_ENGLISH_US = 0x01;

    /**
     * macro: FormatMessageAに渡すためのLnagIdを作成します．
     * @param langId
     * @param subLangId
     * @return
     */
    public final static int MAKELANGID(int langId, int subLangId) {
        return (subLangId << 10) | langId;
    }

    public final static int MAX_PATH = 260 * 3; /* UTF-8 を考慮して3倍しておく */

    /* JNA Issue */
    public final static int BROWSER_INFO_HEADING_LEN = 4; /* bytes */
}
