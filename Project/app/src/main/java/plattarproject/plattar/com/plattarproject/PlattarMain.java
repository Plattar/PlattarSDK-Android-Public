package plattarproject.plattar.com.plattarproject;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.plattar.android.PlattarAppView;
import com.plattar.android.interfaces.AppSettings;
import com.plattar.android.interfaces.PlattarEngineCore;
import com.plattar.android.plattar.callback.PlattarStateListener;
import com.plattar.android.plattar.main.PlattarApplication;
import com.plattar.android.plattar.permissions.PlattarPermissionManager;
import com.plattar.android.plattar.permissions.interfaces.PermissionCallback;
import com.plattar.android.plattar.settings.PlattarSettings;

public final class PlattarMain extends Activity implements PlattarStateListener {

    private PlattarApplication app;
    private PlattarPermissionManager perms;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // plattar only works in Portrait Orientation for now. Landscape orientations
        // are unsupported and may cause unexpected errors/problems
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * Perform all setup operations required to launch the Plattar app
     */
    private final void setupPlattar() {
        // this is a Base64 encoded string of app specific data required by the UI system to setup
        // and initialise the SDK to CMS communication streams. This string can change on a
        // per app basis
        final String appID = "";

        // used to set certain settings/variables for plattar.
        final AppSettings settings = new PlattarSettings(appID);


        app = new PlattarAppView(this, settings, null, PlattarEngineCore.EngineCoreType.NATIVEAR);
        app.registerCallback(this);
    }

    /**
     * Called on the main UI thread as soon as the engine has completed initialization
     * and is ready for execution.
     *
     * @param plattarApplication - The Plattar Application instance. Advised not to store
     *                           this instance since it can be destroyed internally.
     */
    @Override
    public void onPlattarStarted(final PlattarApplication plattarApplication) {}

    /**
     * Called on the main UI thread as soon as the engine has stopped execution. The engine
     * can be killed directly or via the Plattar UI (which sits remote)
     *
     * @param plattarApplication - The Plattar Application instance. Advised not to store
     *                           this instance since it can be destroyed internally.
     */
    @Override
    public void onPlattarStopped(final PlattarApplication plattarApplication) {}

    /**
     * The Plattar UI runs in a separate Web View executing Javascript/HTML content. This generally
     * takes some time to load depending on internet speed etc.. This callback is called as soon as
     * The UI has loaded and is ready for display.
     *
     * @param plattarApplication - The Plattar Application instance. Advised not to store
     *                           this instance since it can be destroyed internally.
     */
    @Override
    public void onTemplateStarted(final PlattarApplication plattarApplication) {}

    /**
     * Simple function which deals with all required user permissions required by the app to
     * function properly. Plattar provides a simple Permissions Manager to deal with this in a
     * consistent manner. The PermissionCallback will be called as soon as the user has accepted
     * all required permissions.
     *
     * @param callback - Called as soon as all required permissions are accepted by the user. Returns true
     *                 when successful or false if one or more permissions were denied.
     */
    private final void askPermissions(final PermissionCallback callback) {
        if (perms == null) {
            perms = new PlattarPermissionManager(this);
        }

        perms.pushStack("SplashAccess");
        perms.pushPermission(PlattarPermissionManager.Permission.INTERNET);
        perms.pushPermission(PlattarPermissionManager.Permission.ACCESS_NETWORK_STATE);
        perms.pushPermission(PlattarPermissionManager.Permission.CAMERA);
        perms.pushPermission(PlattarPermissionManager.Permission.READ_EXTERNAL_STORAGE);
        perms.pushPermission(PlattarPermissionManager.Permission.WRITE_EXTERNAL_STORAGE);

        perms.ask(callback);
    }

    /**
     * Used to pipe permissions into the permission manager and the app
     */
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        if (perms != null) {
            perms.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        if (app != null) {
            // the app may ask for additional permissions depending on user actions. Pipe the results
            // so the app knows what to do.
            app.onRequestPermissionsResultCallback(requestCode, permissions, grantResults);
        }
    }
}
