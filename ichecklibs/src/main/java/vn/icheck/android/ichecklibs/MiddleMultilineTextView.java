package vn.icheck.android.ichecklibs;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class MiddleMultilineTextView extends androidx.appcompat.widget.AppCompatTextView {

    private String SYMBOL = " ... ";
    private final int SYMBOL_LENGTH = SYMBOL.length();

    public MiddleMultilineTextView(Context context) {
        super(context);
    }
    public MiddleMultilineTextView(Context context, AttributeSet attrs) { super(context, attrs); }
    public MiddleMultilineTextView(Context context, AttributeSet attrs,int defStyleAttr) { super(context, attrs,defStyleAttr); }

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

        int subTo = midpoint - lstrip - 4;
        if (subTo < 0){
            subTo = 0;
        }

        String result = string.substring(0, subTo) + SYMBOL + string.substring(midpoint + rstrip);

        return result;
    }

    private int getVisibleLength() {
        int start = getLayout().getLineStart(0);
        int end = getLayout().getLineEnd(getMaxLines() - 1);
        return getText().toString().substring(start, end).length();
    }
}
