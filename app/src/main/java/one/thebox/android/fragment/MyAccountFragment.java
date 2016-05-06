package one.thebox.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AddressBottomSheet;
import one.thebox.android.activity.AddressesActivity;
import one.thebox.android.activity.OrderDetailActivity;
import one.thebox.android.activity.SplashActivity;
import one.thebox.android.api.ApiResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MyAccountFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private TextView showAllAddressesButton;
    private TextView showAllOrdersButton;
    private TextView userName, email, phoneNumber, address, lastOrder, signOut;
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
            for (int i = 0; i < user.getAddresses().size(); i++) {
                if (user.getAddresses().get(i).isCurrentAddress()) {
                    address.setText(user.getAddresses().get(i).getFlat() + ", " +
                            user.getAddresses().get(i).getStreet());
                }
            }
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
        signOut = (TextView) rootView.findViewById(R.id.button_sign_out);
        signOut.setOnClickListener(this);
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
            case R.id.button_sign_out: {
                final MaterialDialog dialog = new MaterialDialog.Builder(getActivity()).progressIndeterminateStyle(true).progress(true, 0).show();
                MyApplication.getAPIService().signOut(PrefUtils.getToken(getActivity()))
                        .enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                dialog.dismiss();
                                PrefUtils.removeAll(getActivity());
                                startActivity(new Intent(getActivity(), SplashActivity.class));
                                getActivity().finish();
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                dialog.dismiss();
                            }
                        });
            }
        }
    }

    private void openAddAddressBottomSheet() {
        new AddressBottomSheet(getActivity(), new AddressBottomSheet.OnAddressAdded() {
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
