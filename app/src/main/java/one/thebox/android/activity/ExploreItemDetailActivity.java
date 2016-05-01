package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import one.thebox.android.Events.ItemAddEvent;
import one.thebox.android.Models.BoxItem;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.UserItem;
import one.thebox.android.R;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.api.Responses.CategoryBoxItemsResponse;
import one.thebox.android.api.Responses.ExploreBoxResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.CoreGsonUtils;
import one.thebox.android.util.PrefUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreItemDetailActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SearchDetailAdapter searchDetailAdapter;
    private TextView noOfItemSelectedTextView;
    public static final String EXTRA_EXPLORE_ITEM = "extra_explore_item";
    public static final String EXTRA_CATEGORY_ID = "extra_category_id";
    private ExploreItem exploreItem;
    private ProgressBar progressBar;
    private int categoryId;

    public static Intent getInstance(Context context, int categoryId, String exploreItem) {
        return new Intent(context, ExploreItemDetailActivity.class)
                .putExtra(EXTRA_CATEGORY_ID, categoryId)
                .putExtra(EXTRA_EXPLORE_ITEM, exploreItem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_item_detail);
        exploreItem = CoreGsonUtils.fromJson(getIntent().getStringExtra(EXTRA_EXPLORE_ITEM), ExploreItem.class);
        initViews();
        setTitle(exploreItem.getTitle());
        if (categoryId == 0) {
            getData();
        } else {
        }
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noOfItemSelectedTextView = (TextView) findViewById(R.id.no_of_item_selected);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        noOfItemSelectedTextView.bringToFront();
    }

    @Subscribe
    public void onItemAddEvent(ItemAddEvent itemAddEvent) {
        if (itemAddEvent.getNoOfItems() <= 0) {
            noOfItemSelectedTextView.setVisibility(View.GONE);
        } else {
            noOfItemSelectedTextView.setVisibility(View.VISIBLE);
            noOfItemSelectedTextView.setText(String.valueOf(itemAddEvent.getNoOfItems()));
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

    public void getData() {
        MyApplication.getAPIService().getExploreBox(PrefUtils.getToken(this), exploreItem.getId())
                .enqueue(new Callback<ExploreBoxResponse>() {
                    @Override
                    public void onResponse(Call<ExploreBoxResponse> call, Response<ExploreBoxResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.body() != null) {
                            ArrayList<Category> categories = new ArrayList<>();
                            categories.add(response.body().getSelectedCategory());
                            categories.addAll(response.body().getRestCategories());
                            searchDetailAdapter = new SearchDetailAdapter(ExploreItemDetailActivity.this);
                            searchDetailAdapter.setBoxItems(response.body().getNormalItems(), response.body().getMyItems(), categories);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ExploreItemDetailActivity.this));
                            recyclerView.setAdapter(searchDetailAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<ExploreBoxResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public void getCategoryItem() {
        MyApplication.getAPIService().getCategoryBoxItems(PrefUtils.getToken(this),
                categoryId)
                .enqueue(new Callback<CategoryBoxItemsResponse>() {
                    @Override
                    public void onResponse(Call<CategoryBoxItemsResponse> call, Response<CategoryBoxItemsResponse> response) {
                        progressBar.setVisibility(View.GONE);
                        if (response.body() != null) {
                            ArrayList<Category> categories = new ArrayList<>();
                            categories.add(response.body().getSelectedCategory());
                            categories.addAll(response.body().getRestCategories());
                            searchDetailAdapter = new SearchDetailAdapter(ExploreItemDetailActivity.this);
                            searchDetailAdapter.setBoxItems(response.body().getNormalBoxItems(), response.body().getMyBoxItems(), categories);
                            recyclerView.setLayoutManager(new LinearLayoutManager(ExploreItemDetailActivity.this));
                            recyclerView.setAdapter(searchDetailAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryBoxItemsResponse> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
}
