package com.github.mobile.android.issue;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;
import static java.util.Locale.US;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.PaintDrawable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import org.eclipse.egit.github.core.Label;

/**
 * Custom drawable for labels applied to an issue
 */
public class LabelsDrawable extends PaintDrawable {

    private static final int PADDING_LEFT = 10;

    private static final int PADDING_RIGHT = 10;

    private static final int PADDING_TOP = 10;

    private static final int PADDING_BOTTOM = 10;

    private static final int SIZE_SHADOW = 5;

    private static final int FIN = 8;

    private final Label[] labels;

    private final int height;

    /**
     * Create drawable for labels
     *
     * @param textSize
     * @param labels
     */
    public LabelsDrawable(final float textSize, final Collection<Label> labels) {
        this.labels = labels.toArray(new Label[labels.size()]);
        Arrays.sort(this.labels, new Comparator<Label>() {

            public int compare(Label lhs, Label rhs) {
                return lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        });

        Paint p = getPaint();
        p.setTypeface(Typeface.DEFAULT_BOLD);
        p.setTextSize(textSize);

        final Rect bounds = new Rect();
        bounds.right = PADDING_LEFT + PADDING_RIGHT;
        for (int i = 0; i < this.labels.length; i++)
            getSize(this.labels[i], i, bounds);
        height = bounds.height();
        bounds.bottom += PADDING_BOTTOM;
        setBounds(bounds);
    }

    private void getSize(final Label label, final int index, final Rect out) {
        getSize(label.getName().toUpperCase(US), index, out);
    }

    private void getSize(final String name, final int index, final Rect out) {
        Rect tBounds = new Rect();
        getPaint().getTextBounds(name, 0, name.length(), tBounds);
        float width = tBounds.width() + PADDING_LEFT + PADDING_RIGHT;
        if (index != 0)
            width += FIN * 2;
        else
            width += FIN;
        float height = tBounds.height() + PADDING_TOP + PADDING_BOTTOM;
        out.right += (int) Math.ceil(width);
        out.bottom = Math.max(out.bottom, (int) Math.ceil(height));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        Paint paint = getPaint();
        int original = paint.getColor();
        int start = PADDING_LEFT;
        Rect tBounds = new Rect();

        Label label = this.labels[0];
        String name = label.getName().toUpperCase(US);
        tBounds.setEmpty();
        getSize(name, 0, tBounds);
        int width = tBounds.width();
        int quarter = height / 4;
        final Path path = new Path();
        path.moveTo(start, 0);
        path.lineTo(start + width, 0);
        path.lineTo(start + width - FIN, quarter);
        path.lineTo(start + width, quarter * 2);
        path.lineTo(start + width - FIN, quarter * 3);
        path.lineTo(start + width, height);
        path.lineTo(start, height);
        path.lineTo(start, 0);

        paint.setColor(Color.parseColor('#' + label.getColor()));
        canvas.drawPath(path, paint);

        paint.setColor(WHITE);
        paint.setShadowLayer(SIZE_SHADOW, 0, 0, BLACK);
        canvas.drawText(name, start + PADDING_LEFT, height - PADDING_BOTTOM, paint);
        paint.clearShadowLayer();

        start += width;

        for (int i = 1; i < labels.length; i++) {
            label = labels[i];
            name = label.getName().toUpperCase();
            tBounds.setEmpty();
            getSize(name, i, tBounds);
            width = tBounds.width();
            quarter = height / 4;
            path.reset();
            path.moveTo(start + FIN, 0);
            path.lineTo(start + width, 0);
            path.lineTo(start + width - FIN, quarter);
            path.lineTo(start + width, quarter * 2);
            path.lineTo(start + width - FIN, quarter * 3);
            path.lineTo(start + width, height);
            path.lineTo(start + FIN, height);
            path.lineTo(start, quarter * 3);
            path.lineTo(start + FIN, quarter * 2);
            path.lineTo(start, quarter);
            path.lineTo(start + FIN, 0);

            paint.setColor(Color.parseColor('#' + label.getColor()));
            canvas.drawPath(path, paint);

            paint.setShadowLayer(SIZE_SHADOW, 0, 0, BLACK);
            paint.setColor(WHITE);
            canvas.drawText(name, start + PADDING_LEFT + FIN, height - PADDING_BOTTOM, paint);
            paint.clearShadowLayer();

            start += width;
        }

        paint.setColor(original);
    }
}
