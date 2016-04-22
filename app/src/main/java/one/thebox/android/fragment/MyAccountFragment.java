package one.thebox.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import one.thebox.android.Models.Address;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AddEditAddressViewHelper;
import one.thebox.android.activity.AddressesActivity;
import one.thebox.android.activity.OrderDetailActivity;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;


public class MyAccountFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private TextView showAllAddressesButton;
    private TextView showAllOrdersButton;
    private TextView userName, email, phoneNumber, address, lastOrder;
    private User user;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
        initViews();
        setupViews();
        return rootView;
    }

    private void setupViews() {
        user = PrefUtils.getUser(getActivity());
        userName.setText(user.getName());
        email.setText(user.getEmail());
        phoneNumber.setText(user.getPhoneNumber());
        if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
            address.setText("You have no address added");
            showAllAddressesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openAddAddressBottomSheet();
                }
            });
            showAllAddressesButton.setText("Add Address");
        } else {
            showAllAddressesButton.setText("Show all");
            showAllAddressesButton.setOnClickListener(this);
            address.setText(user.getAddresses().get(0).getFlat() + "," +
                    user.getAddresses().get(0).getStreet());
        }
    }


    private void initViews() {
        showAllAddressesButton = (TextView) rootView.findViewById(R.id.button_show_all_address);
        showAllOrdersButton = (TextView) rootView.findViewById(R.id.button_show_all_orders);
        showAllAddressesButton.setOnClickListener(this);
        showAllOrdersButton.setOnClickListener(this);
        userName = (TextView) rootView.findViewById(R.id.user_name_text_view);
        email = (TextView) rootView.findViewById(R.id.email_text_view);
        phoneNumber = (TextView) rootView.findViewById(R.id.phone_text_view);
        address = (TextView) rootView.findViewById(R.id.address_text_view);
        lastOrder = (TextView) rootView.findViewById(R.id.last_order_text_view);
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
            case R.id.button_show_all_address: {
                startActivity(new Intent(getActivity(), AddressesActivity.class));
                break;
            }
            case R.id.button_show_all_orders: {
                startActivity(new Intent(getActivity(), OrderDetailActivity.class));
                break;
            }
        }
    }

    private void openAddAddressBottomSheet() {
        new AddEditAddressViewHelper(getActivity(), new AddEditAddressViewHelper.OnAddressAdded() {
            @Override
            public void onAddressAdded(User.Address address) {
                User user = PrefUtils.getUser(getActivity());
                ArrayList<User.Address> addresses = user.getAddresses();
                if (addresses == null || addresses.isEmpty()) {
                    addresses = new ArrayList<>();
                }
                addresses.add(address);
                user.setAddresses(addresses);
                PrefUtils.saveUser(getActivity(), user);
                setupViews();
            }
        }).show();
    }


}
