package one.thebox.android.adapter.reschedule;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.reschedule.RescheduleReason;
import one.thebox.android.R;
import one.thebox.android.adapter.base.BaseRecyclerAdapter;

/**
 * Created by developers on 05/06/17.
 */

public class RescheduleReasonAdapter extends BaseRecyclerAdapter {

    private Context context;
    private ArrayList<RescheduleReason> rescheduleReasons = new ArrayList<>();
    private int currentSelection = 0;
    private OnRescheduleReasonActionCompleted onRescheduleReasonActionCompleted;

    public RescheduleReasonAdapter(Context context) {
        super(context);
        this.context = context;
    }

    public ArrayList<RescheduleReason> getRescheduleReasons() {
        return rescheduleReasons;
    }

    public void setRescheduleReasons(ArrayList<RescheduleReason> rescheduleReasons) {
        this.rescheduleReasons = rescheduleReasons;
        notifyDataSetChanged();
    }

    @Override
    protected ItemHolder getItemHolder(View view) {
        return new ItemViewHolder(view);
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
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        itemViewHolder.setView(rescheduleReasons.get(position), position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int temp = currentSelection;
                currentSelection = position;
                notifyItemChanged(currentSelection);
                notifyItemChanged(temp);

                onRescheduleReasonActionCompleted.onRescheduleReason(rescheduleReasons.get(currentSelection));
            }
        });
    }

    @Override
    public void onBindViewHeaderHolder(HeaderHolder holder, int position) {

    }

    @Override
    public void onBindViewFooterHolder(FooterHolder holder, int position) {

    }

    @Override
    public int getItemsCount() {
        return rescheduleReasons.size();
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_reschedule_reason;
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


    private class ItemViewHolder extends BaseRecyclerAdapter.ItemHolder {
        private TextView reasonText;
        private RadioButton radioButton;

        public ItemViewHolder(View itemView) {
            super(itemView);
            reasonText = (TextView) itemView.findViewById(R.id.text_reason);
            radioButton = (RadioButton) itemView.findViewById(R.id.radio_button);
        }

        public void setView(RescheduleReason rescheduleReason, int position) {
            reasonText.setText(rescheduleReason.getName());
            radioButton.setClickable(false);
            if (getAdapterPosition() == currentSelection) {
                radioButton.setChecked(true);
            } else {
                radioButton.setChecked(false);
            }
        }
    }

    public void attachListener(OnRescheduleReasonActionCompleted onRescheduleReasonActionCompleted) {
        this.onRescheduleReasonActionCompleted = onRescheduleReasonActionCompleted;
    }


    public interface OnRescheduleReasonActionCompleted {
        void onRescheduleReason(RescheduleReason rescheduleReason);
    }

}
