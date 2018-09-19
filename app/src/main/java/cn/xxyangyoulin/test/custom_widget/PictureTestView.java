package cn.xxyangyoulin.test.custom_widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Picture;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * canvas中的 Picture
 */
public class PictureTestView extends View {

    private Picture picture;

    public PictureTestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        createPicture(context);

    }

    private void createPicture(Context context) {
        picture = new Picture();
        Canvas canvas = picture.beginRecording(200, 200);

        Paint paint  = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);

        canvas.translate(100,100);
        canvas.drawCircle(0,0,80,paint);

        picture.endRecording();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        canvas.drawColor(Color.GRAY);

//        picture.draw(canvas);
        canvas.drawPicture(picture);
    }
}
