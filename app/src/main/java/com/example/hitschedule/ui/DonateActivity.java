package com.example.hitschedule.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.hitschedule.R;
import com.example.hitschedule.util.AlipayDonate;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DonateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        Button donate = findViewById(R.id.donate);
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean hasInstalledAlipayClient = AlipayDonate.hasInstalledAlipayClient(DonateActivity.this);
                if (hasInstalledAlipayClient) {
                    AlipayDonate.startAlipayClient(DonateActivity.this, "FKX011837PJ6RFCW2TDX49");
                }
            }
        });
    }
}
