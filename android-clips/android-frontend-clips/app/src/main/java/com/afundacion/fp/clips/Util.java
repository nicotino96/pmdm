package com.afundacion.fp.clips;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Util {

    /**
     * Creates a secondary thread and opens a connection to the given url.
     * The obtained bitmap is then placed into the imageView with `setImageBitmap`
     * from the UI Thread.
     *
     * @param url An HTTP URL holding a valid image.
     * @param imageView An instance of ImageView where the result will be placed into.
     */
    public static void downloadBitmapToImageView(String url, ImageView imageView) {
        Context context = imageView.getContext();
        if (context instanceof Activity) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap image = getBitmapFromURL(url);
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(image);
                        }
                    });
                }
            });
            thread.start();
        }
    }

    private static Bitmap getBitmapFromURL(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
