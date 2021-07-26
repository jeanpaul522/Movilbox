package com.movilbox.principal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.movilbox.utilities.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListPosts extends AppCompatActivity {

    private Context context;
    private Utils utils;
    private int pageBusqueda = 1, countPosts = 100;
    private boolean haveInternet = true;
    private boolean peticionActiva = false;
    private Typeface fredoka, monserratSemiBold, monserratBold, monserratExtraBold;

    TextView textTitle, textFiltro, textInformacion, textPogressPosts;
    Button buttonRefresh, buttonDelete;
    LinearLayout contentFiltro, contentScrollOptions, contentBodyPosts, contentPogressPosts, contentFooter;
    ScrollView contentPosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_posts);

        textTitle = (TextView) findViewById(R.id.textTitle);
        textFiltro = (TextView) findViewById(R.id.textFiltro);
        textInformacion = (TextView) findViewById(R.id.textInformacion);
        textPogressPosts = (TextView) findViewById(R.id.textPogressPosts);

        buttonRefresh = (Button) findViewById(R.id.buttonRefresh);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);

        contentFiltro = (LinearLayout) findViewById(R.id.contentFiltro);
        contentScrollOptions = (LinearLayout) findViewById(R.id.contentScrollOptions);
        contentBodyPosts = (LinearLayout) findViewById(R.id.contentBodyPosts);
        contentPogressPosts = (LinearLayout) findViewById(R.id.contentPogressPosts);

        contentPosts = (ScrollView) findViewById(R.id.contentPosts);

        fredoka = Typeface.createFromAsset(getAssets(), "fonts/FredokaOne-Regular.ttf");
        monserratSemiBold = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-SemiBold.ttf");
        monserratBold = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Bold.ttf");
        monserratExtraBold = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-ExtraBold.ttf");

        context = this;

        utils = new Utils();
        utils.init(this);

        initData();
        initStyle();
        navigation();
    }

    private void initStyle() {

        textTitle.setTypeface(monserratExtraBold);
        textFiltro.setTypeface(fredoka);
        textInformacion.setTypeface(monserratSemiBold);
        textPogressPosts.setTypeface(monserratBold);

        buttonRefresh.setTypeface(monserratExtraBold);
        buttonDelete.setTypeface(monserratExtraBold);
    }

    private void navigation() {

        contentFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Funcionalidad para hacer el iltro de favoritos
            }
        });

        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                refreshPosts();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteAllPosts();
            }
        });

        contentPosts.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {

                int scrollTop = contentPosts.getScrollY();
                int HeightMin = contentPosts.getHeight();
                int HeightMax = contentScrollOptions.getHeight();

                if ((scrollTop + HeightMin) == HeightMax) {

                    int countByPage = pageBusqueda * 10;

                    if (countByPage <= countPosts) {

                        listaPosts();
                    }
                }
            }
        });
    }

    private void initData() {

        listaPosts();
    }

    private void refreshPosts() {

        pageBusqueda = 1;

        contentBodyPosts.removeAllViews();

        listaPosts();
    }

    private void deleteAllPosts() {

        pageBusqueda = 1;

        contentBodyPosts.removeAllViews();
    }

    private void listaPosts() {

        haveInternet = utils.haveInternet();

        if (!peticionActiva && haveInternet) {

            peticionActiva = true;

            contentPogressPosts.setVisibility(View.VISIBLE);

            String page = "_page=" + pageBusqueda;
            String limit = "_limit=" + 10;

            RequestQueue peticion = Volley.newRequestQueue(ListPosts.this);

            String URL = utils.getURLApi() + "posts?" + page + "&" + limit;

            JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {

                            execListaPosts(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                            peticionActiva = false;

                            contentPogressPosts.setVisibility(View.GONE);

                            System.out.println(error);
                        }
                    });

            jsonRequest.setRetryPolicy(new DefaultRetryPolicy(15000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            peticion.add(jsonRequest);
        }
    }

    private void execListaPosts(JSONArray dataPosts) {

        try {

            if (dataPosts.length() != 0) {

                View inflatePosts;
                TextView textTitle, textDetalle;
                LinearLayout contentFavorite, contentDelete, contentLeido;

                for (int i = 0; i < dataPosts.length(); i++) {

                    JSONObject objectPosts = dataPosts.getJSONObject(i);

                    inflatePosts = getLayoutInflater().inflate(R.layout.inflate_posts, null);
                    contentBodyPosts.addView(inflatePosts);

                    textTitle = inflatePosts.findViewById(R.id.textCardTitle);
                    textDetalle = inflatePosts.findViewById(R.id.textCardMensaje);

                    contentFavorite = inflatePosts.findViewById(R.id.contentFavorite);
                    contentDelete = inflatePosts.findViewById(R.id.contentDelete);
                    contentLeido = inflatePosts.findViewById(R.id.contentLeido);

                    inflatePosts.setTag(objectPosts.getInt("userId"));

                    contentFavorite.setTag(objectPosts.getInt("userId"));
                    contentDelete.setTag(inflatePosts);
                    contentLeido.setTag(objectPosts.getInt("userId"));

                    textTitle.setTypeface(monserratBold);
                    textDetalle.setTypeface(monserratSemiBold);

                    textTitle.setText(objectPosts.getString("title"));
                    textDetalle.setText(objectPosts.getString("body"));

                    inflatePosts.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent activityUser = new Intent(ListPosts.this, DetailUser.class);
                            activityUser.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            activityUser.putExtra("Data", view.getTag().toString());
                            startActivity(activityUser);
                        }
                    });

                    contentFavorite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            int colorGeneral = context.getResources().getColor(R.color.grisClaro);

                            Drawable drawableMute = ContextCompat.getDrawable(context.getApplicationContext(), R.drawable.fondo_option_favorite);
                            drawableMute.mutate().setColorFilter(colorGeneral, PorterDuff.Mode.SRC_ATOP);

                            view.setBackground(drawableMute);
                        }
                    });

                    contentDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            System.out.println("Delete");
                        }
                    });

                    contentLeido.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            System.out.println("leido");
                        }
                    });
                }

                pageBusqueda += 1;

            } else {
                textInformacion.setVisibility(View.VISIBLE);
            }

            peticionActiva = false;

            contentPogressPosts.setVisibility(View.GONE);

        } catch (JSONException e) {
            System.out.println("err in JSON ListPosts - execListaPosts");
        }
    }
}
