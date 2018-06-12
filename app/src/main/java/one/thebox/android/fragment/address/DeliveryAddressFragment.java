package one.thebox.android.fragment.address;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import one.thebox.android.Models.address.Address;
import one.thebox.android.R;
import one.thebox.android.activity.address.AddressActivity;
import one.thebox.android.databinding.DeliveryAddressFragmentBinding;
import one.thebox.android.fragment.base.FragmentBase;
import one.thebox.android.viewmodel.address.DeliveryAddressFragmentViewModel;

/**
 * Created by developers on 08/03/17.
 */

public class DeliveryAddressFragment extends FragmentBase {

    /**
     * DeliverAddressFragmentViewModel
     */
    private DeliveryAddressFragmentViewModel deliveryAddressFragmentViewModel;

    /**
     * Address
     */
    private Address address;

    /**
     * Is Merge
     */
    private boolean isMerge;

    /**
     * Constructor
     */
    public DeliveryAddressFragment() {

    }

    /**
     * Called from Add Address Fragment
     */
    @SuppressLint("ValidFragment")
    public DeliveryAddressFragment(Address address, boolean isMerge) {
        this.address = address;
        this.isMerge = isMerge;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delivery_address, container, false);

        DeliveryAddressFragmentBinding binding = DataBindingUtil.bind(view);
        deliveryAddressFragmentViewModel = new DeliveryAddressFragmentViewModel(this, address, isMerge);
        binding.setViewModel(deliveryAddressFragmentViewModel);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((AddressActivity) getActivity()).getToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                } else {
                    getActivity().finish();
                }


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((AddressActivity) getActivity()).getToolbarTitle().setText("Delivery Address");
    }
}
