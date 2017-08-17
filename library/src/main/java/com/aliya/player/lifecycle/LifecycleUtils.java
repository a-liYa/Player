package com.aliya.player.lifecycle;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;

import com.aliya.player.R;

/**
 * LifecycleUtils
 *
 * @author a_liYa
 * @date 2017/8/16 20:55.
 */
public class LifecycleUtils {

    public static final String FRAGMENT_TAG = "video.manager";

    public static void addVideoLifecycle(View playerView, LifecycleListener listener) {
        if (playerView == null || playerView.getParent() == null) return;

        ViewGroup parent = (ViewGroup) playerView.getParent();
        if (parent == null) return;

        Object fmObject = getFragmentManager(parent);

        android.app.FragmentManager fm = null;
        FragmentManager v4fm = null;

        if (fmObject == null) {
            if (parent.getContext() instanceof Activity) {
                fm = ((Activity) parent.getContext()).getFragmentManager();
            }
        } else if (fmObject instanceof android.app.FragmentManager) {
            fm = (android.app.FragmentManager) fmObject;
        } else if (fmObject instanceof FragmentManager) { // supportFragment
            v4fm = (FragmentManager) fmObject;
        }

        if (fm != null) {
            LifecycleFragment current = (LifecycleFragment) fm.findFragmentByTag
                    (FRAGMENT_TAG);
            if (current == null || current.isAsyncRemove()) {
                current = new LifecycleFragment();
                fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
            }
            current.setLifecycleListener(listener);
        } else if (v4fm != null) {
            LifecycleV4Fragment current = (LifecycleV4Fragment)
                    v4fm.findFragmentByTag(FRAGMENT_TAG);
            if (current == null || current.isAsyncRemove()) {
                current = new LifecycleV4Fragment();
                v4fm.beginTransaction().add(current, FRAGMENT_TAG).commitAllowingStateLoss();
            }
            current.setLifecycleListener(listener);
        }

    }

    public static void removeVideoLifecycle(View playerView, LifecycleListener listener) {
        if (playerView == null || playerView.getParent() == null) return;

        ViewGroup parent = (ViewGroup) playerView.getParent();
        Object fmObject = getFragmentManager(parent);

        android.app.FragmentManager fm = null;
        FragmentManager v4fm = null;

        if (fmObject == null) {
            if (parent.getContext() instanceof Activity) {
                fm = ((Activity) parent.getContext()).getFragmentManager();
            }
        } else if (fmObject instanceof android.app.FragmentManager) {
            fm = (android.app.FragmentManager) fmObject;
        } else if (fmObject instanceof FragmentManager) { // support
            v4fm = (FragmentManager) fmObject;
        }

        if (fm != null) {
            LifecycleFragment current = (LifecycleFragment) fm
                    .findFragmentByTag(FRAGMENT_TAG);
            if (current != null && current.getLifecycleListener() == listener) {
                fm.beginTransaction().remove(current).commitAllowingStateLoss();
                current.tagAsyncRemove();
            }
        } else if (v4fm != null) {
            LifecycleV4Fragment current = (LifecycleV4Fragment)
                    v4fm.findFragmentByTag(FRAGMENT_TAG);
            if (current != null && current.getLifecycleListener() == listener) {
                v4fm.beginTransaction().remove(current).commitAllowingStateLoss();
                current.tagAsyncRemove();
            }
        }

    }

    private static Object getFragmentManager(ViewGroup parent) {
        if (parent == null) {
            return null;
        }

        while (true) {
            if (parent.getTag(R.id.player_tag_fragment) instanceof android.app.Fragment) {
                android.app.Fragment fragment = (android.app.Fragment) parent.getTag(R.id
                        .player_tag_fragment);
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    return fragment.getChildFragmentManager();
                } else {
                    return null;
                }
            } else if (parent.getTag(R.id.player_tag_fragment) instanceof Fragment) {
                Fragment supportFragment = (Fragment) parent.getTag(R.id.player_tag_fragment);
                return supportFragment.getChildFragmentManager();
            }

            if (parent.getParent() instanceof ViewGroup) {
                parent = (ViewGroup) parent.getParent();
            } else {
                break;
            }
        }

        return null;
    }

}
