package one.thebox.android.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import one.thebox.android.Models.BillItem;
import one.thebox.android.R;
import one.thebox.android.adapter.ConfirmDetailAdapter;
import one.thebox.android.adapter.PaymentDetailAdapter;

public class ConfirmDetailsActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private PaymentDetailAdapter paymentDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_details);
        initViews();
        setupRecyclerAdapter();
    }

    private void setupRecyclerAdapter() {
        paymentDetailAdapter = new PaymentDetailAdapter(this);
        for(int i=0; i<10; i++) {
            paymentDetailAdapter.addSubBillItem(new BillItem.SubBillItem());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        recyclerView.setAdapter(paymentDetailAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }

    @Override
    void onClick(int id) {

    }
}
