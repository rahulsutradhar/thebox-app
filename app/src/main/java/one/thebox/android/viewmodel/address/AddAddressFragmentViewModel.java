package one.thebox.android.viewmodel.address;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.RealmList;
import one.thebox.android.Models.address.Address;
import one.thebox.android.Models.address.Locality;
import one.thebox.android.Models.Order;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.BoxLoader;
import one.thebox.android.ViewHelper.SimpleTextWatcher;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.activity.address.AddressActivity;
import one.thebox.android.adapter.address.LocalitySpinnerAdapter;
import one.thebox.android.api.RequestBodies.AddAddressRequestBody;
import one.thebox.android.api.RequestBodies.UpdateAddressRequestBody;
import one.thebox.android.api.Responses.AddressesApiResponse;
import one.thebox.android.api.Responses.LocalityResponse;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.address.AddAddressFragment;
import one.thebox.android.fragment.address.DeliveryAddressFragment;
import one.thebox.android.util.Constants;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.CustomSnackBar;
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
    private TextView localityErrorMessage;

    /**
     * Radio Group
     */
    private RadioGroup radioGroup;

    /**
     * Locality
     */
    private ArrayList<Locality> localities;
    private int localityId = -1;
    private LocalitySpinnerAdapter spinnerAdapter;
    private Spinner spinner;
    private int localityFetchCount = 0;


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
        this.localities = new ArrayList<>();
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
        this.localities = new ArrayList<>();
    }

    /**
     * Called when user edit addres
     * type = 2
     */
    public AddAddressFragmentViewModel(AddAddressFragment addAddressFragment, Address address, int calledFrom, int type, View view) {
        this.addAddressFragment = addAddressFragment;
        this.calledFrom = calledFrom;
        this.type = type;
        this.view = view;
        this.address = address;

        setAddressLabel(address.getLabel());
        this.localities = new ArrayList<>();
        notifyChange();
    }

    /**
     * Called from Delivery Address Fragment
     * Edit Delivery Address
     * calledFrom = 3; type = 3;
     */
    public AddAddressFragmentViewModel(AddAddressFragment addAddressFragment,
                                       Address address, RealmList<Order> orders, int calledFrom, int type, View view) {
        this.addAddressFragment = addAddressFragment;
        this.address = address;
        this.orders = orders;
        this.calledFrom = calledFrom;
        this.type = type;
        this.view = view;

        setAddressLabel(address.getLabel());
        this.localities = new ArrayList<>();
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
        localityErrorMessage = (TextView) view.findViewById(R.id.error_message_locality);
        spinner = (Spinner) view.findViewById(R.id.spinnerLocality);

        //fetch from server
        fetchAllLocality();

    }

    public void setUpSpinner() {
        try {
            if (localities.size() > 0) {
                spinnerAdapter = new LocalitySpinnerAdapter(addAddressFragment.getActivity(), localities);
                spinner.setAdapter(spinnerAdapter);

                //check if address is editting
                if (type == 2 || type == 3) {
                    for (int i = 0; i < localities.size(); i++) {
                        Locality locality = localities.get(i);
                        if (locality.getId() == address.getLocalityId()) {
                            localityId = locality.getId();
                            spinner.setSelection(i);
                            break;
                        }
                    }
                }
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        localityId = localities.get(position).getId();
                        spinner.setSelection(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
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
                        //check if user has entered localityId
                        if (checkPinCodeSelected()) {
                            //request server to save addres
                            //type == 2,3 edit address
                            if (type == 2 || type == 3) {
                                updateAddress(address);
                            }
                            //type == 1 save address
                            else if (type == 1) {
                                addAddress(address);
                            }
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

    public boolean checkPinCodeSelected() {
        boolean flag = false;
        if (localityId == -1) {
            flag = false;
            localityErrorMessage.setVisibility(View.VISIBLE);
            localityErrorMessage.setText("Please select your Locality");
        } else {
            localityErrorMessage.setText("");
            localityErrorMessage.setVisibility(View.GONE);
            flag = true;
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
     * Fetch All Locality
     */
    public void fetchAllLocality() {
        localityFetchCount++;
        TheBox.getAPIService().getLocality()
                .enqueue(new Callback<LocalityResponse>() {
                    @Override
                    public void onResponse(Call<LocalityResponse> call, Response<LocalityResponse> response) {

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                localities.addAll(response.body().getLocalities());
                                setUpSpinner();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LocalityResponse> call, Throwable t) {
                        CustomSnackBar.showLongSnackBar(view, "Please check your Internet connection");
                        if (localityFetchCount < 2) {
                            fetchAllLocality();
                        }
                    }
                });

    }

    /**
     * Add New address to server
     */
    private void addAddress(final Address address) {
        final BoxLoader dialog = new BoxLoader(addAddressFragment.getActivity()).show();
        TheBox.getAPIService().addAddress(PrefUtils.getToken(addAddressFragment.getActivity()),
                new AddAddressRequestBody(
                        new AddAddressRequestBody.Address(
                                localityId,
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
                            Toast.makeText(addAddressFragment.getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AddressesApiResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(addAddressFragment.getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
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
                                address.getId(),
                                localityId,
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
                            Toast.makeText(addAddressFragment.getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<AddressesApiResponse> call, Throwable t) {
                        dialog.dismiss();
                        Toast.makeText(addAddressFragment.getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
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

                //Clevertap Event for Saved Address After Proceed from Cart
                setCleverTapEventSaveAddress(address);

                //transact to delivery Address Fragment
                transactToDeliveryAddressFragment(address);
            } else if (calledFrom == 3) {
                //clear back stack
                clearBackStack();
                transactToDeliveryAddressFragment(address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void transactToDeliveryAddressFragment(Address address) {
        DeliveryAddressFragment deliveryAddressFragment = new
                DeliveryAddressFragment(address, orders);
        FragmentManager fragmentManager = addAddressFragment.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, deliveryAddressFragment, "Delivery_Address");
        fragmentTransaction.commit();

        ((AddressActivity) addAddressFragment.getActivity()).getToolbar().setTitle("Delivery Address");
    }

    public int getAddressLabel() {
        return addressLabel;
    }

    public void setAddressLabel(int addressLabel) {
        this.addressLabel = addressLabel;
    }

    private void clearBackStack() {
        for (int i = 0; i < addAddressFragment.getActivity().getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            addAddressFragment.getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    /**
     *
     */
    public void setCleverTapEventSaveAddress(Address address) {
        HashMap<String, Object> saveAddress = new HashMap<>();
        saveAddress.put("delivery_address", address);

        TheBox.getCleverTap().event.push("save_address", saveAddress);

    }
}
