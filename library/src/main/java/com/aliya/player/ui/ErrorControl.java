package com.aliya.player.ui;

import android.util.Log;
import android.view.View;
import android.view.ViewStub;

import com.aliya.player.R;

/**
 * ErrorControl
 *
 * @author a_liYa
 * @date 2017/8/13 11:19.
 */
public class ErrorControl extends AbsControl {

    private ViewStub viewStub;
    private View rootView;

    public ErrorControl(Controller controller) {
        super(controller);
    }

    @Override
    public void onViewCreate(View view) {
        if (view instanceof ViewStub) {
            viewStub = (ViewStub) view;
            viewStub.setLayoutResource(R.layout.module_player_layout_error);
        }
    }

    @Override
    public boolean isVisible() {
        return rootView != null && rootView.getVisibility() == View.VISIBLE ? true : false;
    }

    @Override
    public void setVisibility(boolean isVisible) {
        if (isVisible) {
            show();
        } else {
            hide();
        }
    }

    private void show() {
        if (rootView == null) {
            if (viewStub != null) {
                rootView = viewStub.inflate();
                viewStub = null;

                View view = rootView.findViewById(R.id.player_click_retry);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (controller != null) {
                            Log.e("TAG", "重新播放");
                            hide();
                        }
                    }
                });
            }
        }

        if (rootView != null) {
            rootView.setVisibility(View.VISIBLE);
        }
    }

    private void hide() {
        if (rootView != null) {
            rootView.setVisibility(View.GONE);
        }
    }

}
