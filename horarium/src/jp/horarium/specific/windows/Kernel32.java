package jp.horarium.specific.windows;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;

public interface Kernel32 extends StdCallLibrary {
    Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("kernel32", Kernel32.class);

    /* おそらくhandlerはPointerで扱ったほうがいいのかも */
    Pointer OpenProcess(
            int dwDesiredAccess,
            boolean bInheritHandle,
            int dwProcessId
          ) throws LastErrorException;

    boolean CloseHandle(
            Pointer hObject
          );

    int FormatMessageA(
            int dwFlags,
            Pointer lpSource,
            int dwMessageId,
            int dwLanguageId,
            byte [] lpBuffer,
            int nSize,
            Pointer Arguments
          );


    Pointer LocalFree(
            Pointer hMem   // ローカルメモリオブジェクトのハンドル
    );

    int GetCurrentThreadId() throws LastErrorException;
}
