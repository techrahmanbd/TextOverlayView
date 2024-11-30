package com.techrahman.text_overlay;

import android.view.View;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import java.util.ArrayList;
import java.util.List;

public class TextOverlayView extends View {

    // Text item to hold individual text properties
    static class TextItem {
        String text;
        float x, y;
        float textSize;
        Paint paint;

        public TextItem(String text, float x, float y, float textSize, int color) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.textSize = textSize;

            paint = new Paint();
            paint.setColor(color);
            paint.setTextSize(textSize);
        }
    }

    private List<TextItem> textItems; // List to hold multiple texts
    private TextItem activeTextItem = null; // Currently selected text item
    private ScaleGestureDetector scaleGestureDetector;

    private float screenWidth;
    private float screenHeight;

    public TextOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        textItems = new ArrayList<>();
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (activeTextItem != null) {
                    float scaleFactor = detector.getScaleFactor();
                    activeTextItem.textSize *= scaleFactor;
                    activeTextItem.textSize = Math.max(20, Math.min(activeTextItem.textSize, 200)); // Limit text size
                    activeTextItem.paint.setTextSize(activeTextItem.textSize);
                    invalidate();
                }
                return true;
            }
        });
    }

    public void addText(String text, float textSize, int color) {
        // Default vertical positioning
        float defaultX = 50; // Default left padding
        float defaultY = textItems.size() * (textSize + 20) + textSize; // Adjust for spacing

        textItems.add(new TextItem(text, defaultX, defaultY, textSize, color));
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        screenWidth = getWidth();
        screenHeight = getHeight();

        // Draw all text items
        for (TextItem item : textItems) {
            // Keep text within screen bounds
            item.x = Math.max(0, Math.min(item.x, screenWidth - item.paint.measureText(item.text)));
            item.y = Math.max(item.textSize, Math.min(item.y, screenHeight));
            canvas.drawText(item.text, item.x, item.y, item.paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                activeTextItem = findTextItemAt(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                if (activeTextItem != null) {
                    activeTextItem.x = event.getX();
                    activeTextItem.y = event.getY();
                    invalidate();
                }
                break;

            case MotionEvent.ACTION_UP:
                activeTextItem = null;
                break;
        }
        return true;
    }

    private TextItem findTextItemAt(float touchX, float touchY) {
        for (TextItem item : textItems) {
            float textWidth = item.paint.measureText(item.text);
            if (touchX >= item.x && touchX <= item.x + textWidth &&
                    touchY >= item.y - item.textSize && touchY <= item.y) {
                return item;
            }
        }
        return null;
    }
}

