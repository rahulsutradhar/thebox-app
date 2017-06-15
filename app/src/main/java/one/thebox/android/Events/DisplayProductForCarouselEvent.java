package one.thebox.android.Events;

import one.thebox.android.Models.carousel.Offer;

/**
 * Created by developers on 07/06/17.
 */

public class DisplayProductForCarouselEvent {

    private Offer offer;

    public DisplayProductForCarouselEvent(Offer offer) {
        this.offer = offer;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }
}
