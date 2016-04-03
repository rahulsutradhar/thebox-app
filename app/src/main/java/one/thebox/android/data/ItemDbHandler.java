package one.thebox.android.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import one.thebox.android.data.model.BaseModel;
import one.thebox.android.data.model.Item;

/**
 * Created by harsh on 11/12/15.
 */
public class ItemDbHandler extends BaseDbHandler {
    private final String TAG = ItemDbHandler.class.getSimpleName();

    public static final String TABLE_NAME = "Item";

    private static ItemDbHandler singleton;
    private Context context;

    // Column names
    public static final String COL_TEXT = "text";

    public static final String[] FIELDS = {
            COL_ID,
            COL_TEXT,
            COL_CREATED_AT,
            COL_UPDATED_AT
    };

    // Create table statement
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COL_TEXT + " TEXT DEFAULT '',"
                    + CREATE_BASE_FIELDS
                    + ")";

    public ItemDbHandler(Context context) {
        this.context = context;
    }

    public static ItemDbHandler getInstance(Context context) {
        if (singleton == null)
            singleton = new ItemDbHandler(context);

        return singleton;
    }

    @Override
    Uri buildUri(long id) {
        return AppContentProvider.URI_ITEM.buildUpon().appendPath(id + "").build();
    }

    @Override
    public Item get(long id) {
        Cursor cursor = context.getContentResolver().query(
                buildUri(id), null, null, null, null
        );

        Item item = null;

        if ((cursor.moveToFirst()) && cursor.getCount() != 0) {
            //cursor is not empty
            item = new Item(cursor);
        }

        cursor.close();
        return item;
    }

    @Override
    public void insert(BaseModel model) {
        context.getContentResolver().insert(
                AppContentProvider.URI_ITEM, model.getContent(true)
        );
    }

    @Override
    public void update(BaseModel model) {
        context.getContentResolver().update(
                buildUri(model.getId()), model.getContent(false), null, null
        );
    }

    @Override
    public void delete(BaseModel model) {
        context.getContentResolver().delete(
                AppContentProvider.URI_ITEM,
                COL_ID + " = " + model.getId(),
                null
        );
    }

    public void insertOrUpdate(BaseModel model) {
        Item item = get(model.getId());

        if (item != null) {
            update(model);
        }
    }

    public Cursor getAll() {
        return context.getContentResolver().query(
                AppContentProvider.URI_ITEM, FIELDS, null, null, null
        );
    }

    public Loader<Cursor> getAllCursorLoader() {
        return new CursorLoader(
                context,
                AppContentProvider.URI_ITEM,
                FIELDS,
                null,
                null,
                null
        );
    }
}
