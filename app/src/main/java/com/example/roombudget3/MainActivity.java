package com.example.roombudget3;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.tapadoo.alerter.Alerter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    AlertDialog dialogg1;
    AlertDialog.Builder mBuilder1;
    String version_name;
    String actual_version;
    String update_description;
    String last_updated;
    TextView update_notification, drawer_version;
    Button storage_btn;
    LinearLayout ll, ll3;
    TextView tv;
    String val = "open";
    LottieAnimationView anm2;
    NavigationView navigationView;
    private DrawerLayout drawer;
    private long pressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBuilder1 = new AlertDialog.Builder(this);
        View mview1 = getLayoutInflater().inflate(R.layout.custom_loading, null);
        mBuilder1.setView(mview1);
        dialogg1 = mBuilder1.create();

        update_notification = findViewById(R.id.update_btn_notification);
        drawer_version = findViewById(R.id.drawer_version);
        storage_btn = findViewById(R.id.storage_btn);
        ll = findViewById(R.id.storage_layout);
        ll3 = findViewById(R.id.ll3);
        anm2 = findViewById(R.id.lottie_animation_2);
        anm2.setAnimation(R.raw.notification);
        anm2.playAnimation();



        saveData();
        update_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                update("two");

            }
        });


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.nav_add_data:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddDataFragment()).commit();
                        break;
                    case R.id.nav_individual_data:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GetDataFragment()).commit();
                        break;
                    case R.id.nav_all_data:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GetAllDataFragment()).commit();
                        break;
                    case R.id.nav_delete_data:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DeleteDataFragment()).commit();
                        break;
                    case R.id.nav_charts:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ChartsFragment()).commit();
                        break;
                    case R.id.nav_developer:
                        saveData();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DeveloperFragment()).commit();
                        break;
                    case R.id.nav_report_bugs:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ReportBugsFragment()).commit();
                        break;

                    //Below are Action Triggering Menu items

                    case R.id.nav_relaunch:
                        relaunch();
                        break;
                    case R.id.nav_update:
                        update("two");
                        break;
                    case R.id.nav_about:
                        about();
                        break;


                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer_version.setText(getResources().getString(R.string.version));
        version_name = getResources().getString(R.string.version);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DefaultFragment()).commit();
        }






        getUpdateDescription();
        getVersion();


    }



    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else {
            if (pressedTime + 2000 > System.currentTimeMillis()) {
                super.onBackPressed();
                finish();
            } else {
                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
            }
            pressedTime = System.currentTimeMillis();
        }
    }

    private void saveData() {

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && (grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Storage Permissions Granted", Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    private void getVersion() {

        String param = "?action=getVersion";
        String url = getResources().getString(R.string.spreadsheet_url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + param,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItems(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResposnce) {


        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                version_name = jo.getString("Version");
                update("one");


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void update(String k) {

        if (k == "one") {

            //MainActivity Update button show Event
            actual_version = getResources().getString(R.string.version);

            float temp1 = Float.parseFloat(version_name);
            float temp2 = Float.parseFloat(actual_version);

            if (temp2 < temp1) {

                alerter(version_name);
                LayoutInflater li = LayoutInflater.from(MainActivity.this);
                tv = (TextView) li.inflate(R.layout.update_notification_badge, null);
                navigationView.getMenu().findItem(R.id.nav_update).setActionView(tv);


            }

        } else if (k == "two") {

            //Menu update button clicked Event

            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            View mview = getLayoutInflater().inflate(R.layout.update2, null);
            mBuilder.setView(mview);
            final AlertDialog dialogg = mBuilder.create();


            ImageButton show_hide = mview.findViewById(R.id.show_hide_id);
            LinearLayout ll6 = mview.findViewById(R.id.ll6);
            Button update_btn_u = (Button) mview.findViewById(R.id.update_btn);
            TextView title_u = (TextView) mview.findViewById(R.id.update_title);
            TextView latest_version_u = (TextView) mview.findViewById(R.id.update_version);
            TextView last_updated_u = (TextView) mview.findViewById(R.id.last_updated_id);
            TextView update_description_u = (TextView) mview.findViewById(R.id.update_description);
            RelativeLayout relativeLayout = mview.findViewById(R.id.relative123);



            actual_version = getResources().getString(R.string.version);
            float temp1 = Float.parseFloat(version_name);
            float temp2 = Float.parseFloat(actual_version);

            if (temp2 < temp1) {
                latest_version_u.setText(version_name);
                last_updated_u.setText(last_updated);
                update_description_u.setText(update_description);
                dialogg.setCanceledOnTouchOutside(false);
            } else {
                title_u.setVisibility(View.VISIBLE);
                dialogg.setCanceledOnTouchOutside(true);
                relativeLayout.setVisibility(View.GONE);
            }

            show_hide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(val == "open"){
                        ll6.setVisibility(View.VISIBLE);
                        show_hide.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_up));
                        val = "close";
                    } else {
                        ll6.setVisibility(View.GONE);
                        show_hide.setImageDrawable(getDrawable(R.drawable.ic_keyboard_arrow_down));
                        val = "open";
                    }
                }
            });

            update_btn_u.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://drive.google.com/file/d/1a3AL9xh0QUt6qGMDFP5BtLAV4x3DGNJh/view?usp=sharing";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });
            dialogg.show();

        }

    }

    private void relaunch() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void about() {

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mview = getLayoutInflater().inflate(R.layout.about, null);
        TextView tv = mview.findViewById(R.id.app_version);
        tv.setText(getResources().getString(R.string.version));
        mBuilder.setView(mview);
        final AlertDialog dialogg = mBuilder.create();

        dialogg.setCanceledOnTouchOutside(true);
        dialogg.show();

    }

    public void alerter(String temp1){

        Alerter.create(this)
                .setTitle("New Update Available")
                .setText("This application is not UP-TO_DATE\n" +
                        "Update to the latest version\n" +
                        temp1)
                .setIcon(R.drawable.ic_bell)
                .setIconColorFilter(getResources().getColor(R.color.bell_color))
                .setBackgroundColorRes(R.color.notify_bg_color)
                .setDuration(4000)
                .enableProgress(true)
                .setProgressColorRes(R.color.white_700)
                .enableSwipeToDismiss()
                .show();


    }

    private void getUpdateDescription() {

        String params = "?action=getUpdateDescription";
        String url = getResources().getString(R.string.spreadsheet_url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseUD(response);
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        int socketTimeOut = 50000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

        stringRequest.setRetryPolicy(policy);

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);

    }

    private void parseUD(String jsonResposnce) {


        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                update_description = jo.getString("UpdateDescription");
                last_updated = jo.getString("LastUpdated");


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}