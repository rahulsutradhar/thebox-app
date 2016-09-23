package one.thebox.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;


import one.thebox.android.R;


/**
 * Created by Ruchit on 9/13/2016.
 */
public class UniversalSearchActivity extends Activity {


//    @BindView(R.id.imgsearchBack)
//    ImageView imgSearchBack;
//    @BindView(R.id.edt_search_query)
//    EditText edtSearchQuery;
//    @BindView(R.id.recycler_view_search)
//    RecyclerView searchRecyclerView;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_universal_search);

    }
}
