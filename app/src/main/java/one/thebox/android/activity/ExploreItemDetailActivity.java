package one.thebox.android.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import one.thebox.android.Events.ItemAddEvent;
import one.thebox.android.Events.TabEvent;
import one.thebox.android.Models.Category;
import one.thebox.android.Models.ExploreItem;
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
    public static final String EXTRA_CATEGORY_ITEM = "extra_category_item";
    private ExploreItem exploreItem;
    private ProgressBar progressBar;
    private String categoryItem;
    private Category category;
    private FloatingActionButton floatingActionButton;

    public static Intent getInstance(Context context, String categoryItem, String exploreItem) {
        return new Intent(context, ExploreItemDetailActivity.class)
                .putExtra(EXTRA_CATEGORY_ITEM, categoryItem)
                .putExtra(EXTRA_EXPLORE_ITEM, exploreItem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_item_detail);
        initViews();
        categoryItem = getIntent().getStringExtra(EXTRA_CATEGORY_ITEM);
        if (categoryItem == null || categoryItem.isEmpty()) {
            exploreItem = CoreGsonUtils.fromJson(getIntent().getStringExtra(EXTRA_EXPLORE_ITEM), ExploreItem.class);
            getExploreData();
            setTitle(exploreItem.getTitle());
        } else {
            category = CoreGsonUtils.fromJson(getIntent().getStringExtra(EXTRA_CATEGORY_ITEM), Category.class);
            getCategoryData();
            setTitle(category.getTitle());
        }
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noOfItemSelectedTextView = (TextView) findViewById(R.id.no_of_item_selected);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        noOfItemSelectedTextView.bringToFront();
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //EventBus.getDefault().post(new TabEvent(1));
                startActivity(new Intent(ExploreItemDetailActivity.this, MainActivity.class).putExtra(MainActivity.EXTRA_TAB_NO,1));
                finish();
            }
        });
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

    public void getExploreData() {
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

    public void getCategoryData() {
        MyApplication.getAPIService().getCategoryBoxItems(PrefUtils.getToken(this),
                category.getId())
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
