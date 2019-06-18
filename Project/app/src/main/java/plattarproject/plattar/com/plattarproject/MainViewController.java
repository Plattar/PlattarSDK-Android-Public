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

            final Button openAppButton = mainView.findViewById(R.id.open_app_button);
            final Button openSceneButton = mainView.findViewById(R.id.open_scene_button);
            final Button openPageButton = mainView.findViewById(R.id.open_page_button);

            if (openAppButton != null) {
                openAppButton.setOnClickListener(view -> {

                });
            }

            if (openSceneButton != null) {
                openSceneButton.setOnClickListener(view -> {

                });
            }

            if (openPageButton != null) {
                openPageButton.setOnClickListener(view -> {

                });
            }
        }
        else {
            mainView = null;
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
