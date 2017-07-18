package one.thebox.android.activity.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import java.util.Calendar;

import one.thebox.android.R;
import one.thebox.android.activity.BaseActivity;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.order.OrderCalenderFragment;

/**
 * Created by developers on 09/07/17.
 */

public class OrderCalenderActivity extends BaseActivity {

    private int currentMonth, currentYear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_calender);

        currentYear = getIntent().getIntExtra(Constants.EXTRA_CALENDER_SELECTED_YEAR, Calendar.getInstance().get(Calendar.YEAR));
        currentMonth = getIntent().getIntExtra(Constants.EXTRA_CALENDER_SELECTED_MONTH, Calendar.getInstance().get(Calendar.MONTH));


        transactToFragment();
    }

    public void transactToFragment() {
        OrderCalenderFragment fragment = new OrderCalenderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.EXTRA_CALENDER_SELECTED_YEAR, currentYear);
        bundle.putInt(Constants.EXTRA_CALENDER_SELECTED_MONTH, currentMonth);
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, "Calender_Fragment");
        fragmentTransaction.commit();
    }
}
