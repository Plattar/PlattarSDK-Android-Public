package plattarproject.plattar.com.plattarproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

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
        if (app != null) {
            app.start();
        }
        else {
            final String appID = getString(R.string.app_code);
            app = com.plattar.android.PlattarMain.init(this);
            app.setup(new PlattarSettings(appID));

            app.registerForEventCallback(PlattarWebEvent.WebEvent.onWebGLReady, (webEvent, jsonValue) -> {
                // This event can be used to know when the plattar view has loaded and launched
            });

            app.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        launchPlattar();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Used to pipe permissions into the permission manager and the app
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        PlattarPermission.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        PlattarChromeClient.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
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
