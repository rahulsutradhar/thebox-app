package one.thebox.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceScreen;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Events.OnHomeTabChangeEvent;
import one.thebox.android.Events.UpdateUpcomingDeliveriesEvent;
import one.thebox.android.Helpers.OrderHelper;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.activity.ConfirmAddressActivity;
import one.thebox.android.adapter.OrdersItemAdapter;
import one.thebox.android.api.Responses.OrdersApiResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.Constants;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpComingOrderFragment extends Fragment implements View.OnClickListener {

    //Variables
    public int scene_number = 0;

    //Views
    private View rootView;
    private RecyclerView recyclerView;
    private OrdersItemAdapter ordersItemAdapter;
    private RealmList<Order> orders = new RealmList<>();
    private LinearLayout no_orders_subscribed_view_holder;
    private GifImageView progress_bar;

    public UpComingOrderFragment() {
    }

    public static UpComingOrderFragment newInstance() {
        UpComingOrderFragment fragment = new UpComingOrderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_up_coming_order, container, false);
        initVariables();
        initViews();
        return rootView;
    }

    private void initVariables() {

        //ignore Order class if order successful is false
        Realm realm = MyApplication.getRealm();
        RealmQuery<Order> query = realm.where(Order.class).notEqualTo("successful", false);
        RealmResults<Order> realmResults = query.notEqualTo(Order.FIELD_ID, 0).equalTo(Order.FIELD_IS_CART, false).findAll();
        orders.clear();
        for (int i = 0; i < realmResults.size(); i++) {
            orders.add(realmResults.get(i));
        }

        //Setting scenes
        if (orders.isEmpty()) {
            //No data available;show static content
            //New User
            scene_number = -1;
        }//Atleast one delivery scheduled by the user
        else if (PrefUtils.getBoolean(MyApplication.getInstance(), Constants.PREF_IS_ORDER_IS_LOADING, true)) {
            if (PrefUtils.should_i_fetch_model_data_from_server(MyApplication.getInstance(), 0)) {
                OrderHelper.getOrderAndNotify(false);
                PrefUtils.clean_model_being_updated_on_server_details(MyApplication.getInstance(), 0);
            }
            scene_number = 1;
        } else if ((orders.size() < 4)) {
            scene_number = 1;
        } else {
            scene_number = 0;
        }
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        no_orders_subscribed_view_holder = (LinearLayout) rootView.findViewById(R.id.no_orders_subscribed_view_holder);
        progress_bar = (GifImageView) rootView.findViewById(R.id.progress_bar);

        switch (scene_number) {
            case -1: {
                no_orders_subscribed_view_holder.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                no_orders_subscribed_view_holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EventBus.getDefault().post(new OnHomeTabChangeEvent(1));
                    }
                });
                break;
            }
            case 0: {
                setupRecyclerView();
                progress_bar.setVisibility(View.GONE);
                break;
            }
            case 1: {
                progress_bar.setVisibility(View.VISIBLE);
                setupRecyclerView();
                break;
            }
        }
    }

    private void setupRecyclerView() {
        recyclerView.setVisibility(View.VISIBLE);
        ordersItemAdapter = new OrdersItemAdapter(getActivity(), orders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(ordersItemAdapter);
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
    }

    // Called after Constants.PREF_IS_ORDER_IS_LOADING is switched from false to true
    @Subscribe
    public void onUpdateUpcomingDeliveries(UpdateUpcomingDeliveriesEvent UpdateUpcomingDeliveriesEvent) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initVariables();
                    initViews();
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
