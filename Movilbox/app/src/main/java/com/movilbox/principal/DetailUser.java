package com.movilbox.principal;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.movilbox.utilities.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class DetailUser extends AppCompatActivity {

    private String user;
    private Utils utils;
    private boolean haveInternet = true;
    private boolean peticionActiva = false;
    private Typeface fredoka, monserratSemiBold, monserratBold, monserratExtraBold;

    TextView textTitle, textCardName, textCardValorName, textCardUsername, textCardValorUsername, textCardEmail, textCardValorEmail,
            textCardPhone, textCardValorPhone, textCardWebsite, textCardValorWebsite, textPogressInfoUser;
    ImageView imgBack;
    LinearLayout contentBodyInfoUser, contentPogressInfoUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_user);

        textTitle = (TextView) findViewById(R.id.textTitle);
        textCardName = (TextView) findViewById(R.id.textCardName);
        textCardValorName = (TextView) findViewById(R.id.textCardValorName);
        textCardUsername = (TextView) findViewById(R.id.textCardUsername);
        textCardValorUsername = (TextView) findViewById(R.id.textCardValorUsername);
        textCardEmail = (TextView) findViewById(R.id.textCardEmail);
        textCardValorEmail = (TextView) findViewById(R.id.textCardValorEmail);
        textCardPhone = (TextView) findViewById(R.id.textCardPhone);
        textCardValorPhone = (TextView) findViewById(R.id.textCardValorPhone);
        textCardWebsite = (TextView) findViewById(R.id.textCardWebsite);
        textCardValorWebsite = (TextView) findViewById(R.id.textCardValorWebsite);
        textPogressInfoUser = (TextView) findViewById(R.id.textPogressInfoUser);

        contentBodyInfoUser = (LinearLayout) findViewById(R.id.contentBodyInfoUser);
        contentPogressInfoUser = (LinearLayout) findViewById(R.id.contentPogressInfoUser);

        imgBack = (ImageView) findViewById(R.id.imgBack);

        fredoka = Typeface.createFromAsset(getAssets(), "fonts/FredokaOne-Regular.ttf");
        monserratSemiBold = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-SemiBold.ttf");
        monserratBold = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Bold.ttf");
        monserratExtraBold = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-ExtraBold.ttf");

        utils = new Utils();
        utils.init(this);

        initData();
        initStyle();
        navigation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(0, 0);
    }

    private void initStyle() {

        textTitle.setTypeface(monserratExtraBold);
        textCardName.setTypeface(monserratSemiBold);
        textCardValorName.setTypeface(monserratSemiBold);
        textCardUsername.setTypeface(monserratSemiBold);
        textCardValorUsername.setTypeface(monserratSemiBold);
        textCardEmail.setTypeface(monserratSemiBold);
        textCardValorEmail.setTypeface(monserratSemiBold);
        textCardPhone.setTypeface(monserratSemiBold);
        textCardValorPhone.setTypeface(monserratSemiBold);
        textCardWebsite.setTypeface(monserratSemiBold);
        textCardValorWebsite.setTypeface(monserratSemiBold);
        textPogressInfoUser.setTypeface(monserratBold);
    }

    private void navigation() {

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
    }

    private void initData() {

        user = getIntent().getExtras().getString("Data");

        buscarUser();
    }

    public void buscarUser() {

        haveInternet = utils.haveInternet();

        if (!peticionActiva && haveInternet) {

            peticionActiva = true;

            contentPogressInfoUser.setVisibility(View.VISIBLE);

            RequestQueue peticion = Volley.newRequestQueue(DetailUser.this);

            String URL = utils.getURLApi() + "users/" + user;

            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            execBuscarUser(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            contentPogressInfoUser.setVisibility(View.GONE);

                            System.out.println(error);
                        }
                    });

            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            peticion.add(jsonRequest);
        }
    }

    public void execBuscarUser(JSONObject dataUser) {

        try {

            if (dataUser != null) {

                textCardValorName.setText(dataUser.getString("name"));
                textCardValorUsername.setText(dataUser.getString("username"));
                textCardValorEmail.setText(dataUser.getString("email"));
                textCardValorPhone.setText(dataUser.getString("phone"));
                textCardValorWebsite.setText(dataUser.getString("website"));
            }

            contentBodyInfoUser.setVisibility(View.VISIBLE);

            peticionActiva = false;

            contentPogressInfoUser.setVisibility(View.GONE);

        } catch (JSONException e) {
            System.out.println("err in JSON DetallePrestamo-execBuscarDeuda");
        }
    }
}
