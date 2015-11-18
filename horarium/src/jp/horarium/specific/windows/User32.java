package jp.horarium.specific.windows;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface User32 extends StdCallLibrary {
    User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

    interface WNDENUMPROC extends StdCallCallback  {
        boolean callback(NativeLong hWnd, PointerByReference arg);
    }

    boolean EnumWindows(WNDENUMPROC lpEnumFunc, PointerByReference arg) throws LastErrorException;
    boolean EnumChildWindows(NativeLong hWnd, WNDENUMPROC lpEnumFunc, PointerByReference arg) throws LastErrorException;

    int GetWindowTextA(NativeLong hWnd, byte[] lpString, int nMaxCount);
    int GetWindowTextW(NativeLong hWnd, byte[] lpString, int nMaxCount);
//    int GetWindowText(Pointer hWnd, byte[] lpString, int nMaxCount);

    int GetWindowThreadProcessId(
            NativeLong hWnd,             // ウィンドウのハンドル
            IntByReference lpdwProcessId  // プロセス ID
          );

    NativeLong SendMessageA(NativeLong hWnd, int msg, NativeLong wParam, NativeLong lParam) throws LastErrorException;
    boolean PostMessageA(NativeLong hWnd, int msg, NativeLong wParam, NativeLong lParam) throws LastErrorException;
    NativeLong SetFocus(NativeLong hWnd) throws LastErrorException;
    NativeLong SetActiveWindow(NativeLong hWnd) throws LastErrorException;

    boolean AttachThreadInput(int idAttach, int idAttachTo, boolean fAttach) throws LastErrorException;
    int GetDlgCtrlID(NativeLong hWnd) throws LastErrorException;
    NativeLong FindWindow(byte [] lpWindowClass, byte [] lpWindowText) throws LastErrorException;
    NativeLong FindWindowEx(NativeLong hParent, NativeLong hChild, byte [] lpWindowClass, byte [] lpWindowText) throws LastErrorException;
}
