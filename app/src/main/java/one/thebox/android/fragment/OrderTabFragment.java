package one.thebox.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import one.thebox.android.Models.Order;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.ViewPagerAdapter;
import one.thebox.android.activity.MainActivity;
import one.thebox.android.api.Responses.OrdersApiResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderTabFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View rootView;
    private ProgressBar progressBar;
    private LinearLayout holder;
    private ArrayList<Order> cartOrders = new ArrayList<>();
    private ArrayList<Order> subscriptionOrders = new ArrayList<>();
    private RealmList<Order> subscriptionAndCart = new RealmList<>();

    public OrderTabFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getToolbar().setTitle("My Orders");
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_order_tab, container, false);
            initVariables();
            initViews();
            if (!subscriptionAndCart.isEmpty()) {
                setupViewPagerAndTabs();
            }
            getAllOrders();
        }
        return rootView;
    }

    private void initVariables() {
        Realm realm = MyApplication.getRealm();
        RealmQuery<Order> query = realm.where(Order.class);
        RealmResults<Order> realmResults = query.notEqualTo(Order.FIELD_ID, 0).findAll();
        for (int i = 0; i < realmResults.size(); i++) {
            subscriptionAndCart.add(realmResults.get(i));
        }
    }

    private void initViews() {
        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        viewPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        holder = (LinearLayout) rootView.findViewById(R.id.holder);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setupViewPagerAndTabs() {
        if (getActivity() == null) {
            return;
        }
        progressBar.setVisibility(View.GONE);
        holder.setVisibility(View.VISIBLE);
        for (Order order : subscriptionAndCart) {
            if (order.isCart()) {
                cartOrders.add(order);
            } else {
                subscriptionOrders.add(order);
            }
        }
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager(), getActivity());
        adapter.addFragment(CartFragment.newInstance(cartOrders), "Cart");
        adapter.addFragment(UpComingOrderFragment.newInstance(subscriptionOrders), "Upcoming Order");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onStart() {
        MainActivity.isSearchFragmentIsAttached = true;
        super.onStart();
    }

    @Override
    public void onStop() {
        MainActivity.isSearchFragmentIsAttached = false;
        super.onStop();
    }

    public void getAllOrders() {
        MyApplication.getAPIService().getMyOrders(PrefUtils.getToken(getActivity()))
                .enqueue(new Callback<OrdersApiResponse>() {
                             @Override
                             public void onResponse(Call<OrdersApiResponse> call, Response<OrdersApiResponse> response) {
                                 if (response.body() != null) {
                                     if (response.body().isSuccess()) {
                                         if (!subscriptionAndCart.equals(response.body().getOrders())) {
                                             subscriptionAndCart.clear();
                                             subscriptionAndCart.addAll(response.body().getOrders());
                                             storeToRealm();
                                             setupViewPagerAndTabs();
                                         }
                                     }
                                 }
                             }

                             @Override
                             public void onFailure(Call<OrdersApiResponse> call, Throwable t) {
                                 progressBar.setVisibility(View.GONE);
                                 holder.setVisibility(View.VISIBLE);
                             }
                         }

                );
    }

    private void storeToRealm() {
        final Realm superRealm = MyApplication.getRealm();
        for (final Order order : subscriptionAndCart) {
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

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).getToolbar().setSubtitle(null);
    }
}
