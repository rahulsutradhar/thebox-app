package one.thebox.android.activity.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import one.thebox.android.R;
import one.thebox.android.activity.BaseActivity;
import one.thebox.android.fragment.order.OrderCalenderFragment;

/**
 * Created by developers on 09/07/17.
 */

public class OrderCalenderActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_calender);

        transactToFragment();
    }

    public void transactToFragment() {
        OrderCalenderFragment fragment = new OrderCalenderFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, "Calender_Fragment").addToBackStack("Calender_Fragment");
        fragmentTransaction.commit();
    }
}
