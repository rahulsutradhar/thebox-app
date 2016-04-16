package one.thebox.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import one.thebox.android.Models.BillItem;
import one.thebox.android.R;
import one.thebox.android.activity.ConfirmDetailsActivity;
import one.thebox.android.adapter.BillsItemAdapter;


public class BillsFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private RecyclerView recyclerView;
    private BillsItemAdapter billsItemAdapter;
    private LinearLayout buttonAddressLinear;
    private TextView buttonSelectAndPay;

    public BillsFragment() {
    }


    public static BillsFragment newInstance(String param1, String param2) {
        BillsFragment fragment = new BillsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bills, container, false);
        initViews();
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {
        billsItemAdapter = new BillsItemAdapter(getActivity());
        for (int i = 0; i < 10; i++) {
            ArrayList<BillItem.SubBillItem> subBillItems = new ArrayList<>();
            for (int j = 0; j < 5; j++) {
                subBillItems.add(new BillItem.SubBillItem());
            }
            billsItemAdapter.addBillItem(new BillItem(subBillItems));
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(billsItemAdapter);
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        buttonAddressLinear = (LinearLayout) rootView.findViewById(R.id.button_address);
        buttonSelectAndPay = (TextView) rootView.findViewById(R.id.button_select_and_pay);
        buttonAddressLinear.setOnClickListener(this);
        buttonSelectAndPay.setOnClickListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.button_address: {
                if (!billsItemAdapter.isAnyItemSelected()) {
                    Toast.makeText(getActivity(), "Select At Least One Option", Toast.LENGTH_SHORT).show();
                    return;
                }
                openConfirmDetailActivity();
                break;
            }
            case R.id.button_select_and_pay: {
                if (!billsItemAdapter.isAnyItemSelected()) {
                    Toast.makeText(getActivity(), "Select At Least One Option", Toast.LENGTH_SHORT).show();
                    return;
                }
                openConfirmDetailActivity();
                break;
            }
        }
    }

    private void openConfirmDetailActivity() {
        startActivity(new Intent(getActivity(), ConfirmDetailsActivity.class));
    }

    public void setButtonState(boolean isSelectAndPay) {
        if (isSelectAndPay) {
            buttonSelectAndPay.setVisibility(View.VISIBLE);
            buttonAddressLinear.setVisibility(View.GONE);
        } else {
            buttonSelectAndPay.setVisibility(View.GONE);
            buttonAddressLinear.setVisibility(View.VISIBLE);
        }
    }
}
