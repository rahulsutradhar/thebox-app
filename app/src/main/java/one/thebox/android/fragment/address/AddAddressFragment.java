package one.thebox.android.fragment.address;


import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.Subscribe;

import io.realm.RealmList;
import one.thebox.android.Models.Address;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.activity.address.AddressActivity;
import one.thebox.android.databinding.AddAddressFragmentBinding;
import one.thebox.android.fragment.base.FragmentBase;
import one.thebox.android.viewmodel.address.AddAddressFragmentViewModel;

/**
 * Created by developers on 07/03/17.
 */

public class AddAddressFragment extends FragmentBase {

    /**
     * View Model
     */
    private AddAddressFragmentViewModel addAddressFragmentViewModel;

    private int calledFrom;
    private int type;

    /**
     * Address
     */
    private Address address;

    /**
     * Orders
     */
    private RealmList<Order> orders;

    /**
     * Constructor
     */
    public AddAddressFragment() {

    }

    @SuppressLint("ValidFragment")
    public AddAddressFragment(int calledFrom, int type) {
        this.calledFrom = calledFrom;
        this.type = type;
    }

    @SuppressLint("ValidFragment")
    public AddAddressFragment(int calledFrom, int type, Address address) {
        this.calledFrom = calledFrom;
        this.type = type;
        this.address = address;
    }

    @SuppressLint("ValidFragment")
    public AddAddressFragment(int calledFrom, int type, RealmList<Order> orders) {
        this.calledFrom = calledFrom;
        this.type = type;
        this.orders = orders;
    }

    /**
     * Called from delivery Address Fragment to edit the Delivery Address
     * calledFrom = 3; type = 3
     */
    @SuppressLint("ValidFragment")
    public AddAddressFragment(int calledFrom, int type, RealmList<Order> orders, Address address) {
        this.calledFrom = calledFrom;
        this.orders = orders;
        this.address = address;
        this.type = type;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_address, container, false);

        AddAddressFragmentBinding binding = DataBindingUtil.bind(view);
        //Add Address
        if (type == 1) {
            //Called from My Fargment
            if (calledFrom == 1) {
                addAddressFragmentViewModel = new AddAddressFragmentViewModel(this, calledFrom, type, view);
            } //called from cart fragment
            else if (calledFrom == 2) {
                addAddressFragmentViewModel = new AddAddressFragmentViewModel(this, orders, calledFrom, type, view);
            }
        }// Edit Address
        else if (type == 2) {
            addAddressFragmentViewModel = new AddAddressFragmentViewModel(this, address, calledFrom, type, view);
        }//edit address from AddAddressFragment
        else if (type == 3 && calledFrom == 3) {
            addAddressFragmentViewModel = new AddAddressFragmentViewModel(this, address, orders, calledFrom, type, view);
        }
        binding.setViewModel(addAddressFragmentViewModel);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        addAddressFragmentViewModel.bindLayout();

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
}
