package one.thebox.android.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import one.thebox.android.R;
import one.thebox.android.app.Constants;

/**
 * Created by Ruchit on 9/28/2016.
 */
public class PaymentSelectorFragment extends Fragment {

    private View rootView;
    public static final String EXTRA_PAYMENT_TYPE = "payment_type";

    private Unbinder unbinder;

    @BindView(R.id.imgPaymentOption)
    ImageView imgPaymentOption;
    @BindView(R.id.txtPaymentMessage)
    TextView txtPaymentMessage;


    public PaymentSelectorFragment() {
    }

    public static PaymentSelectorFragment getInstance(String paymentType, String paymentAmount) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_PAYMENT_TYPE, paymentType);
        bundle.putString(Constants.EXTRA_AMOUNT_TO_PAY, paymentAmount);
        PaymentSelectorFragment paymentSelectorFragment = new PaymentSelectorFragment();
        paymentSelectorFragment.setArguments(bundle);
        return paymentSelectorFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_pay_by_card, container, false);
            unbinder = ButterKnife.bind(this, rootView);

        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments().getString(EXTRA_PAYMENT_TYPE).contentEquals("CARD")) {
            imgPaymentOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_card));
            txtPaymentMessage.setText("Please aunthenticate payment for " + Constants.RUPEE_SYMBOL + " " + fmt(Double.parseDouble(getArguments().getString(Constants.EXTRA_AMOUNT_TO_PAY))) + ".");
        } else if (getArguments().getString(EXTRA_PAYMENT_TYPE).contentEquals("CASH")) {
            imgPaymentOption.setImageDrawable(getResources().getDrawable(R.drawable.ic_cash));
            txtPaymentMessage.setText("Please pay " + Constants.RUPEE_SYMBOL + " " + fmt(Double.parseDouble(getArguments().getString(Constants.EXTRA_AMOUNT_TO_PAY))) + " to delivery executive after delivery.");
        }


    }

    public static String fmt(double d) {
        if (d == (long) d)
            return String.format("%d", (long) d);
        else
            return String.format("%s", d);
    }
}
