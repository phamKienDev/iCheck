package vn.icheck.android.ichecklibs;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

public class MiddleMultilineTextView extends androidx.appcompat.widget.AppCompatTextView {

    private String SYMBOL = " ... ";
    private final int SYMBOL_LENGTH = SYMBOL.length();

    public MiddleMultilineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getMaxLines() > 1) {
            int originalLength = getText().length();
            int visibleLength = getVisibleLength();

            if (originalLength > visibleLength) {
                setText(smartTrim(getText().toString(), visibleLength - SYMBOL_LENGTH));
            }
        }
    }


    private String smartTrim(String string, int maxLength) {
        if (string == null)
            return null;
        if (maxLength < 1)
            return string;
        if (string.length() <= maxLength)
            return string;
        if (maxLength == 1)
            return string.substring(0, 1) + "...";

        int midpoint = (int) Math.ceil(string.length() / 2);
        int toremove = string.length() - maxLength;
        int lstrip = (int) Math.ceil(toremove / 2);
        int rstrip = toremove - lstrip;

        String result = string.substring(0, midpoint - lstrip - 4) + SYMBOL + string.substring(midpoint + rstrip);

        return result;
    }

    private int getVisibleLength() {
        int start = getLayout().getLineStart(0);
        int end = getLayout().getLineEnd(getMaxLines() - 1);
        return getText().toString().substring(start, end).length();
    }
}
