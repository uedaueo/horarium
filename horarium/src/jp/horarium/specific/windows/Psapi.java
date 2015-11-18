package jp.horarium.specific.windows;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface Psapi extends StdCallLibrary {
    Psapi INSTANCE = (Psapi) Native.loadLibrary("psapi", Psapi.class);

    boolean EnumProcessModules(
            Pointer hProcess,
            Pointer [] lphModule,
            int cb,
            IntByReference lpcbNeeded
          ) throws LastErrorException;

    int GetModuleFileNameExA(
            Pointer hProcess,
            Pointer hModule,
            byte [] lpFilename,
            int nSize
          );

}
