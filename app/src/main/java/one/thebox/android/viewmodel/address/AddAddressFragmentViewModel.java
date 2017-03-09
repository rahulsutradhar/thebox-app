package one.thebox.android.viewmodel.address;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.RealmList;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.Address;
import one.thebox.android.Models.Locality;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.ViewHelper.MontserratEditText;
import one.thebox.android.ViewHelper.SimpleTextWatcher;
import one.thebox.android.activity.ConfirmTimeSlotActivity;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.api.RequestBodies.AddAddressRequestBody;
import one.thebox.android.api.RequestBodies.UpdateAddressRequestBody;
import one.thebox.android.api.Responses.AddressesApiResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.address.AddAddressFragment;
import one.thebox.android.fragment.address.DeliveryAddressFragment;
import one.thebox.android.util.Constants;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import one.thebox.android.viewmodel.base.BaseViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by developers on 07/03/17.
 */

public class AddAddressFragmentViewModel extends BaseViewModel {

    /**
     * AddressFragment
     */
    private AddAddressFragment addAddressFragment;

    /**
     * Address
     */
    private Address address;

    /**
     * List Orders
     */
    private RealmList<Order> orders;

    /**
     * Addres Type
     */
    private int addressLabel;

    /**
     * View
     */
    private View view;

    private int calledFrom;
    private int type;

    /**
     * TextInputLayout
     */
    private TextInputLayout textInputLayoutFlat;
    private TextInputLayout textInputLayoutSociety;
    private TextInputLayout textInputLayoutStreet;

    /**
     * Radio Group
     */
    private RadioGroup radioGroup;

    /**
     * Constructor
     * Called from MyFragment to Save Address
     */
    public AddAddressFragmentViewModel(AddAddressFragment addAddressFragment, int calledFrom, int type, View view) {
        this.addAddressFragment = addAddressFragment;
        this.calledFrom = calledFrom;
        this.type = type;
        this.view = view;
        this.addressLabel = 0;
        this.address = new Address();
        Locality locality = Constants.POWAI_LOCALITY;
        this.address.setLocality(locality);
    }

    /**
     * Called from Cart to Save Address
     */
    public AddAddressFragmentViewModel(AddAddressFragment addAddressFragment, RealmList<Order> orders, int calledFrom, int type, View view) {
        this.addAddressFragment = addAddressFragment;
        this.orders = orders;
        this.calledFrom = calledFrom;
        this.type = type;
        this.view = view;
        this.addressLabel = 0;
        this.address = new Address();
        Locality locality = Constants.POWAI_LOCALITY;
        this.address.setLocality(locality);
    }

    /**
     * Called when user edit addres
     */
    public AddAddressFragmentViewModel(AddAddressFragment addAddressFragment, Address address, int calledFrom, int type, View view) {
        this.addAddressFragment = addAddressFragment;
        this.calledFrom = calledFrom;
        this.type = type;
        this.view = view;
        this.address = address;
        setAddressLabel(address.getLabel());
        notifyChange();
    }

    public void bindLayout() {

        radioGroup = (RadioGroup) view.findViewById(R.id.radioAddressType);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radioAddressTypeHome:
                        addressLabel = 0;
                        address.setLabel(addressLabel);
                        break;
                    case R.id.radioAddressTypeWork:
                        addressLabel = 1;
                        address.setLabel(addressLabel);
                        break;
                    case R.id.radioAddressTypeOther:
                        addressLabel = 2;
                        address.setLabel(addressLabel);
                        break;
                    default:
                        addressLabel = 0;
                        address.setLabel(addressLabel);
                }

            }
        });


    }

    public TextWatcher getAddressLineFlatWatcher() {
        return new SimpleTextWatcher() {
            @Override
            public void onTextChanged(String text) {
                //get the text
                address.setFlat(text);
            }
        };
    }

    public TextWatcher getAddressLineSocietyWatcher() {
        return new SimpleTextWatcher() {
            @Override
            public void onTextChanged(String text) {
                //get the text
                address.setSociety(text);
            }
        };
    }

    public TextWatcher getAddressLineStreetWatcher() {
        return new SimpleTextWatcher() {
            @Override
            public void onTextChanged(String text) {
                //get the text
                address.setStreet(text);
            }
        };
    }

    /*******************************
     * Validations
     *******************************/

    public void validateInputs() {

        //check addres type Validation
        if (checkAddressTypeValidate()) {
            //check flat number validation
            if (checkFlatNumberValidation()) {
                //check society Validation
                if (checkSocietyValidation()) {
                    //check street validation
                    if (checkStreetValidation()) {
                        //request server to save addres
                        //type == edit address
                        if (type == 2) {
                            updateAddress(address);
                        }
                        //type == 1 save address
                        else {
                            addAddress(address);
                        }

                    }

                }
            }
        }

    }

    public boolean checkAddressTypeValidate() {
        boolean flag = false;
        if (addressLabel >= 0) {
            flag = true;

        } else {
            //invalide input
            flag = false;
        }
        return flag;
    }

    public boolean checkFlatNumberValidation() {
        boolean flag = false;
        textInputLayoutFlat = (TextInputLayout) view.findViewById(R.id.textlayout_flatNumber);

        if (address.getFlat() != null) {
            if (!address.getFlat().isEmpty()) {
                textInputLayoutFlat.setErrorEnabled(false);
                textInputLayoutFlat.setError("");
                flag = true;
            } else {
                textInputLayoutFlat.setErrorEnabled(true);
                textInputLayoutFlat.setError("This field cannot be Empty");
                flag = false;
            }
        } else {
            textInputLayoutFlat.setErrorEnabled(true);
            textInputLayoutFlat.setError("This field cannot be Empty");
            flag = false;
        }
        return flag;
    }

    public boolean checkSocietyValidation() {
        boolean flag = false;
        textInputLayoutSociety = (TextInputLayout) view.findViewById(R.id.textlayout_Society);

        if (address.getSociety() != null) {
            if (!address.getSociety().isEmpty()) {
                textInputLayoutSociety.setErrorEnabled(false);
                textInputLayoutSociety.setError("");
                flag = true;
            } else {
                textInputLayoutSociety.setErrorEnabled(true);
                textInputLayoutSociety.setError("This field cannot be empty");
                flag = false;
            }
        } else {
            textInputLayoutSociety.setErrorEnabled(true);
            textInputLayoutSociety.setError("This field cannot be empty");
            flag = false;
        }
        return flag;
    }

    public boolean checkStreetValidation() {
        boolean flag = false;
        textInputLayoutStreet = (TextInputLayout) view.findViewById(R.id.textlayout_Street);

        if (address.getStreet() != null) {
            if (!address.getStreet().isEmpty()) {
                textInputLayoutStreet.setErrorEnabled(false);
                textInputLayoutStreet.setError("");
                flag = true;
            } else {
                textInputLayoutStreet.setErrorEnabled(true);
                textInputLayoutStreet.setError("This field cannot be empty");
                flag = false;
            }
        } else {
            textInputLayoutStreet.setErrorEnabled(true);
            textInputLayoutStreet.setError("This field cannot be empty");
            flag = false;
        }
        return flag;
    }

    /**
     * Click event
     */
    public void onClickSaveAddress() {
        validateInputs();
    }

    /**
     * Databinding data
     */
    public String getAddressFlat() {
        return address.getFlat();
    }

    public String getAddressSociety() {
        return address.getSociety();
    }

    public String getAddressStreet() {
        return address.getStreet();
    }


    /**
     * Add New address to server
     */
    private void addAddress(final Address address) {
        final BoxLoader dialog = new BoxLoader(addAddressFragment.getActivity()).show();
        TheBox.getAPIService().addAddress(PrefUtils.getToken(addAddressFragment.getActivity()),
                new AddAddressRequestBody(
                        new AddAddressRequestBody.Address(
                                address.getLocality().getCode(),
                                address.getLabel(),
                                address.getFlat(),
                                address.getSociety(),
                                address.getStreet())))
                .enqueue(new Callback<AddressesApiResponse>() {
                    @Override
                    public void onResponse(Call<AddressesApiResponse> call, Response<AddressesApiResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (response.body().isSuccess()) {
                                        updateAddressLocally(response.body().getAddress());
                                        Toast.makeText(addAddressFragment.getActivity(), response.body().getInfo(), Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(addAddressFragment.getActivity(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<AddressesApiResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(addAddressFragment.getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Edit Addres or Update Addres to Server
     */
    public void updateAddress(final Address address) {
        final BoxLoader dialog = new BoxLoader(addAddressFragment.getActivity()).show();
        TheBox.getAPIService().updateAddress(PrefUtils.getToken(addAddressFragment.getActivity()),
                new UpdateAddressRequestBody(
                        new UpdateAddressRequestBody.Address(
                                address.getId()
                                , address.getLocality().getCode(),
                                address.getLabel(),
                                address.getFlat(),
                                address.getSociety(),
                                address.getStreet())))
                .enqueue(new Callback<AddressesApiResponse>() {
                    @Override
                    public void onResponse(Call<AddressesApiResponse> call, Response<AddressesApiResponse> response) {
                        dialog.dismiss();
                        try {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    if (response.body().isSuccess()) {
                                        updateAddressLocally(response.body().getAddress());
                                        Toast.makeText(addAddressFragment.getActivity(), response.body().getInfo(), Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(addAddressFragment.getActivity(), response.body().getInfo(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(Call<AddressesApiResponse> call, Throwable t) {
                        dialog.dismiss();
                    }
                });
    }


    public void updateAddressLocally(Address address) {
        try {
            User user = PrefUtils.getUser(addAddressFragment.getActivity());
            RealmList<Address> addresses = new RealmList<>();
            address.setCurrentAddress(true);
            addresses.add(address);
            user.setAddresses(addresses);
            PrefUtils.saveUser(addAddressFragment.getActivity(), user);

            //MyAccountFragment
            if (calledFrom == 1) {
                Intent intent = new Intent(addAddressFragment.getActivity(), MainActivity.class);
                intent.putExtra("Delivery_Address_Updated", CoreGsonUtils.toJson(address));
                addAddressFragment.getActivity().setResult(2, intent);
                addAddressFragment.getActivity().finish();
            }
            //Cart frament
            else if (calledFrom == 2) {
                //proceed to time slot Activity
                /*addAddressFragment.getActivity().startActivity(ConfirmTimeSlotActivity.newInstance(addAddressFragment.getActivity(),
                        OrderHelper.getAddressAndOrder(orders), false));
                addAddressFragment.getActivity().finish();*/

                //transact to delivery Address Fragment
                transactToDeliveryAddressFragment();


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void transactToDeliveryAddressFragment() {
        DeliveryAddressFragment deliveryAddressFragment = new
                DeliveryAddressFragment(address, orders);
        FragmentManager fragmentManager = addAddressFragment.getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, deliveryAddressFragment, "Delivery_Address");
        fragmentTransaction.commit();

        addAddressFragment.getActivity().setTitle("Delivery Address");
    }

    public int getAddressLabel() {
        return addressLabel;
    }

    public void setAddressLabel(int addressLabel) {
        this.addressLabel = addressLabel;
    }
}
