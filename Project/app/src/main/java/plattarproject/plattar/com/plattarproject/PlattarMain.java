package plattarproject.plattar.com.plattarproject;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;

import com.plattar.android.PlattarAppView;
import com.plattar.android.cvengine.managers.core.CVManager;
import com.plattar.android.interfaces.AppSettings;
import com.plattar.android.interfaces.PlattarEngineCore;
import com.plattar.android.plattar.callback.PlattarStateListener;
import com.plattar.android.plattar.main.PlattarApplication;
import com.plattar.android.plattar.permissions.PlattarPermissionManager;
import com.plattar.android.plattar.permissions.interfaces.PermissionCallback;
import com.plattar.android.plattar.settings.PlattarSettings;
import com.plattar.android.plattar.webview.client.PlattarChromeClient;
import com.plattar.android.util.ExitDialog;

/**
 * Launches the ARCore Backed Plattar app. Note that Plattar supports alternative tracker
 * backends however they are not supported by this example repository.
 *
 * To run this example, the user must have an ARCore enabled device. For a full list of supported
 * devices please visit https://developers.google.com/ar/discover/
 */
public final class PlattarMain extends Activity implements PlattarStateListener {

    private PlattarApplication app;
    private PlattarPermissionManager perms;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // plattar only works in Portrait Orientation for now. Landscape orientations
        // are unsupported and may cause unexpected errors/problems
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // perform all setup from scratch
        init();
    }

    private final void init() {
        // before we do anything, we need to ensure all required permissions are granted
        // by the user
        askPermissions(new PermissionCallback() {
            @Override
            public void onComplete(final boolean success) {
                // if all permissions have been granted properly we can proceed
                // otherwise fail and exit
                if (!success) {
                    // show a friendly error message and exit
                    ExitDialog.show(PlattarMain.this, "One or more permissions were denied. Cannot proceed!");
                    return;
                }

                // otherwise, we now need to ensure that the Tracking service can be
                // ran on this device. For a full list of ARCore supported devices please
                // visit https://developers.google.com/ar/discover/
                checkARCoreSupport(new PermissionCallback() {
                    @Override
                    public void onComplete(final boolean success) {
                        // looks like ARCore is not supported
                        if (!success) {
                            // show a friendly error message and exit
                            ExitDialog.show(PlattarMain.this, "ARCore is not supported on this device!");
                            return;
                        }

                        // we are finally good to go with the launch of the app
                        setupPlattar();
                    }
                });
            }
        });
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

        // we only support NATIVEAR for this particular example. Other backends require further setup.
        app = new PlattarAppView(this, settings, null, PlattarEngineCore.EngineCoreType.NATIVEAR);

        app.registerCallback(this);

        // this will push all internal GL/Rendering views into the top of the current activity.
        // if required we can create more flexible interfaces for this to be done manually.
        app.show();
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

        perms.ask(new PermissionCallback() {
            @Override
            public void onComplete(final boolean success) {
                // allow perms instance to be garbage collected
                perms = null;

                if (callback != null) {
                    callback.onComplete(success);
                }
            }
        });
    }

    /**
     * Perform a check for ARCore Support. Just reuse the PermissionCallback for a simple
     * boolean true/false return type. (could use builtin callbacks etc since Java 1.8)
     *
     * @param callback - Called once successful/failed
     */
    private final void checkARCoreSupport(final PermissionCallback callback) {
        final CVManager.SupportStatus status = CVManager.isARCoreSupported(this);

        // we have support! great, we can launch Plattar using the NATIVEAR feature
        if (status == CVManager.SupportStatus.SUPPORTED) {
            callback.onComplete(true);

            return;
        }

        // we have support, but the user has not installed the required libraries. ARCore would
        // have prompted at this stage for the user to install. This activity is due to be re-launched
        // once installation is complete
        if (status == CVManager.SupportStatus.INSTALLING) {
            return;
        }

        // there was some kind of internal delay on checking for support. We should re-try this until
        // a proper support/unsupported event occurs
        if (status == CVManager.SupportStatus.CHECKING) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkARCoreSupport(callback);
                }
            }, 200);

            return;
        }

        // we do not have ARCore support, let the calback know
        callback.onComplete(false);
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

    @Override
    public void onResume() {
        super.onResume();

        // if we don't have an app, just init everything
        if (app == null) {
            init();
        }
        else {
            app.resume();
        }
    }

    @Override
    protected void onPause() {
        if (app != null) {
            app.pause();
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (app != null) {
            app.destroy();
        }

        super.onDestroy();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // statically callback to our Chrome client instance. This is required for very specific
        // user actions that do not support internal app hooks
        PlattarChromeClient.onActivityResult(requestCode, resultCode, intent);
    }
}
