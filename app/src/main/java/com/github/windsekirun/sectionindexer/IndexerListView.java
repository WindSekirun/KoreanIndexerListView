package com.github.windsekirun.sectionindexer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class IndexerListView extends ListView {
    private Context context;

    private static int indWidth = 20;
    private String[] sections;
    private float scaledWidth;
    private float sx;
    private int indexSize;
    private String section;
    private boolean showLetter = true;
    private Handler listHandler;

    public IndexerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public IndexerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        scaledWidth = indWidth * getSizeInPixel(context);
        sx = this.getWidth() - this.getPaddingRight() - scaledWidth;

        Paint p = new Paint();
        p.setColor(Color.WHITE);
        p.setAlpha(100);

        canvas.drawRoundRect(new RectF(sx, this.getPaddingTop(), sx + scaledWidth,
                this.getHeight() - this.getPaddingBottom()), 60f, 60f, p);

        indexSize = (this.getHeight() - this.getPaddingTop() - getPaddingBottom()) / sections.length;

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(scaledWidth / 2);

        for (int i = 0; i < sections.length; i++)
            canvas.drawText(sections[i].toUpperCase(), sx + textPaint.getTextSize() / 2,
                    getPaddingTop() + indexSize * (i + 1), textPaint);
    }

    private static float getSizeInPixel(Context ctx) {
        return ctx.getResources().getDisplayMetrics().density;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof SectionIndexer)
            sections = (String[]) ((SectionIndexer) adapter).getSections();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (x < sx)
                    return super.onTouchEvent(event);
                else {
                    try {
                        float y = event.getY() - this.getPaddingTop() - getPaddingBottom();
                        int currentPosition = (int) Math.floor(y / indexSize);
                        section = sections[currentPosition];
                        this.setSelection(((SectionIndexer) getAdapter()).getPositionForSection(currentPosition));
                    } catch (Exception e) {

                    }
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (x < sx)
                    return super.onTouchEvent(event);
                else {
                    try {
                        float y = event.getY();
                        int currentPosition = (int) Math.floor(y / indexSize);
                        section = sections[currentPosition];
                        this.setSelection(((SectionIndexer) getAdapter()).getPositionForSection(currentPosition));
                    } catch (Exception e) {
                    }

                }
                break;

            }
            case MotionEvent.ACTION_UP: {
                listHandler = new ListHandler();
                listHandler.sendEmptyMessageDelayed(0, 30 * 1000);
                break;
            }
        }
        return true;
    }

    @SuppressLint("HandlerLeak")
    private class ListHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            showLetter = false;
            IndexerListView.this.invalidate();
        }
    }
}