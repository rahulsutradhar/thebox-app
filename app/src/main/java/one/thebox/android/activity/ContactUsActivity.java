package one.thebox.android.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import one.thebox.android.R;

public class ContactUsActivity extends BaseActivity implements View.OnClickListener{
    public CardView cvsupport;
    public CardView cvcall;
    public TextView reach_us_at;
    public TextView support;
    public CardView cvmessage;
    public Typeface type;
    public final String MOBILE_NUMBER = "8408995347";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        initViews();
        setTitle("Contact Us");
        cvsupport.setOnClickListener(this);
        cvcall.setOnClickListener(this);
        cvmessage.setOnClickListener(this);

    }

    private void initViews() {
        cvsupport = (CardView) findViewById(R.id.cvsupport);
        cvcall = (CardView) findViewById(R.id.cvcall);
        cvmessage = (CardView) findViewById(R.id.cvmessage);
        reach_us_at=(TextView) findViewById(R.id.tv_reach_us_at);
        support=(TextView) findViewById(R.id.tv_reach_us_at);
        type=Typeface.createFromAsset(getAssets(),"fonts/AvenirLTStd-Book.otf");
        reach_us_at.setTypeface(type);
        reach_us_at.setTypeface(type);

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.cvsupport: {
                emailIntent();
                break;
            }
            case R.id.cvcall:
                callIntent();
                break;
            case R.id.cvmessage:
                messageIntent();
                break;
        }
    }

    private void messageIntent() {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
        if (launchIntent != null) {
            startActivity(launchIntent);//null pointer check in case package name was not found
        }
        else{
            Toast.makeText(this,
                    "Please install facebook messenger to continue.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void emailIntent() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto: support@thebox.one"));
        startActivity(Intent.createChooser(emailIntent, "Reach us via email"));
    }

    private void callIntent() {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + MOBILE_NUMBER));
        startActivity(intent);
    }

}
