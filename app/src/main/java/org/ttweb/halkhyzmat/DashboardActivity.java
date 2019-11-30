package org.ttweb.halkhyzmat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // creating a CardView and assigning a value.
        CardView paymentBtn = findViewById(R.id.paymentBtn);
        CardView profileBtn = findViewById(R.id.profileBtn);

        String toolbarTitle;

        SharedPreferences sharedPref = getSharedPreferences(MainActivity.USER_DATA, MODE_PRIVATE);
        toolbarTitle = sharedPref.getString("USER_NAME", "Halk Hyzmatlary");

        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.collapsingToolBarLayout);

        toolbarLayout.setTitle(toolbarTitle);
        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        Toolbar mToolbar = findViewById(R.id.toolbarId);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_white_black_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePayment();
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfile();
            }
        });
    }

    /** Called when the user taps the payment button */
    private void makePayment(){
        Intent intent = new Intent(this, PaymentActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the meter button */
    private void showProfile(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }
}
