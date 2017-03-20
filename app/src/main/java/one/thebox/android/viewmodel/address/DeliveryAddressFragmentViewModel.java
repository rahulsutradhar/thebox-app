package one.thebox.android.viewmodel.address;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.util.HashMap;

import io.realm.RealmList;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.Address;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.activity.ConfirmTimeSlotActivity;
import one.thebox.android.activity.address.AddressActivity;
import one.thebox.android.app.TheBox;
import one.thebox.android.fragment.address.AddAddressFragment;
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
     * ArrayAdapter
     */
    private ArrayAdapter<String> adapterTypeSelection;

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

        //save CleverTap Event; Display Address Proceed
        saveCleverTapEventDisplayAddressProceed();

        deliveryAddressFragment.getActivity().startActivity(ConfirmTimeSlotActivity.newInstance(deliveryAddressFragment.getActivity(),
                OrderHelper.getAddressAndOrder(orders), false));
    }

    public void onClickOverflowMenu(View view) {

        int[] location = new int[2];
        view.getLocationOnScreen(location);

        // Initialize the Point with x, and y positions
        Point point = new Point();
        point.x = location[0];
        point.y = location[1];

        setUpPopupWindow(view, point);
    }

    public void setUpPopupWindow(View v, Point point) {

        int popupwindowHeight = LinearLayout.LayoutParams.WRAP_CONTENT;

        LayoutInflater layoutInflater = (LayoutInflater) deliveryAddressFragment.getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = layoutInflater.inflate(
                R.layout.popup_window_card_address, null);


        // Creating the PopupWindow
        final PopupWindow pwindow = new PopupWindow(deliveryAddressFragment.getActivity());
        pwindow.setContentView(layout);
        pwindow.setHeight(popupwindowHeight);
        pwindow.setFocusable(true);
        pwindow.setOutsideTouchable(true);

        // Call requires API level 21
        if (Build.VERSION.SDK_INT >= 21) {
            pwindow.setElevation(10f);
            pwindow.setBackgroundDrawable(new ColorDrawable(deliveryAddressFragment.getActivity().getResources().getColor(R.color.white)));
        } else {
            pwindow.setBackgroundDrawable(deliveryAddressFragment.getActivity().getResources().getDrawable(R.drawable.background_popup_window_card_address_shadow));
        }

        int OFFSET_X = 250;
        int OFFSET_Y = 30;

        String[] types = {"EDIT"};

        adapterTypeSelection = new ArrayAdapter<String>(deliveryAddressFragment.getActivity(),
                R.layout.item_popup_window,
                R.id.textView, types);
        ListView listview = (ListView) pwindow.getContentView().findViewById(
                R.id.listview);
        listview.setAdapter(adapterTypeSelection);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                switch (position) {
                    case 0:
                        //write you function
                        editAddress();
                        break;
                    default:
                        break;
                }

                pwindow.dismiss();
            }
        });

        pwindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                //TODO dismiss settings
            }

        });

        pwindow.setWidth(300);
        pwindow.showAtLocation(layout, Gravity.NO_GRAVITY, point.x - OFFSET_X, point.y + OFFSET_Y);
    }


    /**
     * Edit Delivery Address
     */
    public void editAddress() {
        /**
         * calledFrom = 3; Type = 3
         */
        AddAddressFragment addAddressFragment = new AddAddressFragment(3, 3, orders, address);

        FragmentManager fragmentManager = deliveryAddressFragment.getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, addAddressFragment, "Add_Address")
                .addToBackStack("DeliveryAddress");
        fragmentTransaction.commit();

        ((AddressActivity) deliveryAddressFragment.getActivity()).getToolbar().setTitle("Edit Delivery Address");

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

    public void saveCleverTapEventDisplayAddressProceed() {
        HashMap<String, Object> saveAddress = new HashMap<>();
        saveAddress.put("delivery_address", address);

        TheBox.getCleverTap().event.push("display_address_proceed", saveAddress);
    }

}

