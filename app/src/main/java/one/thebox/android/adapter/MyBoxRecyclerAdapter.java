package one.thebox.android.adapter;

import android.content.Context;
import android.view.View;

/**
 * Created by Ajeet Kumar Meena on 11-04-2016.
 */
public class MyBoxRecyclerAdapter extends BaseRecyclerAdapter {

    public MyBoxRecyclerAdapter(Context context) {
        super(context);
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return null;
    }

    @Override
    protected HeaderHolder getHeaderHolder(View view) {
        return null;
    }

    @Override
    protected FooterHolder getFooterHolder(View view) {
        return null;
    }

    @Override
    public void onBindViewItemHolder(ItemHolder holder, int position) {

    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return 0;
    }

    @Override
    protected int getItemLayoutId() {
        return 0;
    }

    @Override
    protected int getHeaderLayoutId() {
        return 0;
    }

    @Override
    protected int getFooterLayoutId() {
        return 0;
    }

    public static class SmartItemAdapter extends BaseRecyclerAdapter {

        public SmartItemAdapter(Context context) {
            super(context);
        }

        @Override
        protected ItemHolder getItemHolder(View view) {
            return null;
        }

        @Override
        protected HeaderHolder getHeaderHolder(View view) {
            return null;
        }

        @Override
        protected FooterHolder getFooterHolder(View view) {
            return null;
        }

        @Override
        public void onBindViewItemHolder(ItemHolder holder, int position) {

        }

        @Override
        public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

        }

        @Override
        public void onBindViewFooterHolder(FooterHolder holder, int position) {

        }

        @Override
        public int getItemsCount() {
            return 0;
        }

        @Override
        protected int getItemLayoutId() {
            return 0;
        }

        @Override
        protected int getHeaderLayoutId() {
            return 0;
        }

        @Override
        protected int getFooterLayoutId() {
            return 0;
        }
    }

    public static class ExpandedListAdapter extends BaseRecyclerAdapter {

        public ExpandedListAdapter(Context context) {
            super(context);
        }

        @Override
        protected ItemHolder getItemHolder(View view) {
            return null;
        }

        @Override
        protected HeaderHolder getHeaderHolder(View view) {
            return null;
        }

        @Override
        protected FooterHolder getFooterHolder(View view) {
            return null;
        }

        @Override
        public void onBindViewItemHolder(ItemHolder holder, int position) {

        }

        @Override
        public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

        }

        @Override
        public void onBindViewFooterHolder(FooterHolder holder, int position) {

        }

        @Override
        public int getItemsCount() {
            return 0;
        }

        @Override
        protected int getItemLayoutId() {
            return 0;
        }

        @Override
        protected int getHeaderLayoutId() {
            return 0;
        }

        @Override
        protected int getFooterLayoutId() {
            return 0;
        }
    }
}
