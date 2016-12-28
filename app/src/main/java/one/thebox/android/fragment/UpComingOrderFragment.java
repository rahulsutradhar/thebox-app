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

    ArrayList<Integer> orderIds = new ArrayList<>();
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
        initViews();
        initVariables();
        setupRecyclerView();
        return rootView;
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        no_orders_subscribed_view_holder = (LinearLayout) rootView.findViewById(R.id.no_orders_subscribed_view_holder);
        progress_bar = (GifImageView) rootView.findViewById(R.id.progress_bar);
    }

    private void initVariables() {
        Realm realm = MyApplication.getRealm();
        RealmQuery<Order> query = realm.where(Order.class);
        RealmResults<Order> realmResults = query.notEqualTo(Order.FIELD_ID, 0).equalTo(Order.FIELD_IS_CART, false).findAll();

        orders.clear();

        for (int i = 0; i < realmResults.size(); i++) {
            orders.add(realmResults.get(i));
        }
    }

    private void setupRecyclerView() {
        if (orders == null || orders.isEmpty()) {
            no_orders_subscribed_view_holder.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            // Adding Onclick listener directing to Store Fragment
            no_orders_subscribed_view_holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new OnHomeTabChangeEvent(1));
                }
            });
        }
        else {
            no_orders_subscribed_view_holder.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            ordersItemAdapter = new OrdersItemAdapter(getActivity(), orders);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(ordersItemAdapter);

            // If orders are not fetched completely, showing progress bar, fetching orders and removing progress bar
            // If orders are being updated on server after payment they are not fetched
            if (PrefUtils.getBoolean(MyApplication.getInstance(),Constants.PREF_IS_ORDER_IS_LOADING,true)){
                progress_bar.setVisibility(View.VISIBLE);
            }
            else if(orders.size() < 4 ){
                progress_bar.setVisibility(View.VISIBLE);
                new OrderHelper(new OrderHelper.OnOrdersFetched() {
                    @Override
                    public void OnOrdersFetched() {
                        initVariables();
                        setupRecyclerView();
                        progress_bar.setVisibility(View.GONE);
                    }
                });
            }
        }
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

    @Subscribe
    public void onUpdateUpcomingDeliveries(UpdateUpcomingDeliveriesEvent UpdateUpcomingDeliveriesEvent) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    orders.clear();
                    initVariables();
                    setupRecyclerView();
                    progress_bar.setVisibility(View.GONE);
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
        onUpdateUpcomingDeliveries(new UpdateUpcomingDeliveriesEvent(3));
    }
}
