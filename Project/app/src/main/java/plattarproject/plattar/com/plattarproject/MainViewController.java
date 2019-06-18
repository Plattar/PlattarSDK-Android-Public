package plattarproject.plattar.com.plattarproject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.plattar.android.plattar.interfaces.PlattarEngine;

public final class MainViewController {
    private PlattarEngine app;
    private final View mainView;

    public MainViewController(final Activity mainActivity) {
        final LayoutInflater vi = (LayoutInflater) mainActivity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (vi != null) {
            mainView = vi.inflate(R.layout.main_view, null);
            mainActivity.setContentView(mainView);

            setCallbacks();
        }
        else {
            mainView = null;
        }
    }

    private final void setCallbacks() {
        if (mainView == null) {
            return;
        }

        final Button openAppButton = mainView.findViewById(R.id.open_app_button);

        if (openAppButton != null) {
            openAppButton.setOnClickListener(view -> {
                if (app != null) {
                    app.getAsyncBridge().openApplication("3543b61c-7b44-4f90-a92f-4352952e5fe1");
                }
            });
        }

        final Button openSceneButton = mainView.findViewById(R.id.open_scene_button);

        if (openSceneButton != null) {
            openSceneButton.setOnClickListener(view -> {
                if (app != null) {
                    app.getAsyncBridge().openScene("f584ab37-c542-4536-9b63-dd41a167144a");
                }
            });
        }

        final Button openPageButton = mainView.findViewById(R.id.open_page_button);

        if (openPageButton != null) {
            openPageButton.setOnClickListener(view -> {
                if (app != null) {
                    app.getAsyncBridge().openPage("471b2ac7-f873-498f-a1b9-a51ba6c1e385");
                }
            });
        }
    }

    public final void setApp(final PlattarEngine app) {
        this.app = app;
    }

    public final void moveToFront() {
        if (mainView != null) {
            mainView.bringToFront();
        }
    }
}
