package org.ttweb.halkhyzmat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

    //Retrofit
    private Retrofit.Builder builder;
    private Retrofit retrofit;
    private UserClient userClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        builder = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        retrofit = builder.build();

        userClient = retrofit.create(UserClient.class);

        progressBar = findViewById(R.id.progressBar);
    }

    /** Called when the user taps the login button */
    public void login(View view){
        EditText loginText = findViewById(R.id.login);
        String login = loginText.getText().toString();

        EditText passwordText = findViewById(R.id.password);
        String password = passwordText.getText().toString();

        progressBar.setVisibility(ProgressBar.VISIBLE);
        if(!login.isEmpty() && !password.isEmpty()){
            Call<User> call = userClient.login(login, password);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull retrofit2.Response<User> response) {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    if (response.isSuccessful() && response.body().isStatus()) {
                        //Save User to shared pref file and open Dashboard Activity
                        saveUser(response.body());
                    }

                    if(!response.body().isStatus()){
                        Log.e("LOGIN", "Wrong login data or user does not exist!");
                        Toast.makeText(MainActivity.this,"Siz awtorizasýa bolup bilmediňiz. Täzeden barlamagyňyzy soraýarys!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                    Log.e("LOGIN", t.getMessage());
                    Toast.makeText(MainActivity.this,"Ýalňyşlyk: Serwere baglanyp bolmady!", Toast.LENGTH_SHORT).show();

                }
            });
        }else{
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            Toast.makeText(MainActivity.this,"Login ýa-da açarsözüňizi ýazyň!", Toast.LENGTH_SHORT).show();
        }
    }

    /** Save user data to shared preferences file and starts Dashboard Activity */
    private void saveUser(User user){
        Log.e("USER", "session_id: " + user.getSessionId());
        Log.e("USER", "sess: " + user.getBitrixSessid());
        Log.e("USER", "h/h: " + user.getXmlId());

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

        Log.i("LOGIN", "User data has been saved!");

        //Go to Dashboard activity
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra(Intent.EXTRA_TITLE, user.getFullName());
        startActivity(intent);
    }
}
