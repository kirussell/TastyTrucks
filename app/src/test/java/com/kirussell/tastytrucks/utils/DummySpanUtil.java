package com.kirussell.tastytrucks.utils;

/**
 * Created by russellkim on 10/04/16.
 * Ignores tags for style spans. To prevent null text in default-unit-tests implementation
 * of SpannableStringBuilder
 */
public class DummySpanUtil extends SpanUtil {

    @Override
    protected CharSequence apply(CharSequence[] content, Object... tags) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence token: content) {
            sb.append(token);
        }
        return sb.toString();
    }
}
