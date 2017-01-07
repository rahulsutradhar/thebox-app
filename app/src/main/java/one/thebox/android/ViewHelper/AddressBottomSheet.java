package one.thebox.android.ViewHelper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import one.thebox.android.Models.Address;
import one.thebox.android.Models.Locality;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.activity.BaseActivity;
import one.thebox.android.api.RequestBodies.AddAddressRequestBody;
import one.thebox.android.api.RequestBodies.UpdateAddressRequestBody;
import one.thebox.android.api.Responses.AddressesApiResponse;
import one.thebox.android.api.Responses.LocalitiesResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.Constants;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ajeet Kumar Meena on 22-04-2016.
 */
public class AddressBottomSheet {
    Call<LocalitiesResponse> call;
    private Activity context;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheet;
    private EditText label, flat, street;
    private RadioGroup radioGroup;
    private TextView addButton;
    private AutoCompleteTextView localityAutoCompleteTextView;
    private CheckBox primaryAddress;
    private GifImageView progressBar;
    private boolean callHasBeenCompleted = true;
    private ArrayList<Locality> localities = new ArrayList<>();
    private String[] localitiesSuggestions = new String[0];
    Callback<LocalitiesResponse> localitiesResponseCallback = new Callback<LocalitiesResponse>() {
        @Override
        public void onResponse(Call<LocalitiesResponse> call, Response<LocalitiesResponse> response) {
            progressBar.setVisibility(View.GONE);
            callHasBeenCompleted = true;
            if (response.body() != null && response.body().getLocalities() != null && !response.body().getLocalities().isEmpty()) {
                localities = new ArrayList<>(response.body().getLocalities());
                localitiesSuggestions = new String[localities.size()];
                for (int i = 0; i < localities.size(); i++) {
                    localitiesSuggestions[i] = localities.get(i).getName();
                }
                setAutoCompleteAdapter();
            }
        }

        @Override
        public void onFailure(Call<LocalitiesResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            callHasBeenCompleted = true;
        }
    };
    private int codeSelected;
    private Locality localitySelected = Constants.POWAI_LOCALITY;
    private OnAddressAdded onAddressAdded;
    private Address address;

    public AddressBottomSheet(Activity context, OnAddressAdded onAddressAdded) {
        this.context = context;
        this.onAddressAdded = onAddressAdded;
    }

    public AddressBottomSheet(Activity context, OnAddressAdded onAddressAdded, Address address) {
        this.context = context;
        this.onAddressAdded = onAddressAdded;
        this.address = address;
    }

    private void setupViews() {
        label.setText(address.getSociety());
        flat.setText(address.getSociety());
        street.setText(address.getStreet());
        ((RadioButton) radioGroup.getChildAt(address.getType())).setChecked(true);
        localityAutoCompleteTextView.setText(address.getLocality().getName());
        primaryAddress.setChecked(address.isCurrentAddress());
        addButton.setText("Save");
    }

    public void show() {
        bottomSheet = context.getLayoutInflater().inflate(R.layout.layout_add_address, null);
        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
        bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(((BaseActivity) context).getContentView().getWindowToken(), 0);
            }
        });
        bottomSheetDialog.show();

        initViews();
        if (address != null) {
            setupViews();
        }
        setupAutoComplete();
        addButtonClickListener();
    }

    private void addButtonClickListener() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (label.getText().toString().isEmpty()) {
                    label.setError("This field could not be empty");
                    return;
                }
                if (flat.getText().toString().isEmpty()) {
                    flat.setError("This field could not be empty");
                    return;
                }
                if (street.getText().toString().isEmpty()) {
                    street.setError("This field could not be empty");
                    return;
                }
                if (localityAutoCompleteTextView.getText().toString().isEmpty()) {
                    localityAutoCompleteTextView.setError("This field could not be empty");
                    return;
                }
                User user = PrefUtils.getUser(context);
                boolean primaryAddressCanBeFalse = !(user.getAddresses() == null || user.getAddresses().isEmpty());
                if (!primaryAddressCanBeFalse && !primaryAddress.isChecked()) {
                    Toast.makeText(context, "Your first address should be primary", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (localitySelected == null) {
                    localityAutoCompleteTextView.setError("Selected locality doesn't exist");
                    return;
                }
                int type = radioGroup.indexOfChild(bottomSheet.findViewById(radioGroup.getCheckedRadioButtonId()));
                if (localitySelected == null && address != null) {
                    localitySelected = address.getLocality();
                }
                //TODO: Make the api call here.
                int id;
                if (address != null) {
                    id = address.getId();
                } else {
                    id = 0;
                }
                Address address = new Address(id, type,
                        label.getText().toString(),
                        flat.getText().toString(),
                        street.getText().toString(),
                        localitySelected,
                        primaryAddress.isChecked(), Address.getAddressTypeName(type)
                );
                hideKeyboard();
                if (AddressBottomSheet.this.address != null) {
                    updateAddress(address);
                } else {
                    addAddress(address);
                }

            }
        });
    }

    private void hideKeyboard() {
        View view = context.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addAddress(final Address address) {
        final BoxLoader dialog = new BoxLoader(context).show();
        MyApplication.getAPIService().addAddress(PrefUtils.getToken(context),
                new AddAddressRequestBody(
                        new AddAddressRequestBody.Address(
                                address.getLocality().getCode(),
                                address.getType(),
                                address.getFlat(),
                                address.getSociety(),
                                address.getStreet())))
                .enqueue(new Callback<AddressesApiResponse>() {
                    @Override
                    public void onResponse(Call<AddressesApiResponse> call, Response<AddressesApiResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                bottomSheetDialog.dismiss();
                                address.setId(response.body().getAddress().getId());
                                onAddressAdded.onAddressAdded(address);
                                Toast.makeText(context, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(context, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<AddressesApiResponse> call, Throwable t) {
                        dialog.dismiss();
                    }
                });
    }

    private void updateAddress(final Address address) {
        final BoxLoader dialog = new BoxLoader(context).show();
        MyApplication.getAPIService().updateAddress(PrefUtils.getToken(context),
                new UpdateAddressRequestBody(
                        new UpdateAddressRequestBody.Address(
                                address.getId()
                                , address.getLocality().getCode(),
                                address.getType(),
                                address.getFlat(),
                                address.getSociety(),
                                address.getStreet())))
                .enqueue(new Callback<AddressesApiResponse>() {
                    @Override
                    public void onResponse(Call<AddressesApiResponse> call, Response<AddressesApiResponse> response) {
                        dialog.dismiss();
                        if (response.body() != null) {
                            if (response.body().isSuccess()) {
                                bottomSheetDialog.dismiss();
                                onAddressAdded.onAddressAdded(address);
                                Toast.makeText(context, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, response.body().getInfo(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<AddressesApiResponse> call, Throwable t) {
                        dialog.dismiss();
                    }
                });
    }

    private void setupAutoComplete() {
        localityAutoCompleteTextView.setDropDownBackgroundDrawable(context.getResources().getDrawable(R.drawable.autocomplete_dropdown));
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.item_autocomplete, localitiesSuggestions);
        localityAutoCompleteTextView.setAdapter(arrayAdapter);
        localityAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (callHasBeenCompleted) {
                        callHasBeenCompleted = false;
                        call = MyApplication.getAPIService().getAllLocalities(PrefUtils.getToken(context), editable.toString());
                        call.enqueue(localitiesResponseCallback);
                    } else {
                        call.cancel();
                        call = MyApplication.getAPIService().getAllLocalities(PrefUtils.getToken(context), editable.toString());
                        call.enqueue(localitiesResponseCallback);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
        localityAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                codeSelected = localities.get(position).getCode();
                localitySelected = localities.get(position);
            }
        });
    }

    private void initViews() {
        progressBar = (GifImageView) bottomSheet.findViewById(R.id.progress_bar);
        label = (EditText) bottomSheet.findViewById(R.id.society_edit_text);
        flat = (EditText) bottomSheet.findViewById(R.id.flat_edit_text);
        street = (EditText) bottomSheet.findViewById(R.id.street_edit_text);
        radioGroup = (RadioGroup) bottomSheet.findViewById(R.id.radio_group_type);
        localityAutoCompleteTextView = (AutoCompleteTextView) bottomSheet.findViewById(R.id.edit_text_locality);
        primaryAddress = (CheckBox) bottomSheet.findViewById(R.id.check_box_primary_address);
        addButton = (TextView) bottomSheet.findViewById(R.id.button_add);
        progressBar.setVisibility(View.GONE);
        localityAutoCompleteTextView.setText(Constants.POWAI_LOCALITY.getName());
        localityAutoCompleteTextView.setFocusable(false);
        localityAutoCompleteTextView.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        localityAutoCompleteTextView.setClickable(false); //
    }

    private void setAutoCompleteAdapter() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.item_autocomplete, localitiesSuggestions);
        localityAutoCompleteTextView.setAdapter(arrayAdapter);
    }

    public interface OnAddressAdded {
        void onAddressAdded(Address address);
    }
}
