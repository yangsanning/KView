package ysn.com.kview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import ysn.com.kview.util.ResUtil;

/**
 * @Author yangsanning
 * @ClassName KView
 * @Description 一句话概括作用
 * @Date 2018/6/13
 * @History 2018/6/13 author: description:
 */
public class KView extends View {

    /**
     * 默认虚线效果
     */
    private static final PathEffect DEFAULT_DASH_EFFECT = new DashPathEffect(new float[]{2, 2, 2,
            2}, 1);

    /**
     * 两边字体大小
     */
    private float textSpace = 20.0f;

    /**
     * 列数
     */
    private int column = 2;

    /**
     * 虚线效果
     */
    private PathEffect mDashEffect = DEFAULT_DASH_EFFECT;


    public KView(Context context) {
        super(context);
    }

    public KView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int viewHeight = getHeight();
        int viewWidth = getWidth();
        float longitudeSpacing = (viewWidth - 2 * textSpace) / column;

        // 绘制边框
        drawBorders(canvas, viewHeight, viewWidth);


        // 绘制经线
        drawLongitudes(canvas, viewHeight, longitudeSpacing);
    }


    /**
     * 绘制边框
     */
    private void drawBorders(Canvas canvas, int viewHeight, int viewWidth) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ResUtil.getColor(R.color.k_view_row));
        paint.setStrokeWidth(1);
        canvas.drawLine(textSpace, 1, viewWidth - textSpace, 1, paint);
        canvas.drawLine(viewWidth - textSpace, viewHeight - 1, textSpace, viewHeight - 1, paint);

    }

    /**
     * 绘制经线
     */
    private void drawLongitudes(Canvas canvas, int viewHeight, float longitudeSpacing) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ResUtil.getColor(R.color.k_view_column));
        paint.setStrokeWidth(1);
        paint.setPathEffect(mDashEffect);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        for (int i = 1; i < column; i++) {
            canvas.drawLine(textSpace + longitudeSpacing * i, 1, textSpace + longitudeSpacing * i,
                    viewHeight - 1, paint);
        }
    }
}
