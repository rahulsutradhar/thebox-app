package one.thebox.android.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import one.thebox.android.Events.SearchEvent;
import one.thebox.android.Models.SearchResult;
import one.thebox.android.R;
import one.thebox.android.ViewHelper.MontserratTextView;
import one.thebox.android.ViewHelper.ShowCaseHelper;
import one.thebox.android.adapter.SearchAutoCompleteAdapter;
import one.thebox.android.api.Responses.SearchAutoCompleteResponse;
import one.thebox.android.app.MyApplication;
import one.thebox.android.util.PrefUtils;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Ruchit on 9/13/2016.
 */
public class UniversalSearchActivity extends Activity {


    @BindView(R.id.imgsearchBack)
    ImageView imgSearchBack;
    @BindView(R.id.edt_search_query)
    EditText edtSearchQuery;
    @BindView(R.id.recycler_view_search)
    RecyclerView searchRecyclerView;

    @BindView(R.id.imgSearchCancel)
    ImageView imgSearchCancel;
    @BindView(R.id.headerSearch)
    RelativeLayout headerSearch;

    @BindView(R.id.llNoResult)
    LinearLayout llNoResult;

    private String query;
    public Typeface type;

    TextView progress_bar_text;
    private Call<SearchAutoCompleteResponse> call;
    private SearchAutoCompleteAdapter searchAutoCompleteAdapter;
    private ArrayList<SearchResult> searchResults = new ArrayList<>();
    private int tempScroll;
    @BindView(R.id.progress_bar)
    GifImageView progressBar;

    private boolean callHasBeenCompleted = true;

    Callback<SearchAutoCompleteResponse> searchAutoCompleteResponseCallback = new Callback<SearchAutoCompleteResponse>() {
        @Override
        public void onResponse(Call<SearchAutoCompleteResponse> call, Response<SearchAutoCompleteResponse> response) {
            progressBar.setVisibility(View.GONE);
            progress_bar_text.setVisibility(View.GONE);
            callHasBeenCompleted = true;
            if (response.body() != null) {
                onSearchEvent(new SearchEvent(query, response.body()));
            }
        }

        @Override
        public void onFailure(Call<SearchAutoCompleteResponse> call, Throwable t) {
            progressBar.setVisibility(View.GONE);
            progress_bar_text.setVisibility(View.GONE);
            callHasBeenCompleted = true;
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universal_search);
        progress_bar_text = (TextView) findViewById(R.id.progress_bar_text);
        ButterKnife.bind(this);
        progressBar.setVisibility(View.GONE);
        progress_bar_text.setVisibility(View.GONE);

        initViews();
        setupRecyclerView();

        imgSearchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearchQuery.setText("");
                searchRecyclerView.setVisibility(View.GONE);
                llNoResult.setVisibility(View.GONE);
            }
        });


        edtSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                query = s.toString();
//                attachSearchResultFragment();
                if (s.length() > 0) {
                    imgSearchCancel.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    progress_bar_text.setVisibility(View.VISIBLE);
                    searchRecyclerView.setVisibility(View.GONE);
                    llNoResult.setVisibility(View.GONE);
                    if (callHasBeenCompleted) {
                        callHasBeenCompleted = false;
                        call = MyApplication.getAPIService().searchAutoComplete(PrefUtils.getToken(UniversalSearchActivity.this), query);
                        call.enqueue(searchAutoCompleteResponseCallback);
                    } else {
                        call.cancel();
                        call = MyApplication.getAPIService().searchAutoComplete(PrefUtils.getToken(UniversalSearchActivity.this), query);
                        call.enqueue(searchAutoCompleteResponseCallback);
                    }
                } else {
                    imgSearchCancel.setVisibility(View.GONE);
                    searchRecyclerView.setVisibility(View.GONE);
                    llNoResult.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    progress_bar_text.setVisibility(View.GONE);
                }
            }
        });

        imgSearchBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        new ShowCaseHelper(this, 0).show("Search", "Search for an item, brand or category", headerSearch);


    }


    private void setupRecyclerView() {
        searchAutoCompleteAdapter = new SearchAutoCompleteAdapter(UniversalSearchActivity.this);
        searchAutoCompleteAdapter.setSearchResults(searchResults);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(UniversalSearchActivity.this));
        searchRecyclerView.setAdapter(searchAutoCompleteAdapter);
    }

    private void initViews() {
        type=Typeface.createFromAsset(getAssets(),"fonts/AvenirLTStd-Book.otf");
        progress_bar_text.setTypeface(type);
        searchRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy != 0) {
                    InputMethodManager imm = (InputMethodManager) UniversalSearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), 0);
                }
            }
        });
        edtSearchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    if (searchAutoCompleteAdapter.getSearchResults().size() != 0)
//                        ((MainActivity) getActivity()).attachSearchDetailFragment(searchAutoCompleteAdapter.getSearchResults().get(0));
//                    return true;
                }
                return false;
            }
        });
    }


    public void onSearchEvent(SearchEvent searchEvent) {
        searchResults.clear();
        for (int i = 0; i < searchEvent.getSearchAutoCompleteResponse().getCategories().size(); i++) {
            String categoryName = searchEvent.getSearchAutoCompleteResponse().getCategories().get(i).getTitle();
            int categoryId = searchEvent.getSearchAutoCompleteResponse().getCategories().get(i).getId();
            SearchResult searchResult = new SearchResult(categoryId, categoryName);
            searchResults.add(searchResult);
        }
        for (int i = 0; i < searchEvent.getSearchAutoCompleteResponse().getItems().size(); i++) {
            String itemName = searchEvent.getSearchAutoCompleteResponse().getItems().get(i);
            SearchResult searchResult = new SearchResult(itemName);
            searchResults.add(searchResult);
        }
        if (searchResults.size() == 0) {
            llNoResult.setVisibility(View.VISIBLE);
            searchRecyclerView.setVisibility(View.GONE);
        } else {
            llNoResult.setVisibility(View.GONE);
            searchRecyclerView.setVisibility(View.VISIBLE);
        }
        searchAutoCompleteAdapter.setSearchResults(searchResults);
        searchRecyclerView.setAdapter(searchAutoCompleteAdapter);
    }


}
