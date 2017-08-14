package com.aliya.player.ui;

import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import com.aliya.player.R;
import com.aliya.player.utils.Utils;

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
        boolean oldVisible = isVisible();
        if (isVisible) {
            show();
        } else {
            hide();
        }
        if (oldVisible != isVisible) {
            if (visibilityListener != null) {
                visibilityListener.onVisibilityChange(this, isVisible);
            }
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
                            if (Utils.isAvailable(view.getContext())) {
                                setVisibility(false);
                                // TODO 待完善
                            } else {
                                Toast.makeText(view.getContext(), "网络不可用", Toast.LENGTH_SHORT)
                                        .show();
                            }
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
