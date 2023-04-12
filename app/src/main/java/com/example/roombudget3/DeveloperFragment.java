package com.example.roombudget3;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class DeveloperFragment extends Fragment {

    private static final String PERSONS_FILE_NAME = "persons.txt";
    private static final String MONTHS_FILE_NAME = "months.txt";
    StorageMethods storageMethods = new StorageMethods();
    AlertDialog.Builder mBuilder1;
    AlertDialog dialogg1;
    String[] persons = {};
    String[] months = {};
    String[] months1 = {};
    Button preview_reload_btn, month_save_btn, timer_textview;
    TextView person_preview, month_preview, version_textview, running_textview;
    EditText month_entry;
    String default_path = Environment.getExternalStorageDirectory() + "/Android/data/com.android.room_budget/files/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_developer, container, false);

        mBuilder1 = new AlertDialog.Builder(getContext());
        View mview1 = getLayoutInflater().inflate(R.layout.custom_loading, null);
        mBuilder1.setView(mview1);
        dialogg1 = mBuilder1.create();


        person_preview = v.findViewById(R.id.persons_preview_id);
        month_preview = v.findViewById(R.id.months_preview_id);
        version_textview = v.findViewById(R.id.version_textview_id);
        timer_textview = v.findViewById(R.id.timer_textview);
        month_entry = v.findViewById(R.id.month_entry_id);
        preview_reload_btn = v.findViewById(R.id.preview_reload_btn_id);
        month_save_btn = v.findViewById(R.id.month_save_btn);
        running_textview = v.findViewById(R.id.running_month_id);


        person_preview.setMovementMethod(new ScrollingMovementMethod());
        month_preview.setMovementMethod(new ScrollingMovementMethod());

        version_textview.setText(getResources().getString(R.string.version));

        month_save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (month_entry.getText().toString().trim().length() == 0) {
                    month_entry.setError("Month should not be empty");
                } else {
                    validate("month");
                }


            }
        });

        preview_reload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                storageMethods.deleteFile(PERSONS_FILE_NAME);
                storageMethods.deleteFile(MONTHS_FILE_NAME);
                regularCheck();
                countDownTimer(10000);
                person_preview.setText("");
                month_preview.setText("");


            }
        });

        readFile(PERSONS_FILE_NAME);
        readFile(MONTHS_FILE_NAME);
        getNoofMonths1();


        return v;
    }

    public void validate(String type) {

        final AlertDialog.Builder mBuilder123 = new AlertDialog.Builder(getActivity());
        View view123 = getLayoutInflater().inflate(R.layout.key_authentication, null);
        EditText entry_key1 = view123.findViewById(R.id.key_entry_id);
        Button validate = view123.findViewById(R.id.validate_btn);
        LottieAnimationView lottieAnimationView = view123.findViewById(R.id.lottie_animation_3);
        LottieAnimationView lottieAnimationView2 = view123.findViewById(R.id.lottie_animation_4);
        LinearLayout ll4 = view123.findViewById(R.id.ll4);
        lottieAnimationView2.setMinAndMaxProgress(0.5f, 1f);
        lottieAnimationView2.setSpeed(0.5f);
        mBuilder123.setView(view123);
        final AlertDialog dialog123 = mBuilder123.create();

        dialog123.setCanceledOnTouchOutside(false);

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String entry_key = entry_key1.getText().toString().trim();
                String original_key1 = getResources().getString(R.string.original_key1).trim();
                String original_key2 = getResources().getString(R.string.original_key2).trim();


                if (entry_key.equals(original_key1) || entry_key.equals(original_key2)) {
                    lottieAnimationView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.move_down));
                    entry_key1.setBackgroundColor(getResources().getColor(R.color.green));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            lottieAnimationView2.setVisibility(View.VISIBLE);
                            ll4.setVisibility(View.GONE);

                        }
                    }, 700);
                    lottieAnimationView2.setMinAndMaxProgress(0.0f, 0.5f);
                    lottieAnimationView2.playAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog123.dismiss();
                            if (type == "person") {
                                //personSave();
                            } else if (type == "month") {
                                monthSave();
                            }
                        }
                    }, 2200);


                } else {
                    //else part
                    entry_key1.setError("invalid key");

                }

            }
        });

        dialog123.show();
        lottieAnimationView2.playAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lottieAnimationView2.setVisibility(View.GONE);
                ll4.setVisibility(View.VISIBLE);
                lottieAnimationView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.move_up));
            }
        }, 1700);


    }

    public void regularCheck() {

        //check for storage directories
        if (storageMethods.checkDirectories()) {

            //check for 2 files
            if (storageMethods.checkFile(PERSONS_FILE_NAME) && storageMethods.checkFile(MONTHS_FILE_NAME)) {

                //check for does 2 files have content or not
                String person_content_result = storageMethods.readFile(PERSONS_FILE_NAME).trim();
                String month_content_result = storageMethods.readFile(MONTHS_FILE_NAME).trim();


                if (person_content_result.length() != 0 && month_content_result.length() != 0) {


                    readFile(PERSONS_FILE_NAME);
                    readFile(MONTHS_FILE_NAME);


                }

            } else {

                getData();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        storageMethods.createFile(PERSONS_FILE_NAME, persons);
                        storageMethods.createFile(MONTHS_FILE_NAME, months);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                regularCheck();

                            }
                        }, 3000);


                    }
                }, 7000);


            }
        } else {

            //create directories and recall for check
            storageMethods.createDirectories();
            regularCheck();
        }


    }

    private void readFile(String filename) {


        File file = new File(default_path, filename);
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        printFiles(filename, stringBuilder);

    }

    private void printFiles(String filename, StringBuilder stringBuilder) {

        if (filename == PERSONS_FILE_NAME) {
            person_preview.setText(stringBuilder.toString());
        } else if (filename == MONTHS_FILE_NAME) {
            month_preview.setText(stringBuilder.toString());

        }


    }

    private void monthSave() {

        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View view1 = getLayoutInflater().inflate(R.layout.confirmation_dialog, null);
        LinearLayout ll1 = view1.findViewById(R.id.ll1);
        ll1.setVisibility(View.GONE);
        LottieAnimationView anm1 = (LottieAnimationView) view1.findViewById(R.id.lottie_animation_1);
        anm1.setVisibility(View.VISIBLE);
        anm1.setAnimation(R.raw.meditation_wait_please);
        anm1.playAnimation();
        mBuilder.setView(view1);
        final AlertDialog dialog1 = mBuilder.create();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();


        String month = month_entry.getText().toString().trim();

        String url = getResources().getString(R.string.spreadsheet_url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        anm1.setAnimation(R.raw.done);
                        anm1.playAnimation();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog1.dismiss();
                            }
                        }, 5000);

                        month_entry.setText("");

                        Toast.makeText(getActivity().getApplicationContext(), response, Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        anm1.setAnimation(R.raw.error);
                        anm1.playAnimation();

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "addMonth");
                parmas.put("month", month);

                return parmas;
            }
        };


        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(stringRequest);


    }


    private void getNoofMonths() {


        dialogg1.show();
        dialogg1.setCanceledOnTouchOutside(false);

        String param = "?action=getMonth";
        String url = getResources().getString(R.string.spreadsheet_url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + param,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItemsofMonths(response);
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
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);

    }

    private void parseItemsofMonths(String jsonResposnce) {


        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");

            months = new String[(0)];


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String months_123 = jo.getString("Month");

                months = Arrays.copyOf(months, months.length + 1); //create new array from old array and allocate one more element
                months[months.length - 1] = months_123;
                running_textview.setText("Running Month :  " + months_123);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialogg1.dismiss();


    }

    private void getNoofPersons() {


        dialogg1.show();
        dialogg1.setCanceledOnTouchOutside(false);

        String param = "?action=getPerson";
        String url = getResources().getString(R.string.spreadsheet_url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + param,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItemsofPersons(response);
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
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);

    }

    private void parseItemsofPersons(String jsonResposnce) {

        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");

            persons = new String[(0)];

            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String persons_123 = jo.getString("Name");

                persons = Arrays.copyOf(persons, persons.length + 1); //create new array from old array and allocate one more element
                persons[persons.length - 1] = persons_123;


            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

        dialogg1.dismiss();


    }

    private void countDownTimer(int time) {


        preview_reload_btn.setVisibility(View.GONE);
        timer_textview.setVisibility(View.VISIBLE);
        new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                timer_textview.setText(">  " + millisUntilFinished / 1000 + "  <");

            }

            @Override
            public void onFinish() {
                timer_textview.setVisibility(View.GONE);
                preview_reload_btn.setVisibility(View.VISIBLE);
            }
        }.start();


    }

    private void getData() {


        getNoofMonths();
        getNoofPersons();

    }

    private void getNoofMonths1() {

        Log.i("entered", "one");

        String param = "?action=getMonth";
        String url = getResources().getString(R.string.spreadsheet_url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + param,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseItemsofMonths1(response);
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
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(stringRequest);

    }

    private void parseItemsofMonths1(String jsonResposnce) {

        Log.i("entered", "two");


        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");

            months = new String[(0)];


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String months_123 = jo.getString("Month");

                Log.i("started", "" + i);

                months1 = Arrays.copyOf(months1, months1.length + 1); //create new array from old array and allocate one more element
                months1[months1.length - 1] = months_123;
                running_textview.setText("Running Month :  " + months_123);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}