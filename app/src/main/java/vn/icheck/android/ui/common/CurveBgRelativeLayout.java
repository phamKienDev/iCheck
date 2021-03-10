package vn.icheck.android.ui.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class CurveBgRelativeLayout extends RelativeLayout {
    private Path path;
    private Paint paint;

    public CurveBgRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(0xffff0000);
        paint.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.BLUE, Color.GRAY, Shader.TileMode.MIRROR));
        path = new Path();

        float horizontalOffset = w * .8f;
        float top = -h * .8f;
        float bottom = h;

        RectF ovalRect = new RectF(-horizontalOffset, top, w + horizontalOffset, bottom);
        path.lineTo(ovalRect.left, top);
        path.arcTo(ovalRect, 0, 180, false);
        path.setFillType(Path.FillType.INVERSE_EVEN_ODD);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (path != null)
            canvas.drawPath(path, paint);
        super.onDraw(canvas);
    }
}