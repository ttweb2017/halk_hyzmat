package org.ttweb.halkhyzmat;

import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPref = getSharedPreferences(MainActivity.USER_DATA, MODE_PRIVATE);

        String userXmlId = sharedPref.getString("XML_ID", "");
        String street = sharedPref.getString("STREET", "");
        String house = sharedPref.getString("HOUSE", "");
        String flat = sharedPref.getString("FLAT", "");

        String toolbarTitle;
        CollapsingToolbarLayout toolbarLayout = findViewById(R.id.profileCollapsingToolBarLayout);

        toolbarTitle = sharedPref.getString("USER_NAME", "");

        toolbarLayout.setTitle(toolbarTitle);
        toolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        toolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        Toolbar mToolbar = findViewById(R.id.profileToolbarId);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_white_black_24dp);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView xmlId = findViewById(R.id.xmlId);
        TextView userStreet = findViewById(R.id.userStreet);
        TextView userHouse = findViewById(R.id.userHouse);
        TextView userFlat = findViewById(R.id.userFlat);

        xmlId.setText(userXmlId);
        userStreet.setText(street);
        userHouse.setText(house);
        userFlat.setText(flat);
    }
}
