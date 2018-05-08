package seng302.UI.Watchers;

import java.util.regex.Pattern;

public class NumberWatcher extends Watcher {
    private static final Pattern pattern = Pattern.compile("^\\d+$");

    @Override
    public boolean isValid(Object value) {
        if (value instanceof Integer) {
            return true;
        }
        if (value instanceof Float) {
            return true;
        }
        if (value instanceof CharSequence) {
            CharSequence sequence = (CharSequence) value;
            return pattern.matcher(sequence).matches();
        }
        return false;
    }
}
