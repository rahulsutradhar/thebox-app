package one.thebox.android.fragment.address;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.realm.RealmList;
import one.thebox.android.Models.Address;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
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
     * List Orders
     */
    private RealmList<Order> orders;

    /**
     * Constructor
     */
    public DeliveryAddressFragment() {

    }

    @SuppressLint("ValidFragment")
    public DeliveryAddressFragment(Address address, RealmList<Order> orders) {
        this.address = address;
        this.orders = orders;
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
        deliveryAddressFragmentViewModel = new DeliveryAddressFragmentViewModel(this, address, orders);
        binding.setViewModel(deliveryAddressFragmentViewModel);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
