package com.microsoft.xbox.toolkit.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.text.TextPaint;
import android.widget.ImageView;
import com.microsoft.xbox.toolkit.ui.XLETextArg;
import java.lang.ref.WeakReference;

public class XLETextTask extends AsyncTask<XLETextArg, Void, Bitmap> {
    private static final String TAG = XLETextTask.class.getSimpleName();
    private final WeakReference<ImageView> img;
    private final int imgHeight;
    private final int imgWidth;

    public XLETextTask(ImageView img2) {
        this.img = new WeakReference<>(img2);
        this.imgWidth = img2.getWidth();
        this.imgHeight = img2.getHeight();
    }

    /* access modifiers changed from: protected */
    public Bitmap doInBackground(XLETextArg... args) {
        Bitmap bm = null;
        if (args.length > 0) {
            XLETextArg arg = args[0];
            XLETextArg.Params params = arg.getParams();
            String msg = arg.getText();
            TextPaint p = new TextPaint();
            p.setTextSize(params.getTextSize());
            p.setAntiAlias(true);
            p.setColor(params.getColor());
            p.setTypeface(params.getTypeface());
            int width = Math.round(p.measureText(msg));
            int height = Math.round(p.descent() - p.ascent());
            int bmWidth = width;
            int bmHeight = height;
            if (params.isAdjustForImageSize()) {
                bmWidth = Math.max(width, this.imgWidth);
                bmHeight = Math.max(height, this.imgHeight);
            }
            if (params.hasTextAspectRatio()) {
                float ar = params.getTextAspectRatio().floatValue();
                if (ar > 0.0f) {
                    if (((float) bmHeight) > ((float) bmWidth) * ar) {
                        bmWidth = (int) (((float) bmHeight) / ar);
                    } else {
                        bmHeight = (int) (((float) bmWidth) * ar);
                    }
                }
            }
            bm = Bitmap.createBitmap(bmWidth, bmHeight, Bitmap.Config.ARGB_8888);
            if (params.hasEraseColor()) {
                bm.eraseColor(params.getEraseColor());
            }
            new Canvas(bm).drawText(msg, (float) ((Math.max(0, bmWidth - width) / 2) + 0), (-p.ascent()) + ((float) (Math.max(0, bmHeight - height) / 2)), p);
        }
        return bm;
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(Bitmap bm) {
        ImageView v = (ImageView) this.img.get();
        if (v != null) {
            v.setImageBitmap(bm);
        }
    }
}
