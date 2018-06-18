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

    private static final int COUNT_DEFAULT = 240;
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
    private Paint priceLinePaint;

    /**
     * 当前交易量
     */
    private List<Float> stockVolumeList = new ArrayList<>();
    private float maxStokcVolume;

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

        priceLinePaint = new Paint();
        priceLinePaint.setAntiAlias(true);
        priceLinePaint.setStrokeWidth(2);
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

        // 绘制边框
        drawBorders(canvas);

        // 绘制竖线
        drawColumnLine(canvas);

        // 绘制横线
        drawRowLine(canvas);

        // 绘制价格线
        drawPriceLine(canvas);

        // 绘制坐标
        drawXYText(canvas);

        // 绘制时间坐标
        drawTimeText(canvas);

        // 绘制柱形
        drawPillar(canvas);
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
    private void drawColumnLine(Canvas canvas) {
        float columnSpacing = (viewWidth - 2 * margin) / column;
        for (int i = 1; i < column; i++) {
            path.reset();
            path.moveTo(margin + columnSpacing * i, 1);
            path.lineTo(margin + columnSpacing * i, topTableHeight - 1);
            canvas.drawPath(path, columnPaint);
        }
    }

    /**
     * 绘制横线
     */
    private void drawRowLine(Canvas canvas) {
        float rowSpacing = topTableHeight / row;
        for (int i = 1; i < row; i++) {
            path.reset();
            path.moveTo(margin, topTableHeight + 1 - rowSpacing * i);
            path.lineTo(viewWidth - margin, topTableHeight + 1 - rowSpacing * i);
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
        rect1.left += viewWidth - rect1.width() - margin * 3;
        rect1.right += rect1.left + margin + xYTextMargin;
        rect1.top += xYTextSize - xYTextMargin;
        rect1.bottom += xYTextMargin + xYTextSize;
        canvas.drawRect(rect1, xYTextBgPaint);
        canvas.drawText(percent.trim(), rect1.left + xYTextMargin, xYTextSize, xYTextPaint);

        // 减幅
        xYTextPaint.getTextBounds(percent, 0, percent.length(), rect1);
        rect1.left += viewWidth - rect1.width() - margin * 3;
        rect1.right += rect1.left + margin + xYTextMargin;
        rect1.top = (int) (topTableHeight - xYTextSize - xYTextMargin);
        rect1.bottom = (int) (topTableHeight - xYTextMargin / 2);
        canvas.drawRect(rect1, xYTextBgPaint);
        canvas.drawText("-" + percent.trim(), rect1.left + margin / 2,
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
    private void drawPriceLine(Canvas canvas) {
        path.reset();
        path.moveTo(margin, lastClose - stockPriceList.get(0) + topTableHeight / 2);
        for (int i = 1; i < stockPriceList.size(); i++) {
            path.lineTo(margin + xSpace * i, (lastClose - stockPriceList.get(i)) * ySpace + topTableHeight / 2);
        }

        //渐变效果
//        LinearGradient gradient = new LinearGradient(0.0f,
//                (0),
//                0,
//                topTableHeight,
//                ResUtil.getColor(R.color.red),
//                ResUtil.getColor(R.color.k_view_price_line_bg),
//                Shader.TileMode.CLAMP);
//        priceLinePaint.setShader(gradient);

        priceLinePaint.setColor(ResUtil.getColor(R.color.k_view_price_line));
        priceLinePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, priceLinePaint);
        path.lineTo(viewWidth - margin, topTableHeight - 1);
        path.lineTo(margin, topTableHeight - 1);
        path.lineTo(margin, lastClose - stockPriceList.get(0) + topTableHeight / 2);
        priceLinePaint.setColor(ResUtil.getColor(R.color.k_view_price_line_bg));
        priceLinePaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(path, priceLinePaint);
    }

    /**
     * 绘制柱形
     */
    private void drawPillar(Canvas canvas) {
        float columnSpace = (viewWidth - margin * 2) / (COUNT_DEFAULT * 2);
        Paint paint = new Paint();
        paint.setStrokeWidth(columnSpace);
        for (int i = 0; i < stockPriceList.size(); i++) {
            if (stockPriceList.get(i) >= lastClose) {
                paint.setColor(ResUtil.getColor(R.color.red));
            } else {
                paint.setColor(ResUtil.getColor(R.color.green));
            }

            canvas.drawLine(margin + columnSpace * i * 2,
                    viewHeight - 1,
                    margin + columnSpace * i * 2,
                    viewHeight - (stockVolumeList.get(i) / maxStokcVolume) * bottomTableHeight * 0.95f, paint);
        }
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

        String[] stockVolumes = timeSharing.stockVolume.split(",");
        if (stockVolumes.length > 0) {
            if (stockVolumeList != null && stockVolumeList.size() > 0) {
                stockVolumeList.clear();
            }

            for (String stockVolume : stockVolumes) {
                stockVolumeList.add(Float.parseFloat(stockVolume));
            }
        }
        for (Float stockVolume : stockVolumeList) {
            if (maxStokcVolume < stockVolume) {
                maxStokcVolume = stockVolume;
            }
        }
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
        xSpace = (viewWidth - margin * 2) / (float) COUNT_DEFAULT;
        ySpace = (topTableHeight / (maxY - minY));
    }
}
