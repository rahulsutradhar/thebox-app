package one.thebox.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import one.thebox.android.Events.ItemAddEvent;
import one.thebox.android.Models.Box;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.R;
import one.thebox.android.adapter.ExploreItemAdapter;
import one.thebox.android.adapter.SearchDetailAdapter;
import one.thebox.android.adapter.SearchSuggestionAdapter;

public class ExploreItemDetailActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private SearchDetailAdapter searchDetailAdapter;
    private TextView noOfItemSelectedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_item_detail);
        initViews();
        setupRecyclerView();
        setTitle("The Healthy Snack Box");
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        noOfItemSelectedTextView = (TextView) findViewById(R.id.no_of_item_selected);
        noOfItemSelectedTextView.bringToFront();
    }

    private void setupRecyclerView() {
        searchDetailAdapter = new SearchDetailAdapter(this);
        for (int i = 0; i < 10; i++) {
            searchDetailAdapter.addBoxItem(new Box.BoxItem());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchDetailAdapter);
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
}
