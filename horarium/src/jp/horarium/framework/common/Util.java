package jp.horarium.framework.common;

import java.util.Calendar;
import java.util.Random;

/**
 * ユーティリティクラス
 *
 * @author tueda
 *
 */
public class Util {

    /**
     * Debug 用 println<br>
     * log level:<br>
     * LOG_EMERG 0 system is unusable<br>
     * LOG_ALERT 1 action must be taken immediately<br>
     * LOG_CRIT 2 critical conditions<br>
     * LOG_ERR 3 error conditions<br>
     * LOG_WARNING 4 /* warning conditions<br>
     * LOG_NOTICE 5 /* normal but significant condition<br>
     * LOG_INFO 6 /* informational<br>
     * LOG_DEBUG 7 /* debug-level messages<br>
     *
     * @param level
     *            log level.
     * @param str
     *            log文字列
     */
    public static void infoPrintln(LogLevel level, String str) {
        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR); // (2)現在の年を取得
        int month = cal.get(Calendar.MONTH) + 1; // (3)現在の月を取得
        int day = cal.get(Calendar.DATE); // (4)現在の日を取得
        int hour = cal.get(Calendar.HOUR_OF_DAY); // (5)現在の時を取得
        int minute = cal.get(Calendar.MINUTE); // (6)現在の分を取得
        int second = cal.get(Calendar.SECOND); // (7)現在の秒を取得

        String logThreshold = Config.getProperty("LogLevel");
        if (logThreshold == null)
            logThreshold = "LOG_DEBUG";

        if (level.compareTo(LogLevel.valueOf(logThreshold)) <= 0) {
            // System.err.println(
            // "[" + year + "/" + month + "/" + day + " " +
            // hour + ":" + minute + ":" + second + "] " +
            // str);
            String strLevel = null;
            switch (level) {
            case LOG_EMERG:
                strLevel = "!!!EMERG!!!";
                break;
            case LOG_ALERT:
                strLevel = "!!ALERT!!";
                break;
            case LOG_CRIT:
                strLevel = "!!CRIT!!";
                break;
            case LOG_ERR:
                strLevel = "!!ERR!!";
                break;
            case LOG_WARNING:
                strLevel = "(WARN)";
                break;
            case LOG_NOTICE:
                strLevel = "(NOTICE)";
                break;
            case LOG_INFO:
                strLevel = "(INFO)";
                break;
            case LOG_DEBUG:
                strLevel = "(DEBUG)";
                break;
            default:
                strLevel = "(???)";
            }

            System.err.printf("[%4d/%02d/%02d %02d:%02d:%02d] %s %s\n", year,
                    month, day, hour, minute, second, strLevel.toString(), str);
        }
    }

    public static void infoPrintln(LogLevel level, Exception e) {
        StringBuffer log = new StringBuffer();

        String msg = e.toString() + "\n";
        log.append(msg);

        StackTraceElement[] trace = e.getStackTrace();
        for (int i = 0; i < trace.length; i++) {
            String stack = trace[i].getClassName() + "."
                    + trace[i].getMethodName() + "(" + trace[i].getFileName()
                    + ":" + trace[i].getLineNumber() + ")\n";
            log.append(stack);
        }
        infoPrintln(level, log.toString());
    }

    /**
     * services.xmlに書かれたdebug levelを判定する．
     *
     * @param level
     *            debug leve
     * @return services.xmlで引数に与えられたレベルより高い値が設定されていればtrue
     */
    public static boolean isDebugLevel(LogLevel level) {
        boolean ret = false;
        String logThreshold = Config.getProperty("LogLevel");
        if (logThreshold == null)
            logThreshold = "LOG_DEBUG";

        if (level.compareTo(LogLevel.valueOf(logThreshold)) <= 0) {
            ret = true;
        }
        return ret;
    }

    private static Random rnd = null;

    public static void initRandom(int seed) {
        rnd = new Random(seed);
    }

    /**
     * 0 - max (exclusive) までの整数乱数値を返す
     *
     * @param max
     * @return 乱数値
     */
    public static int getIntRnd(int max) {
        if (null == rnd)
            initRandom(max);

        return rnd.nextInt(max);
    }

    /**
     * searchModeに従って文字列を比較する
     *
     * @param a
     * @param b
     * @param searchMode
     *            0=完全一致, 1=前方一致, 2=後方一致
     * @return 一致すれば true
     */
    public static boolean isStringMatched(String a, String b, int searchMode) {
        boolean ret = false;

        if (a != null && b != null) {
            if (searchMode == Const.PREFIX_SEARCH) {
                ret = a.startsWith(b);
            } else if (searchMode == Const.SUFFIX_SEARCH) {
                ret = a.endsWith(b);
            } else if (searchMode == Const.PARTIAL_SEARCH) {
                if (a.indexOf(b) != -1)
                    ret = true;
            } else {
                ret = a.equals(b);
            }
        }

        return ret;
    }
}
