package one.thebox.android.viewmodel.address;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import io.realm.RealmList;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.Address;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.activity.ConfirmTimeSlotActivity;
import one.thebox.android.fragment.address.DeliveryAddressFragment;
import one.thebox.android.viewmodel.base.BaseViewModel;

/**
 * Created by developers on 08/03/17.
 */

public class DeliveryAddressFragmentViewModel extends BaseViewModel {

    /**
     * Address
     */
    private Address address;

    /**
     * List Orders
     */
    private RealmList<Order> orders;

    /**
     * DeliveryAddressFragment
     */
    private DeliveryAddressFragment deliveryAddressFragment;

    /**
     * AddressType text
     */
    private String addressTypeText;
    private int addressTypeIcon;

    /**
     * Constrcutor
     */
    public DeliveryAddressFragmentViewModel(DeliveryAddressFragment deliveryAddressFragment, Address address, RealmList<Order> orders) {
        this.deliveryAddressFragment = deliveryAddressFragment;
        this.address = address;
        this.orders = orders;
        checkAddressType();
        notifyChange();
    }

    /**
     * Click Event
     */
    public void onClickProceedToPayment() {

        deliveryAddressFragment.getActivity().startActivity(ConfirmTimeSlotActivity.newInstance(deliveryAddressFragment.getActivity(),
                OrderHelper.getAddressAndOrder(orders), false));
    }

    public void checkAddressType() {
        try {
            switch (address.getLabel()) {
                case 0:
                    addressTypeText = "HOME";
                    addressTypeIcon = R.drawable.ic_home_black_24px;
                    break;
                case 1:
                    addressTypeText = "WORK";
                    addressTypeIcon = R.drawable.ic_work_black_24px;
                    break;
                case 2:
                    addressTypeText = "OTHERS";
                    addressTypeIcon = R.drawable.ic_location_other_black_24px;
                    break;
                default:
                    addressTypeText = "HOME";
                    addressTypeIcon = R.drawable.ic_home_black_24px;
            }
            notifyChange();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAddressFlat() {
        String flat = "";
        try {
            flat = address.getFlat();
        } catch (Exception e) {
            e.printStackTrace();
            flat = "";
        }
        return flat;
    }

    public String getAddressSociety() {
        String society = "";
        try {
            society = address.getSociety();
        } catch (Exception e) {
            e.printStackTrace();
            society = "";
        }
        return society;
    }

    public String getAddressStreet() {
        String street = "";
        try {
            street = address.getStreet();
        } catch (Exception e) {
            e.printStackTrace();
            street = "";
        }
        return street;
    }

    /**
     * Bindable Adapter
     */
    @BindingAdapter("android:src")
    public static void setImageDrawable(ImageView imageDrawable, int addressTypeIcon) {
        imageDrawable.setImageResource(addressTypeIcon);
    }


    public String getAddressTypeText() {
        return addressTypeText;
    }

    public void setAddressTypeText(String addressTypeText) {
        this.addressTypeText = addressTypeText;
    }

    public int getAddressTypeIcon() {
        return addressTypeIcon;
    }

    public void setAddressTypeIcon(int addressTypeIcon) {
        this.addressTypeIcon = addressTypeIcon;
    }
}

