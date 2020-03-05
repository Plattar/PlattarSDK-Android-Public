package plattarproject.plattar.com.plattarproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.plattar.android.plattar.events.PlattarWebEvent;
import com.plattar.android.plattar.interfaces.PlattarEngine;
import com.plattar.android.plattar.permissions.PlattarPermission;
import com.plattar.android.plattar.settings.PlattarSettings;
import com.plattar.android.plattar.webview.client.PlattarChromeClient;

/**
 * Launches the ARCore Backed Plattar app.
 *
 * To run this example, the user must have an ARCore enabled device. For a full list of supported
 * devices please visit https://developers.google.com/ar/discover/
 */
public final class PlattarMain extends Activity {

    private PlattarEngine app;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // plattar only works in Portrait Orientation for now. Landscape orientations
        // are unsupported and may cause unexpected errors/problems
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        launchPlattar();
    }

    private void launchPlattar() {
        final String appID = getString(R.string.app_code);

        // PlattarMain.init() and setup() functions can be run pre-emptively
        // to warm-up the AR systems. The warmup ensures that Plattar starts up.
        app = com.plattar.android.PlattarMain.init(this);

        // setup is run internally and is an async process.
        app.setup(new PlattarSettings(appID));

        app.registerForEventCallback(PlattarWebEvent.WebEvent.onWebGLReady, (webEvent, jsonValue) -> {
            // This callback occurs when the SDK has finished loading and is ready for rendering
        });

        // app.start() will connect to the Plattar CMS and start the AR component.
        // This can be called when AR is actually needed (ie, user clicks the AR button). If
        // warmup is completed then this will be a faster process.
        app.start();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (app != null) {
            // allow the activity to be GC'd
            com.plattar.android.PlattarMain.setActivity(this);

            app.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (app != null) {
            app.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (app != null) {
            app.destroy();

            // allow the activity to be GC'd
            com.plattar.android.PlattarMain.setActivity(null);
        }
    }

    /**
     * Used to pipe permissions into the permission manager and the app
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        PlattarPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        PlattarChromeClient.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        final View mDecorView = getWindow().getDecorView();

        if (hasFocus) {
            mDecorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
