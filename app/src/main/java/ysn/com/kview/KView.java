package ysn.com.kview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ysn.com.kview.mode.bean.TimeSharing;
import ysn.com.kview.util.ResUtil;

/**
 * @Author yangsanning
 * @ClassName KView
 * @Description 一句话概括作用
 * @Date 2018/6/13
 * @History 2018/6/13 author: description:
 */
public class KView extends View {

    private static final int POINT_COUNT_DEFAULT = 240;

    /**
     * 默认虚线效果
     */
    private static final PathEffect DEFAULT_DASH_EFFECT = new DashPathEffect(new float[]{2, 2, 2,
            2}, 1);

    /**
     * 两边边距
     */
    private float margin = 20;

    /**
     * 坐标字体大小
     */
    private float xYTextSize = 25;

    /**
     * 坐标字体边距
     */
    private float xYTextMargin = xYTextSize / 5;

    /**
     * 列数
     */
    private int column = 2;

    /**
     * 横数
     */
    private int row = 2;

    /**
     * 虚线效果
     */
    private PathEffect mDashEffect = DEFAULT_DASH_EFFECT;

    /**
     * 当前价格集合
     */
    private List<Float> stockPriceList = new ArrayList<>();
    private Paint xYTextPaint;
    private Paint xYTextBgPaint;
    private float lastClose = 0.0f;
    private float maxStockPrice = 0.0f;
    private float minStockPrice = 0.0f;

    private DecimalFormat decimalFormat;

    private Paint columnPaint;
    private Paint rowPaint;
    private Path path;

    public KView(Context context) {
        this(context, null);
    }

    public KView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        columnPaint = new Paint();
        columnPaint.setColor(ResUtil.getColor(R.color.k_view_column));
        columnPaint.setStyle(Paint.Style.STROKE);
        columnPaint.setPathEffect(mDashEffect);

        rowPaint = new Paint();
        rowPaint.setColor(ResUtil.getColor(R.color.k_view_row));
        rowPaint.setStyle(Paint.Style.STROKE);
        rowPaint.setPathEffect(mDashEffect);

        path = new Path();

        xYTextPaint = new Paint();
        xYTextPaint.setTextSize(xYTextSize);
        xYTextPaint.setAntiAlias(true);
        xYTextPaint.setStyle(Paint.Style.FILL);
        xYTextPaint.setColor(Color.RED);
        decimalFormat = new DecimalFormat("0.00");

        xYTextBgPaint = new Paint();
        xYTextBgPaint.setColor(Color.YELLOW);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int viewHeight = getHeight();
        int viewWidth = getWidth();
        maxStockPrice = getMaxStockPrice();
        minStockPrice = getMinStockPrice();

        float columnSpacing = (viewWidth - 2 * margin) / column;
        float rowSpacing = viewHeight / row;

        // 绘制边框
        drawBorders(canvas, viewHeight, viewWidth);

        // 绘制竖线
        drawColumnLine(canvas, viewHeight, columnSpacing);

        // 绘制横线
        drawRowLine(canvas, viewHeight, viewWidth, rowSpacing);

        // 绘制坐标
        drawXYText(canvas, viewHeight, viewWidth);
    }

    /**
     * 绘制边框
     */
    private void drawBorders(Canvas canvas, int viewHeight, int viewWidth) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ResUtil.getColor(R.color.k_view_frame));
        paint.setStrokeWidth(1);
        canvas.drawLine(margin, 1, viewWidth - margin, 1, paint);
        canvas.drawLine(viewWidth - margin, viewHeight - 1, margin, viewHeight - 1, paint);
    }

    /**
     * 绘制竖线
     */
    private void drawColumnLine(Canvas canvas, int viewHeight, float space) {
        for (int i = 1; i < column; i++) {
            path.reset();
            path.moveTo(margin + space * i, 1);
            path.lineTo(margin + space * i, viewHeight - 1);
            canvas.drawPath(path, columnPaint);
        }
    }

    /**
     * 绘制横线
     */
    private void drawRowLine(Canvas canvas, int viewHeight, int viewWidth, float space) {
        for (int i = 1; i < row; i++) {
            path.reset();
            path.moveTo(margin, viewHeight + 1 - space * i);
            path.lineTo(viewWidth - margin, viewHeight + 1 - space * i);
            canvas.drawPath(path, rowPaint);
        }
    }

    /**
     * 绘制坐标
     */
    private void drawXYText(Canvas canvas, int viewHeight, int viewWidth) {
        // 价格最大值
        String minPrice = decimalFormat.format(minStockPrice);
        Rect rect1 = new Rect();
        xYTextPaint.getTextBounds(minPrice, 0, minPrice.length(), rect1);
        rect1.left += margin;
        rect1.top += xYTextSize - xYTextMargin;
        rect1.right += rect1.left + xYTextMargin * 2;
        rect1.bottom += xYTextMargin + xYTextSize;
        canvas.drawRect(rect1, xYTextBgPaint);
        canvas.drawText(minPrice, margin + xYTextMargin, xYTextSize, xYTextPaint);

        // 价格最小值
        String maxPrice = decimalFormat.format(maxStockPrice);
        xYTextPaint.getTextBounds(maxPrice, 0, maxPrice.length(), rect1);
        rect1.left += margin;
        rect1.right += rect1.left + xYTextMargin * 2;
        rect1.bottom += viewHeight - xYTextMargin / 2;
        rect1.top = (int) (rect1.bottom - xYTextSize - xYTextMargin / 2);
        canvas.drawRect(rect1, xYTextBgPaint);
        canvas.drawText(maxPrice, margin + xYTextMargin, rect1.bottom - xYTextMargin, xYTextPaint);

        // 增幅
        String increasePrice = " 10.00%";
        xYTextPaint.getTextBounds(increasePrice, 0, increasePrice.length(), rect1);
        rect1.left += viewWidth - rect1.right - xYTextMargin * 3 - margin * 2;
        rect1.top += xYTextSize - xYTextMargin;
        rect1.right += rect1.left + xYTextMargin * 2;
        rect1.bottom += xYTextMargin + xYTextSize;
        canvas.drawRect(rect1, xYTextBgPaint);
        canvas.drawText(increasePrice.trim(), rect1.left + xYTextMargin, xYTextSize, xYTextPaint);

        // 减幅
        String decreasePrice = " 10.00%";
        xYTextPaint.getTextBounds(decreasePrice, 0, decreasePrice.length(), rect1);
        rect1.left += viewWidth - rect1.right - xYTextMargin * 3 - margin * 2;
        rect1.right += rect1.left + xYTextMargin * 2;
        rect1.top = (int) (viewHeight - xYTextSize - xYTextMargin);
        rect1.bottom = (int) (viewHeight - xYTextMargin / 2);
        canvas.drawRect(rect1, xYTextBgPaint);
        canvas.drawText("-" + decreasePrice.trim(), rect1.left + xYTextMargin,
                rect1.bottom - xYTextMargin, xYTextPaint);
    }

    public void setDate(TimeSharing timeSharing) {
        String[] stockPrices = timeSharing.stockPrice.split(",");
        if (stockPrices.length > 0) {
            if (stockPriceList != null && stockPriceList.size() > 0) {
                stockPriceList.clear();
            }

            for (String stockPrice : stockPrices) {
                stockPriceList.add(Float.parseFloat(stockPrice));
            }
        }
        lastClose = timeSharing.lastClose;
    }

    /**
     * 价格的最大值
     *
     * @return
     */
    private float getMaxStockPrice() {
        float max = 0.0f;
        if (stockPriceList != null) {
            for (int i = 0; i < stockPriceList.size(); i++) {
                float price = stockPriceList.get(i);
                float off = Math.abs(price - lastClose);
                max = Math.max(off, max);
            }
        }
        return max;
    }

    /**
     * 价格最小值
     *
     * @return
     */
    private float getMinStockPrice() {
        float min = 0.0f;
        if (stockPriceList != null && stockPriceList.size() > 0) {
            for (int i = 0; i < stockPriceList.size(); i++) {
                float nextPrice = stockPriceList.get(i);
                if (i == 0) {
                    min = nextPrice;
                }
                min = Math.min(min, nextPrice);
            }
        }
        return min;
    }
}
