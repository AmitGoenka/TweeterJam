package org.agoenka.tweeterjam.views;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.malmstein.fenster.controller.MediaFensterPlayerController;
import com.malmstein.fenster.controller.SimpleMediaFensterPlayerController;
import com.malmstein.fenster.view.FensterVideoView;
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

    public static void loadScalableVideo(Context context, ScalableVideoView view, String url) {
        try {
            view.setVisibility(View.VISIBLE);
            view.setDataSource(context, Uri.parse(url));
            view.setLooping(true);
            view.prepareAsync(mp -> view.start());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFensterVideo(Window window, ViewGroup layout, FensterVideoView view, SimpleMediaFensterPlayerController controller, String url) {
        layout.setVisibility(View.VISIBLE);
        controller.setVisibilityListener(value -> {
            int newVis = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;

            if (!value) {
                newVis |= View.SYSTEM_UI_FLAG_LOW_PROFILE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            }

            final View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(newVis);
            decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                if ((visibility & View.SYSTEM_UI_FLAG_LOW_PROFILE) == 0) {
                    controller.show();
                }
            });
        });
        view.setMediaController(controller);
        view.setOnPlayStateListener(controller);
        view.setVideo(url, MediaFensterPlayerController.DEFAULT_VIDEO_START);
        view.start();
    }

}