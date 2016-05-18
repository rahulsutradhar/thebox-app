package one.thebox.android.ViewHelper;

import android.support.design.widget.AppBarLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ajeet Kumar Meena on 18/05/16.
 */
public class AppBarObserver implements AppBarLayout.OnOffsetChangedListener {
    AppBarLayout appBarLayout;
    int oldOffset = 0;
    List<OnOffsetChangeListener> listeners;

    private AppBarObserver(AppBarLayout appBarLayout) {
        this.appBarLayout = appBarLayout;
        appBarLayout.addOnOffsetChangedListener(this);
        listeners = new ArrayList<>(1);
    }

    public static AppBarObserver observe(AppBarLayout appBarLayout) {
        return new AppBarObserver(appBarLayout);
    }

    public void addOffsetChangeListener(OnOffsetChangeListener listener) {
        listeners.add(listener);
    }

    public void removeOffsetChangeListener(OnOffsetChangeListener listener) {
        listeners.remove(listener);
    }

    public void stop() {
        appBarLayout.removeOnOffsetChangedListener(this);
        listeners.clear();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int dOffset = offset - oldOffset;
        for (OnOffsetChangeListener listener : listeners) {
            listener.onOffsetChange(offset, dOffset);
        }
        oldOffset = offset;
    }

    public interface OnOffsetChangeListener {
        void onOffsetChange(int offset, int dOffset);
    }
}

