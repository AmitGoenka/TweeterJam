package org.agoenka.tweeterjam.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.view.View;

import com.yqritc.scalablevideoview.ScalableVideoView;

import java.io.IOException;

/**
 * Author: agoenka
 * Created At: 11/5/2016
 * Version: ${VERSION}
 */

public class VideoViewBinder {

    private VideoViewBinder() {
        //no instance
    }

    public static void loadVideo(Context context, ScalableVideoView view, String url) {
        try {
            view.setVisibility(View.VISIBLE);
            view.setDataSource(context, Uri.parse(url));
            view.setLooping(true);
            view.prepareAsync(mp -> view.start());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadVideoWithProgress(Context context, ScalableVideoView view, String url) {
        try {
            view.setVisibility(View.VISIBLE);

            ProgressDialog pDialog = new ProgressDialog(context);
            pDialog.setMessage("Buffering...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            view.setDataSource(context, Uri.parse(url));
            view.setLooping(true);
            view.prepareAsync(mp -> {
                pDialog.dismiss();
                view.start();
            });
            view.setOnCompletionListener(mp -> {
                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}