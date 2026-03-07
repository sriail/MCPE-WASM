package net.hockeyapp.android.views;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.internal.view.SupportMenu;
import android.view.MotionEvent;
import android.widget.ImageView;
import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;
import net.hockeyapp.android.utils.HockeyLog;

@SuppressLint({"ViewConstructor"})
public class PaintView extends ImageView {
    private static final float TOUCH_TOLERANCE = 4.0f;
    private float mX;
    private float mY;
    private Paint paint = new Paint();
    private Path path = new Path();
    private Stack<Path> paths = new Stack<>();

    public static int determineOrientation(ContentResolver resolver, Uri imageUri) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(resolver.openInputStream(imageUri), (Rect) null, options);
            if (((float) options.outWidth) / ((float) options.outHeight) > 1.0f) {
                return 0;
            }
            return 1;
        } catch (IOException e) {
            HockeyLog.error("Unable to determine necessary screen orientation.", (Throwable) e);
            return 1;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /* access modifiers changed from: private */
    public static Bitmap decodeSampledBitmapFromResource(ContentResolver resolver, Uri imageUri, int reqWidth, int reqHeight) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(resolver.openInputStream(imageUri), (Rect) null, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(resolver.openInputStream(imageUri), (Rect) null, options);
    }

    public PaintView(Context context, Uri imageUri, int displayWidth, int displayHeight) {
        super(context);
        this.paint.setAntiAlias(true);
        this.paint.setDither(true);
        this.paint.setColor(SupportMenu.CATEGORY_MASK);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(12.0f);
        new AsyncTask<Object, Void, Bitmap>() {
            /* access modifiers changed from: protected */
            public void onPreExecute() {
                PaintView.this.setAdjustViewBounds(true);
            }

            /* access modifiers changed from: protected */
            public Bitmap doInBackground(Object... args) {
                Context context = args[0];
                try {
                    return PaintView.decodeSampledBitmapFromResource(context.getContentResolver(), args[1], args[2].intValue(), args[3].intValue());
                } catch (IOException e) {
                    HockeyLog.error("Could not load image into ImageView.", (Throwable) e);
                    return null;
                }
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(Bitmap bm) {
                if (bm != null) {
                    PaintView.this.setImageBitmap(bm);
                }
            }
        }.execute(new Object[]{context, imageUri, Integer.valueOf(displayWidth), Integer.valueOf(displayHeight)});
    }

    public void clearImage() {
        this.paths.clear();
        invalidate();
    }

    public void undo() {
        if (!this.paths.empty()) {
            this.paths.pop();
            invalidate();
        }
    }

    public boolean isClear() {
        return this.paths.empty();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Iterator it = this.paths.iterator();
        while (it.hasNext()) {
            canvas.drawPath((Path) it.next(), this.paint);
        }
        canvas.drawPath(this.path, this.paint);
    }

    private void touchStart(float x, float y) {
        this.path.reset();
        this.path.moveTo(x, y);
        this.mX = x;
        this.mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - this.mX);
        float dy = Math.abs(y - this.mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            this.path.quadTo(this.mX, this.mY, (this.mX + x) / 2.0f, (this.mY + y) / 2.0f);
            this.mX = x;
            this.mY = y;
        }
    }

    private void touchUp() {
        this.path.lineTo(this.mX, this.mY);
        this.paths.push(this.path);
        this.path = new Path();
    }

    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case 0:
                touchStart(x, y);
                invalidate();
                return true;
            case 1:
                touchUp();
                invalidate();
                return true;
            case 2:
                touchMove(x, y);
                invalidate();
                return true;
            default:
                return true;
        }
    }
}
