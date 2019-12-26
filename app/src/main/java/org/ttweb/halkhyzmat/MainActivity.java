package org.ttweb.halkhyzmat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import org.ttweb.halkhyzmat.model.User;
import org.ttweb.halkhyzmat.service.UserClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public final static String BASE_URL = "https://toleg.halkbank.gov.tm";
    public final static String USER_DATA = "userData";

    private ProgressBar progressBar;
    private View loginView;

    private UserClient userClient;

    private Call<User> call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginView = findViewById(R.id.loginView);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        userClient = retrofit.create(UserClient.class);

        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onStop() {
        super.onStop();

        if(call != null && call.isExecuted()){
            call.cancel();
        }

        progressBar.setVisibility(ProgressBar.GONE);
    }

    /** Called when the user taps the login button */
    public void login(View view){
        EditText loginText = findViewById(R.id.login);
        String login = loginText.getText().toString();

        EditText passwordText = findViewById(R.id.password);
        String password = passwordText.getText().toString();

        if(!login.isEmpty() && !password.isEmpty()) {
            loginRequest(login, password);
        }else {
            Snackbar.make(view, R.string.login_data_required, Snackbar.LENGTH_LONG).show();
        }
    }

    private void loginRequest(String login, String password){
        progressBar.setVisibility(ProgressBar.VISIBLE);

        call = userClient.login(login, password);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull retrofit2.Response<User> response) {
                progressBar.setVisibility(ProgressBar.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().isStatus()) {
                    //Save User to shared pref file and open Dashboard Activity
                    saveUser(response.body());
                }else{
                    Snackbar.make(loginView, R.string.login_data_incorrect, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                progressBar.setVisibility(ProgressBar.GONE);
                Snackbar.make(loginView, R.string.connection_failed, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    /** Save user data to shared preferences file and starts Dashboard Activity */
    private void saveUser(User user){

        SharedPreferences sharedPref = getSharedPreferences(USER_DATA, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("LOGIN", user.getLogin());
        editor.putString("USER_ID", user.getUserId());
        editor.putString("BX_USER_ID", user.getHashedUserId());
        editor.putString("BX_UIDH", user.getBitrixUidh());
        editor.putString("SESSION_ID", user.getSessionId());
        editor.putString("SESSID", user.getBitrixSessid());
        editor.putString("USER_NAME", user.getFullName());
        editor.putString("XML_ID", user.getXmlId());
        editor.putString("ADDRESS_FULL", user.getFullAddress());
        editor.putString("REGION", user.getRegion());
        editor.putString("CITY", user.getCity());
        editor.putString("SETTLEMENT", user.getSettlement());
        editor.putString("DISTRICT", user.getDistrict());
        editor.putString("STREET", user.getStreet());
        editor.putString("HOUSE", user.getHouse());
        editor.putString("FLAT", user.getFlat());

        editor.apply();

        //Go to Dashboard activity
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra(Intent.EXTRA_TITLE, user.getFullName());
        startActivity(intent);
    }
}
