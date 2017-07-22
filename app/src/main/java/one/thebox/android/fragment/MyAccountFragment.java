package one.thebox.android.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import io.realm.RealmList;
import one.thebox.android.Models.address.Address;
import one.thebox.android.Models.user.User;
import one.thebox.android.R;
import one.thebox.android.activity.address.AddressActivity;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.activity.OrderDetailActivity;
import one.thebox.android.activity.UpdateProfileActivity;
import one.thebox.android.app.Constants;
import one.thebox.android.app.TheBox;
import one.thebox.android.services.AuthenticationService;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;


public class MyAccountFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private TextView showAllAddressesButton;
    private TextView showAllOrdersButton, editAddressButton;
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

        initvariables();
        ((MainActivity) getActivity()).getToolbar().setTitle("My Account");
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_my_account, container, false);
            initViews();
            setupViews();
        }
        return rootView;
    }

    private void initvariables() {
        user = PrefUtils.getUser(getActivity());
    }

    private void initViews() {
        showAllAddressesButton = (TextView) rootView.findViewById(R.id.button_show_all_address);
        showAllOrdersButton = (TextView) rootView.findViewById(R.id.button_show_all_orders);
        editAddressButton = (TextView) rootView.findViewById(R.id.button_edit_address);

        showAllAddressesButton.setOnClickListener(this);
        showAllOrdersButton.setOnClickListener(this);
        editAddressButton.setOnClickListener(this);

        userName = (TextView) rootView.findViewById(R.id.user_name_text_view);
        email = (TextView) rootView.findViewById(R.id.email_text_view);
        phoneNumber = (TextView) rootView.findViewById(R.id.phone_text_view);
        address = (TextView) rootView.findViewById(R.id.address_text_view);
        lastOrder = (TextView) rootView.findViewById(R.id.last_order_text_view);
        signOut = (TextView) rootView.findViewById(R.id.button_sign_out);
        signOut.setOnClickListener(this);
    }

    private void setupViews() {

        try {
            userName.setText(user.getName());
            email.setText(user.getEmail());
            phoneNumber.setText(user.getPhoneNumber());

            if (user.getAddresses() == null || user.getAddresses().isEmpty()) {
                editAddressButton.setVisibility(View.GONE);
                showAllAddressesButton.setVisibility(View.VISIBLE);
                address.setText("You have no address added");

                showAllAddressesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //open add address fragment blank
                        Intent intent = new Intent(getActivity(), AddressActivity.class);
                        /**
                         * 1- My Account Fragment
                         * 2- CartProduct Fargment
                         */
                        intent.putExtra("called_from", 1);
                        /**
                         * 1- add address
                         * 2- edit address
                         */
                        intent.putExtra(Constants.EXTRA_ADDRESS_TYPE, 1);
                        startActivityForResult(intent, 2);
                    }
                });
            } else {
                editAddressButton.setVisibility(View.VISIBLE);
                showAllAddressesButton.setVisibility(View.GONE);
                clickEditAddress();
                if (user.getAddresses().size() > 0) {
                    Address deliveryAddress = user.getAddresses().first();
                    address.setText(deliveryAddress.getFlat() + ",\n" +
                            deliveryAddress.getSociety() + ",\n" +
                            deliveryAddress.getStreet());
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clickEditAddress() {

        editAddressButton.setOnClickListener(this);
        editAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //open add address fragment blank
                Intent intent = new Intent(getActivity(), AddressActivity.class);
                intent.putExtra("called_from", 1);
                /**
                 * 1- add address
                 * 2- edit address
                 */
                intent.putExtra(Constants.EXTRA_ADDRESS_TYPE, 2);
                intent.putExtra("edit_delivery_address", CoreGsonUtils.toJson(fetchAddress()));
                startActivityForResult(intent, 2);
            }
        });
    }

    public Address fetchAddress() {
        RealmList<Address> addresses = PrefUtils.getUser(getActivity()).getAddresses();
        Address editAddress = null;

        if (addresses != null) {
            if (addresses.size() > 0) {
                editAddress = addresses.first();
            }
        }
        return editAddress;
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
            case R.id.button_show_all_orders: {
                startActivity(new Intent(getActivity(), OrderDetailActivity.class));
                break;
            }
            case R.id.button_sign_out: {
                new AuthenticationService().logOut(getContext(), true);
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setSubtitle(null);
        ((MainActivity) getActivity()).getSearchViewHolder().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getButtonSearch().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setVisibility(View.GONE);
        ((MainActivity) getActivity()).getButtonSpecialAction().setImageResource(R.drawable.ic_edit);
        ((MainActivity) getActivity()).getButtonSpecialAction().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
                startActivityForResult(intent, 3);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            /**
             * 2- Add or edit Address
             *
             * 3 - Update User Profile
             */
            if (requestCode == 2) {
                if (data.getExtras() != null) {
                    Address deliveryAddress = CoreGsonUtils.fromJson(data.getStringExtra("Delivery_Address_Updated"), Address.class);
                    address.setText(deliveryAddress.getFlat() + ",\n" + deliveryAddress.getSociety() + ",\n" + deliveryAddress.getStreet());

                    //this show all button turns to edit address
                    editAddressButton.setVisibility(View.VISIBLE);
                    showAllAddressesButton.setVisibility(View.GONE);
                    clickEditAddress();
                }
            } else if (requestCode == 3) {
                if (data.getExtras() != null) {
                    user = CoreGsonUtils.fromJson(data.getStringExtra("UserProfile"), User.class);
                    initViews();
                    setupViews();
                }
            }

        } catch (Exception e) {

        }

    }

    /**
     * Save Clever Tap Event
     */
    public void setCleverTapEventLogout(User user) {
        try {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Phone", user.getPhoneNumber());
            hashMap.put("User_uuid", user.getUuid());
            hashMap.put("Unique_id", user.getUuid());
            hashMap.put("Email", user.getEmail());
            hashMap.put("Name", user.getName());

            TheBox.getCleverTap().event.push("Logout", hashMap);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
