package one.thebox.android.adapter.orders;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Vector;

import one.thebox.android.Models.order.CalenderYear;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;

/**
 * Created by developers on 12/07/17.
 */

public class SelectYearAdapter extends BaseRecyclerAdapter {

    private Context context;
    private Vector<CalenderYear> calenderYears;
    private String selectedYear;
    private OnYearSelected onYearSelected;


    public SelectYearAdapter(Context context, Vector<CalenderYear> calenderYears, String selectedYear) {
        super(context);
        this.context = context;
        this.calenderYears = calenderYears;
    }

    public void attachListener(OnYearSelected onYearSelected) {
        this.onYearSelected = onYearSelected;
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return new ItemHolderYearNormal(view);
    }

    @Override
    protected ItemHolder getItemHolder(View view, int position) {
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
        if (calenderYears != null) {
            ItemHolderYearNormal itemHolderYearNormal = (ItemHolderYearNormal) holder;
            itemHolderYearNormal.setView(calenderYears.get(position));
        }
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return calenderYears.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.card_normal_year_calender;
    }

    @Override
    protected int getItemLayoutId(int position) {
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

    /**
     * Selected Item
     */
    public class ItemHolderYearSelected extends BaseRecyclerAdapter.ItemHolder {

        private TextView textView;

        public ItemHolderYearSelected(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.yearName);
        }

        public void setView(CalenderYear calenderYear) {
            textView.setText(calenderYear.getName());
        }
    }


    /**
     * Normal Item
     */
    public class ItemHolderYearNormal extends BaseRecyclerAdapter.ItemHolder {

        private TextView textView;

        public ItemHolderYearNormal(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.yearName);
        }

        public void setView(CalenderYear calenderYear) {
            textView.setText(calenderYear.getName());
        }
    }

    /**
     * Interface
     */
    public interface OnYearSelected {
        void onYearSelected(CalenderYear calenderYear);
    }
}
