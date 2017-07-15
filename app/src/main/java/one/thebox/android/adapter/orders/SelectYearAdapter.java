package one.thebox.android.adapter.orders;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Vector;

import one.thebox.android.Models.order.CalenderYear;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;
import one.thebox.android.util.DisplayUtil;

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
        this.selectedYear = selectedYear;
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
    public void onBindViewItemHolder(ItemHolder holder, final int position) {
        if (calenderYears != null) {
            ItemHolderYearNormal itemHolderYearNormal = (ItemHolderYearNormal) holder;
            itemHolderYearNormal.setView(calenderYears.get(position));
            ((ItemHolderYearNormal) holder).layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onYearSelected.onYearSelected(calenderYears.get(position));
                }
            });
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

    public class ItemHolderYearNormal extends BaseRecyclerAdapter.ItemHolder {

        private TextView textView;
        private RelativeLayout layout;

        public ItemHolderYearNormal(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.yearName);
            layout = (RelativeLayout) itemView.findViewById(R.id.holder);
        }

        public void setView(CalenderYear calenderYear) {
            textView.setText(calenderYear.getName());

            if (calenderYear.getName().equalsIgnoreCase(selectedYear)) {
                //selected
                textView.setTextColor(context.getResources().getColor(R.color.black));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);


                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                relativeParams.setMargins(15, 0, 15, 0);
                layout.setLayoutParams(relativeParams);

                layout.setBackgroundResource(R.drawable.tab_layout_selected);
                layout.setPadding(DisplayUtil.dpToPx(context, 6), DisplayUtil.dpToPx(context, 6), DisplayUtil.dpToPx(context, 6), DisplayUtil.dpToPx(context, 6));
                layout.requestLayout();
            } else {
                textView.setTextColor(context.getResources().getColor(R.color.md_grey_800));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);


                RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                relativeParams.setMargins(15, 20, 15, 0);
                layout.setLayoutParams(relativeParams);
                layout.setBackgroundResource(R.drawable.tab_layout);
                layout.setPadding(DisplayUtil.dpToPx(context, 2), DisplayUtil.dpToPx(context, 2), DisplayUtil.dpToPx(context, 2), DisplayUtil.dpToPx(context, 2));
                layout.requestLayout();
            }
        }
    }

    /**
     * Interface
     */
    public interface OnYearSelected {
        void onYearSelected(CalenderYear calenderYear);
    }
}
