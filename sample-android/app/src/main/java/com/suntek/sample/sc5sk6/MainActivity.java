package com.suntek.sample.sc5sk6;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import com.sdkapi.api.SdkApi;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SdkApi.newInstance(getApplicationContext());
        TextView view = new TextView(this);
        view.setText("SUNTEK SC5 / SK6 SDK Ready");
        setContentView(view);
    }
}
