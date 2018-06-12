package one.thebox.android.api.Responses;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmList;
import one.thebox.android.Models.carousel.Offer;
import one.thebox.android.api.ApiResponse;

/**
 * Created by developers on 28/03/17.
 */

public class CarouselApiResponse extends ApiResponse implements Serializable {

    private ArrayList<Offer> offers;

    public ArrayList<Offer> getOffers() {
        return offers;
    }

    public void setOffers(ArrayList<Offer> offers) {
        this.offers = offers;
    }
}
