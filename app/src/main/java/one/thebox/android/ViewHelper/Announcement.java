package one.thebox.android.ViewHelper;

import android.content.Context;
import android.support.v7.app.AlertDialog;

import one.thebox.android.R;

/**
 * Created by vaibhav on 13/08/16.
 */
public class Announcement extends AlertDialog.Builder{

    private Context context;
    private static final int TYPE_IMPORTANT = 0;
    private static final int TYPE_NUDGE = 1;
    private int type;
    public int positive_button_text_res_id;
    public int negativeText_button_text_res_id;


    public Announcement(Context context, int type) {
        super(context,R.style.AnnouncementTheme);
        this.type = type;
        switch (type) {
            case TYPE_IMPORTANT: {
                this.positive_button_text_res_id = R.string.useritem_quantity_change_from_box_agree_button;
                this.negativeText_button_text_res_id = R.string.useritem_quantity_change_from_box_disagree_button;
            }
            case TYPE_NUDGE: {

            }
        }
    }

    public int getPositive_button_text_res_id() {return positive_button_text_res_id;}
    public int getNegativeText_button_text_res_id() {return negativeText_button_text_res_id;}
    public int getType() {return type;}


    public void build_it() {

        switch (type) {
            case TYPE_IMPORTANT: {

                this.setTitle(R.string.useritem_quantity_change_from_box_title).setIcon(R.drawable.megaphone)
                .setMessage(R.string.useritem_quantity_change_from_box_content);
            }
            case TYPE_NUDGE: {

            }
        }
    }
}
