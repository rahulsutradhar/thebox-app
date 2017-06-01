package one.thebox.android.api.Responses.cart;

import java.io.Serializable;

import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 01/06/17.
 */

public class CartItemResponse extends ApiResponse implements Serializable {

    private boolean merge;
    
    public boolean isMerge() {
        return merge;
    }

    public void setMerge(boolean merge) {
        this.merge = merge;
    }
}

