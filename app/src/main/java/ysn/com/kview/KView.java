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

import com.lazy.library.logging.Logcat;

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
    private static final String[] timeText = new String[]{"09:30", "11:30/13:00", "15:00"};

    /**
     * 默认虚线效果
     */
    private static final PathEffect DEFAULT_DASH_EFFECT = new DashPathEffect(new float[]{2, 2, 2,
            2}, 1);

    /**
     * 两边边距
     */
    private float margin = 22;

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
    private float maxY = 0.0f;
    private float minY = 0.0f;
    private String percent = " 100%";

    private DecimalFormat decimalFormat;

    private Paint columnPaint;
    private Paint rowPaint;
    private Path path;

    private float viewHeight;
    private float topTableHeight;
    private float timeTableHeight;
    private float bottomTableHeight;
    private int viewWidth;
    private float xSpace;
    private float ySpace;
    private Paint linePaint;

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

        linePaint = new Paint();
        linePaint.setColor(ResUtil.getColor(R.color.colorAccent));
        linePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        viewHeight = getHeight();
        topTableHeight = (int) ((viewHeight - xYTextSize - 2) * 4 / 5);
        timeTableHeight = xYTextSize + 8;
        bottomTableHeight = viewHeight - topTableHeight - timeTableHeight - 1;
        viewWidth = getWidth();
        initXYText();

        float columnSpacing = (viewWidth - 2 * margin) / column;
        float rowSpacing = topTableHeight / row;

        // 绘制边框
        drawBorders(canvas);

        // 绘制竖线
        drawColumnLine(canvas, columnSpacing);

        // 绘制横线
        drawRowLine(canvas, rowSpacing);

        // 绘制坐标
        drawXYText(canvas);

        // 绘制时间坐标
        drawTimeText(canvas);

        // 绘制价格线
        drawLine(canvas);
    }

    /**
     * 绘制边框
     */
    private void drawBorders(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(ResUtil.getColor(R.color.k_view_frame));
        paint.setStrokeWidth(1);
        canvas.drawLine(margin, 1, viewWidth - margin, 1, paint);
        canvas.drawLine(margin, topTableHeight - 1, viewWidth - margin, topTableHeight - 1, paint);
        canvas.drawLine(margin, topTableHeight + timeTableHeight, viewWidth - margin,
                topTableHeight + timeTableHeight, paint);
        canvas.drawLine(viewWidth - margin, viewHeight - 1, margin, viewHeight - 1, paint);
        canvas.drawLine(viewWidth - margin, viewHeight - 1, margin, viewHeight - 1, paint);
    }

    /**
     * 绘制竖线
     */
    private void drawColumnLine(Canvas canvas, float space) {
        for (int i = 1; i < column; i++) {
            path.reset();
            path.moveTo(margin + space * i, 1);
            path.lineTo(margin + space * i, topTableHeight - 1);
            canvas.drawPath(path, columnPaint);
        }
    }

    /**
     * 绘制横线
     */
    private void drawRowLine(Canvas canvas, float space) {
        for (int i = 1; i < row; i++) {
            path.reset();
            path.moveTo(margin, topTableHeight + 1 - space * i);
            path.lineTo(viewWidth - margin, topTableHeight + 1 - space * i);
            canvas.drawPath(path, rowPaint);
        }
    }

    /**
     * 绘制坐标
     */
    private void drawXYText(Canvas canvas) {
        // 价格最大值
        String maxPrice = decimalFormat.format(maxY);
        Rect rect1 = new Rect();
        xYTextPaint.getTextBounds(maxPrice, 0, maxPrice.length(), rect1);
        rect1.left += margin;
        rect1.right += rect1.left + margin;
        rect1.top += xYTextSize - xYTextMargin;
        rect1.bottom += xYTextMargin + xYTextSize;
        canvas.drawRect(rect1, xYTextBgPaint);
        canvas.drawText(maxPrice, margin + margin / 2, xYTextSize, xYTextPaint);

        // 价格最小值
        String minPrice = decimalFormat.format(minY);
        xYTextPaint.getTextBounds(minPrice, 0, minPrice.length(), rect1);
        rect1.left += margin;
        rect1.right += rect1.left + margin;
        rect1.bottom += topTableHeight - xYTextMargin / 2;
        rect1.top = (int) (rect1.bottom - xYTextSize - xYTextMargin / 2);
        canvas.drawRect(rect1, xYTextBgPaint);
        canvas.drawText(minPrice, margin + margin / 2, rect1.bottom - xYTextMargin, xYTextPaint);

        // 增幅
        xYTextPaint.getTextBounds(percent, 0, percent.length(), rect1);
        rect1.left += viewWidth - rect1.width() -margin*3;
        rect1.right += rect1.left + margin + xYTextMargin;
        rect1.top += xYTextSize - xYTextMargin;
        rect1.bottom += xYTextMargin + xYTextSize;
        canvas.drawRect(rect1, xYTextBgPaint);
        canvas.drawText(percent.trim(), rect1.left + xYTextMargin, xYTextSize, xYTextPaint);

        // 减幅
        xYTextPaint.getTextBounds(percent, 0, percent.length(), rect1);
        rect1.left += viewWidth - rect1.width() -margin*3;
        rect1.right += rect1.left + margin + xYTextMargin;
        rect1.top = (int) (topTableHeight - xYTextSize - xYTextMargin);
        rect1.bottom = (int) (topTableHeight - xYTextMargin / 2);
        canvas.drawRect(rect1, xYTextBgPaint);
        canvas.drawText("-" + percent.trim(), rect1.left + margin/2,
                rect1.bottom - xYTextMargin, xYTextPaint);
    }

    /**
     * 绘制时间坐标
     */
    private void drawTimeText(Canvas canvas) {
        Rect rect = new Rect();
        xYTextPaint.getTextBounds(timeText[0], 0, timeText[0].length(), rect);
        canvas.drawText(timeText[0], margin + margin / 2,
                topTableHeight + (timeTableHeight - xYTextSize) / 2 + xYTextSize - xYTextMargin, xYTextPaint);
        xYTextPaint.getTextBounds(timeText[1], 0, timeText[0].length(), rect);
        canvas.drawText(timeText[1], (viewWidth - rect.right) / 2 - margin * 2,
                topTableHeight + (timeTableHeight - xYTextSize) / 2 + xYTextSize - xYTextMargin, xYTextPaint);
        xYTextPaint.getTextBounds(timeText[2], 0, timeText[0].length(), rect);
        canvas.drawText(timeText[2], viewWidth - rect.right - margin - margin / 2,
                topTableHeight + (timeTableHeight - xYTextSize) / 2 + xYTextSize - xYTextMargin, xYTextPaint);
    }

    /**
     * 绘制价格线
     */
    private void drawLine(Canvas canvas) {
        path.reset();
        path.moveTo(margin, lastClose - stockPriceList.get(0) + topTableHeight / 2);
        for (int i = 1; i < stockPriceList.size(); i++) {
            path.lineTo(margin + xSpace * i, (lastClose - stockPriceList.get(i)) * ySpace + topTableHeight / 2);
            Logcat.d("i: " + margin + xSpace * i);
        }
        canvas.drawPath(path, linePaint);
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

    private void initXYText() {
        for (Float stockPrice : stockPriceList) {
            if (maxY < stockPrice) {
                maxY = stockPrice;
            }
        }

        for (int i = 0; i < stockPriceList.size(); i++) {
            if (i == 0) {
                maxY = stockPriceList.get(i);
                minY = stockPriceList.get(i);
            }

            if (maxY < stockPriceList.get(i)) {
                maxY = stockPriceList.get(i);
            } else if (minY > stockPriceList.get(i)) {
                minY = stockPriceList.get(i);
            }
        }

        if (Math.abs(minY - lastClose) > Math.abs(maxY - lastClose)) {
            float temp = maxY;
            maxY = minY;
            minY = temp;
        }

        if (maxY > lastClose) {
            minY = lastClose * 2 - maxY;
        } else {
            minY = maxY;
            maxY = lastClose * 2 - maxY;
        }

        percent = " " + decimalFormat.format((maxY - lastClose) / 100) + "%";
        xSpace = (viewWidth - margin * 2) / (float) POINT_COUNT_DEFAULT;
        ySpace = (topTableHeight / (maxY - minY));
    }
}
