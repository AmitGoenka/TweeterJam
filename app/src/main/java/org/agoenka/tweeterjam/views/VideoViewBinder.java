package org.agoenka.tweeterjam.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Author: agoenka
 * Created At: 11/5/2016
 * Version: ${VERSION}
 */

public class VideoViewBinder {

    private VideoViewBinder() {
        //no instance
    }

    public static void loadVideo(Context context, VideoView view, String url) {
        view.setVideoURI(null);
        view.setVisibility(View.VISIBLE);

        ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        MediaController mediaController = new MediaController(context);
        mediaController.setAnchorView(view);

        view.setMediaController(mediaController);
        view.setVideoURI(Uri.parse(url));
        view.requestFocus();
        view.setOnPreparedListener(mp -> {
            pDialog.dismiss();
            view.start();
        });
        view.setOnCompletionListener(mp -> {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        });
    }
}