package one.thebox.android.api;

import android.content.ContentValues;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import one.thebox.android.app.EndPoints;
import one.thebox.android.app.MyApplication;
import one.thebox.android.data.AppContentProvider;
import one.thebox.android.data.model.Item;

/**
 * Created by harsh on 13/12/15.
 */
public class ListApi extends BaseApi {
    private String TAG = ListApi.class.getSimpleName();

    private interface KEYS {
        final String DATA = "data";
        final String ID = "id";
        final String TEXT = "text";
    }

    public ListApi() {
    }

    @Override
    JSONObject createPayload() {
        return null;
    }

    @Override
    public void call() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                EndPoints.LIST_API,
                createPayload(),
                this,
                this
        );

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(jsonObjectRequest, TAG);
    }

    @Override
    void parse(JSONObject respJsonObject) {
        try {
            JSONArray dataArray = respJsonObject.getJSONArray(KEYS.DATA);
            ContentValues contentValues[] = new ContentValues[dataArray.length()];
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject data = dataArray.getJSONObject(i);

                Item item = new Item();
                item.setId(data.getLong(KEYS.ID));
                item.setText(data.getString(KEYS.TEXT));

                contentValues[i] = item.getContent(true);
            }

            MyApplication.getInstance().getContentResolver().bulkInsert(
                    AppContentProvider.URI_ITEM,
                    contentValues
            );

            if (apiListener != null)
                apiListener.onParsedData(null);

        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception", e);
        }
    }
}
