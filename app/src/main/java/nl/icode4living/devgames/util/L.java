package nl.icode4living.devgames.util;

import android.util.Log;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TODO: write class level documentation.
 *
 * @author Marcel
 * @since 22-6-2016.
 */
@SuppressWarnings("PointlessBooleanExpression")
public class L {

    private static final boolean DEBUG = true;

    // The format of the automatically created tag in each log line.
    private static final String TAG_FORMAT = "DEVGAMES: line=%d: %s#%s: ";

    private static final Pattern ANONYMOUS_CLASS_PATTERN = Pattern.compile("\\$\\d+$");

    // Empty Instantiation of a args array in case of null
    private static final String[] NO_ARGS = {};

    public static int v(String message, Object... args) {
        if (!DEBUG) return -1;
        MessageFormat form = new MessageFormat(message);
        return Log.v(createTag(), form.format(args == null ? NO_ARGS : args));
    }

    public static int v(Throwable throwable, String message, Object... args) {
        if (!DEBUG) return -1;
        MessageFormat form = new MessageFormat(message);
        return Log.v(createTag(), form.format(args == null ? NO_ARGS : args), throwable);
    }

    public static int d(String message, Object... args) {
        if (!DEBUG) return -1;
        MessageFormat form = new MessageFormat(message);
        return Log.d(createTag(), form.format(args == null ? NO_ARGS : args));
    }

    public static int d(Throwable throwable, String message, Object... args) {
        if (!DEBUG) return -1;
        MessageFormat form = new MessageFormat(message);
        return Log.d(createTag(), form.format(args == null ? NO_ARGS : args), throwable);
    }

    public static int i(String message, Object... args) {
        if (!DEBUG) return -1;
        MessageFormat form = new MessageFormat(message);
        return Log.i(createTag(), form.format(args == null ? NO_ARGS : args));
    }

    public static int i(Throwable throwable, String message, Object... args) {
        if (!DEBUG) return -1;
        MessageFormat form = new MessageFormat(message);
        return Log.i(createTag(), form.format(args == null ? NO_ARGS : args), throwable);
    }

    public static int w(String message, Object... args) {
        if (!DEBUG) return -1;
        MessageFormat form = new MessageFormat(message);
        return Log.w(createTag(), form.format(args == null ? NO_ARGS : args));
    }

    public static int w(Throwable throwable, String message, Object... args) {
        if (!DEBUG) return -1;
        MessageFormat form = new MessageFormat(message);
        return Log.w(createTag(), form.format(args == null ? NO_ARGS : args), throwable);
    }

    public static int e(String message, Object... args) {
        if (!DEBUG) return -1;
        MessageFormat form = new MessageFormat(message);
        return Log.e(createTag(), form.format(args == null ? NO_ARGS : args));
    }

    public static int e(Throwable throwable, String message, Object... args) {
        if (!DEBUG) return -1;
        MessageFormat form = new MessageFormat(message);
        return Log.e(createTag(), form.format(args == null ? NO_ARGS : args), throwable);
    }

    public static int wtf(String message, Object... args) {
        if (!DEBUG) return -1;
        MessageFormat form = new MessageFormat(message);
        return Log.wtf(createTag(), form.format(args == null ? NO_ARGS : args));
    }

    public static int wtf(Throwable throwable, String message, Object... args) {
        if (!DEBUG) return -1;
        MessageFormat form = new MessageFormat(message);
        return Log.wtf(createTag(), form.format(args == null ? NO_ARGS : args), throwable);
    }

    /**
     * Creates a tag from the trace of the class from which
     * the Log-call was called.
     * <p/>
     * Tag name retrieval "borrowed" from:
     * https://github.com/JakeWharton/timber/blob/master/timber/src/main/java/timber/log/Timber.java
     *
     * @return a tag from the trace of the class from which
     * the Log-call was called.
     */
    private static String createTag() {

        try {
            StackTraceElement[] traces = Thread.currentThread().getStackTrace();
            StackTraceElement trace = traces[4];
            String tag = trace.getClassName();
            Matcher m = ANONYMOUS_CLASS_PATTERN.matcher(tag);

            if (m != null && m.find()) {
                tag = m.replaceAll("");
            }

            String className = tag.substring(tag.lastIndexOf('.') + 1);

            return String.format(TAG_FORMAT,
                    trace.getLineNumber(),
                    className,
                    trace.getMethodName()
            );
        }
        catch (Exception e) {
            // Should not happen.
            return "UNKNOWN-TAG";
        }
    }
}