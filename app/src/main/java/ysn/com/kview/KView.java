package ysn.com.kview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * @Author yangsanning
 * @ClassName KView
 * @Description 一句话概括作用
 * @Date 2018/6/13
 * @History 2018/6/13 author: description:
 */
public class KView extends View {

    public KView(Context context) {
        super(context);
    }

    public KView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    private float textSpace=20.0f;//编写旁边数字的距离

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int viewHeight = getHeight();
        int viewWidth = getWidth();

        // 绘制边框
        drawBorders(canvas, viewHeight, viewWidth);
    }


    /**
     * 绘制边框
     *
     * @param canvas
     */
    private void drawBorders(Canvas canvas, int viewHeight, int viewWidth) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        paint.setStrokeWidth(2);
        canvas.drawLine(textSpace, 1, viewWidth - textSpace, 1, paint);
        canvas.drawLine(textSpace, 1, textSpace, viewHeight - 1, paint);
        canvas.drawLine(viewWidth - textSpace, viewHeight - 1, viewWidth - textSpace, 1, paint);
        canvas.drawLine(viewWidth - textSpace, viewHeight - 1, textSpace, viewHeight - 1, paint);
    }
}
