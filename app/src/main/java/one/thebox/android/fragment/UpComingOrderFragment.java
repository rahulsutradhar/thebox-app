package one.thebox.android.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.AppBarObserver;
import one.thebox.android.activity.ConfirmAddressActivity;
import one.thebox.android.adapter.OrdersItemAdapter;
import one.thebox.android.api.Responses.OrdersApiResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UpComingOrderFragment extends Fragment implements View.OnClickListener{

    ArrayList<Integer> orderIds = new ArrayList<>();
    private View rootView;
    private RecyclerView recyclerView;
    private OrdersItemAdapter ordersItemAdapter;
    private RealmList<Order> orders = new RealmList<>();
    private TextView emptyOrderText;

    public UpComingOrderFragment() {
    }

    public static UpComingOrderFragment newInstance() {
        UpComingOrderFragment fragment = new UpComingOrderFragment();
      /*  Bundle args = new Bundle();
        ArrayList<Integer> orderIds = new ArrayList<>();
        for (Order order : orders) {
            orderIds.add(order.getId());
        }
        args.putString(EXTRA_ORDER_ARRAY, CoreGsonUtils.toJson(orderIds));
        fragment.setArguments(args);*/
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
        getAllOrders();
        return rootView;
    }

    private void setupRecyclerView() {
        if (orders == null || orders.isEmpty()) {
            emptyOrderText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyOrderText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            ordersItemAdapter = new OrdersItemAdapter(getActivity(), orders);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(ordersItemAdapter);
        }
    }

    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        emptyOrderText = (TextView) rootView.findViewById(R.id.empty_order_text_view);

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

    public void getAllOrders() {
        MyApplication.getAPIService().getMyOrders(PrefUtils.getToken(getActivity()))
                .enqueue(new Callback<OrdersApiResponse>() {
                             @Override
                             public void onResponse(Call<OrdersApiResponse> call, Response<OrdersApiResponse> response) {
                                 if (response.body() != null) {
                                     if (response.body().isSuccess()) {
                                         if (!orders.equals(response.body().getOrders())) {
                                             orders.clear();
                                             for (int i = 0; i < response.body().getOrders().size(); i++) {
                                                 if (!response.body().getOrders().get(i).isCart())
                                                     orders.add(response.body().getOrders().get(i));
                                             }
                                             storeToRealm();
                                             setupRecyclerView();
                                         }
                                     }
                                 }
                             }

                             @Override
                             public void onFailure(Call<OrdersApiResponse> call, Throwable t) {
                                /* progressBar.setVisibility(View.GONE);
                                 holder.setVisibility(View.VISIBLE);*/
                             }
                         }

                );
    }

    private void storeToRealm() {
        final Realm superRealm = MyApplication.getRealm();
        for (final Order order : orders) {
            superRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(order);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    //Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();
                }
            }, new Realm.Transaction.OnError() {
                @Override
                public void onError(Throwable error) {
                    // Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private void initVariables() {
        Realm realm = MyApplication.getRealm();
        RealmQuery<Order> query = realm.where(Order.class);
        RealmResults<Order> realmResults = query.notEqualTo(Order.FIELD_ID, 0).equalTo(Order.FIELD_IS_CART, false).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            orders.add(realmResults.get(i));
        }
    }
}
