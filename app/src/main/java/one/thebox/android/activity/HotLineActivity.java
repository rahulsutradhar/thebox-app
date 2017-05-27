package one.thebox.android.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.freshdesk.hotline.FaqOptions;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import one.thebox.android.Models.User;
import one.thebox.android.R;
import one.thebox.android.util.PrefUtils;

/**
 * Created by ruchit.shah on 11/16/2016.
 */

public class HotLineActivity extends BaseActivity {


    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @BindView(R.id.txtContactSupport)
    TextView txtContactSupport;

    @BindView(R.id.txtFaq)
    TextView txtFaq;

    private User user;
    static final String TAG = "THEBOX";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hotline);
        ButterKnife.bind(this);
        setTitle("Talk to us");
        user = PrefUtils.getUser(this);

        HotlineUser hlUser=Hotline.getInstance(getApplicationContext()).getUser();

        hlUser.setName(user.getName());
        hlUser.setEmail(user.getEmail());
        hlUser.setExternalId(String.valueOf(user.getId()));
        hlUser.setPhone("+91", user.getPhoneNumber());

        Hotline.getInstance(getApplicationContext()).updateUser(hlUser);

//        Hotline.showConversations(getApplicationContext());

        txtContactSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Hotline.showConversations(getApplicationContext());
            }
        });

        txtFaq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FaqOptions faqOptions = new FaqOptions()
                        .showFaqCategoriesAsGrid(true)
                        .showContactUsOnAppBar(true)
                        .showContactUsOnFaqScreens(false)
                        .showContactUsOnFaqNotHelpful(false);
                Hotline.showFAQs(getApplicationContext(),faqOptions);
            }
        });


    }

}
