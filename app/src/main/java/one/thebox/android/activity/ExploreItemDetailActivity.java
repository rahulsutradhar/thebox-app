package one.thebox.android.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import one.thebox.android.Models.Box;
import one.thebox.android.Models.ExploreItem;
import one.thebox.android.R;
import one.thebox.android.adapter.ExploreItemAdapter;
import one.thebox.android.adapter.SearchDetailAdapter;

public class ExploreItemDetailActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SearchDetailAdapter searchDetailAdapter;

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
    }

    private void setupRecyclerView() {
        searchDetailAdapter = new SearchDetailAdapter(this);
        for (int i = 0; i < 10; i++) {
            searchDetailAdapter.addBoxItem(new Box.BoxItem());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchDetailAdapter);
    }

    @Override
    void onClick(int id) {

    }
}
