package com.mystery.update.process;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * 作者:  tang
 * 时间： 2020/3/27 0027 下午 7:02
 * 邮箱： 3349913147@qq.com
 * 描述：
 */
class Logger {

    private static String TAG = "UpdateLogger";
    private static final int CHUNK_SIZE = 4000;
    private static final int JSON_INDENT = 4;
    private static final int MIN_STACK_OFFSET = 3;
    private static final char TOP_LEFT_CORNER = '╔';
    private static final char BOTTOM_LEFT_CORNER = '╚';
    private static final char MIDDLE_CORNER = '╟';
    private static final char HORIZONTAL_DOUBLE_LINE = '║';
    private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
    private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
    private static final String TOP_BORDER = "╔════════════════════════════════════════════════════════════════════════════════════════";
    private static final String BOTTOM_BORDER = "╚════════════════════════════════════════════════════════════════════════════════════════";
    private static final String MIDDLE_BORDER = "╟────────────────────────────────────────────────────────────────────────────────────────";
    private static final ThreadLocal<String> LOCAL_TAG = new ThreadLocal();
    private static final ThreadLocal<Integer> LOCAL_METHOD_COUNT = new ThreadLocal();
    private static int methodCount = 2;
    private static boolean showThreadInfo = true;
    private static boolean debugLog = false;

    public Logger() {
    }

    public static void init(String tag, boolean debug) {
        debugLog = debug;
        if (tag == null) {
            throw new NullPointerException("tag may not be null");
        } else if (tag.trim().length() == 0) {
            throw new IllegalStateException("tag may not be empty");
        } else {
            TAG = tag;
        }
    }

    public static void t(String tag, int methodCount) {
        if (tag != null) {
            LOCAL_TAG.set(tag);
        }

        LOCAL_METHOD_COUNT.set(methodCount);
    }

    public static void d(String message, Object... args) {
        log(3, message, args);
    }

    public static void e(String message, Object... args) {
        e((Throwable)null, message, args);
    }

    public static void e(Throwable throwable) {
        e("", throwable);
    }

    public static void e(String message, Throwable throwable) {
        e(throwable, message);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        if (throwable != null && message != null) {
            message = message + " : " + throwable.toString();
        }

        if (throwable != null && message == null) {
            message = throwable.toString();
        }

        if (message == null) {
            message = "No message/exception is set";
        }

        log(6, message, args);
    }

    public static void w(String message, Object... args) {
        log(5, message, args);
    }

    public static void i(String message, Object... args) {
        log(4, message, args);
    }

    public static void v(String message, Object... args) {
        log(2, message, args);
    }

    public static void wtf(String message, Object... args) {
        log(7, message, args);
    }

    public static void json(String json) {
        if (TextUtils.isEmpty(json)) {
            d("Empty/Null json content");
        } else {
            try {
                String message;
                if (json.startsWith("{")) {
                    JSONObject jsonObject = new JSONObject(json);
                    message = jsonObject.toString(4);
                    d(message);
                    return;
                }

                if (json.startsWith("[")) {
                    JSONArray jsonArray = new JSONArray(json);
                    message = jsonArray.toString(4);
                    d(message);
                }
            } catch (JSONException var3) {
                e(var3.getCause().getMessage() + "\n" + json);
            }

        }
    }

    public static void xml(String xml) {
        if (TextUtils.isEmpty(xml)) {
            d("Empty/Null xml content");
        } else {
            try {
                Source xmlInput = new StreamSource(new StringReader(xml));
                StreamResult xmlOutput = new StreamResult(new StringWriter());
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty("indent", "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                transformer.transform(xmlInput, xmlOutput);
                d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
            } catch (TransformerException var4) {
                e(var4.getCause().getMessage() + "\n" + xml);
            }

        }
    }

    private static synchronized void log(int logType, String msg, Object... args) {
        if (debugLog) {
            String tag = getTag();
            String message = createMessage(msg, args);
            int methodCount = getMethodCount();
            logTopBorder(logType, tag);
            logHeaderContent(logType, tag, methodCount);
            byte[] bytes = message.getBytes();
            int length = bytes.length;
            if (length <= 4000) {
                if (methodCount > 0) {
                    logDivider(logType, tag);
                }

                logContent(logType, tag, message);
                logBottomBorder(logType, tag);
            } else {
                if (methodCount > 0) {
                    logDivider(logType, tag);
                }

                for(int i = 0; i < length; i += 4000) {
                    int count = Math.min(length - i, 4000);
                    logContent(logType, tag, new String(bytes, i, count));
                }

                logBottomBorder(logType, tag);
            }
        }
    }

    private static void logTopBorder(int logType, String tag) {
        logChunk(logType, tag, "╔════════════════════════════════════════════════════════════════════════════════════════");
    }

    private static void logHeaderContent(int logType, String tag, int methodCount) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if (isShowThreadInfo()) {
            logChunk(logType, tag, "║ Thread: " + Thread.currentThread().getName());
            logDivider(logType, tag);
        }

        String level = "";
        int stackOffset = getStackOffset(trace);
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        for(int i = methodCount; i > 0; --i) {
            int stackIndex = i + stackOffset;
            if (stackIndex < trace.length) {
                StringBuilder builder = new StringBuilder();
                builder.append("║ ").append(level).append(getSimpleClassName(trace[stackIndex].getClassName())).append(".").append(trace[stackIndex].getMethodName()).append(" ").append(" (").append(trace[stackIndex].getFileName()).append(":").append(trace[stackIndex].getLineNumber()).append(")");
                level = level + "   ";
                logChunk(logType, tag, builder.toString());
            }
        }

    }

    private static void logBottomBorder(int logType, String tag) {
        logChunk(logType, tag, "╚════════════════════════════════════════════════════════════════════════════════════════");
    }

    private static void logDivider(int logType, String tag) {
        logChunk(logType, tag, "╟────────────────────────────────────────────────────────────────────────────────────────");
    }

    private static void logContent(int logType, String tag, String chunk) {
        String[] lines = chunk.split(System.getProperty("line.separator"));
        String[] arr$ = lines;
        int len$ = lines.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            String line = arr$[i$];
            logChunk(logType, tag, "║ " + line);
        }

    }

    private static void logChunk(int logType, String tag, String chunk) {
        String finalTag = formatTag(tag);
        switch(logType) {
            case 2:
                Log.v(finalTag, chunk);
                break;
            case 3:
            default:
                Log.d(finalTag, chunk);
                break;
            case 4:
                Log.i(finalTag, chunk);
                break;
            case 5:
                Log.w(finalTag, chunk);
                break;
            case 6:
                Log.e(finalTag, chunk);
                break;
            case 7:
                Log.wtf(finalTag, chunk);
        }

    }

    private static String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private static String formatTag(String tag) {
        return !TextUtils.isEmpty(tag) && !TextUtils.equals(TAG, tag) ? TAG + "-" + tag : TAG;
    }

    private static String getTag() {
        String tag = (String)LOCAL_TAG.get();
        if (tag != null) {
            LOCAL_TAG.remove();
            return tag;
        } else {
            return TAG;
        }
    }

    private static String createMessage(String message, Object... args) {
        return args.length == 0 ? message : String.format(message, args);
    }

    private static int getMethodCount() {
        Integer count = (Integer)LOCAL_METHOD_COUNT.get();
        int result = methodCount;
        if (count != null) {
            LOCAL_METHOD_COUNT.remove();
            result = count;
        }

        if (result < 0) {
            throw new IllegalStateException("methodCount cannot be negative");
        } else {
            return result;
        }
    }

    private static int getStackOffset(StackTraceElement[] trace) {
        for(int i = 3; i < trace.length; ++i) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(Logger.class.getName()) && !name.equals(Logger.class.getName())) {
                --i;
                return i;
            }
        }

        return -1;
    }

    public static void hideThreadInfo() {
        showThreadInfo = false;
    }

    public static boolean isShowThreadInfo() {
        return showThreadInfo;
    }

}
