package com.example.roombudget3;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class DefaultFragment extends Fragment {

    private static final String PERSONS_FILE_NAME = "persons.txt";
    private static final String MONTHS_FILE_NAME = "months.txt";
    AlertDialog.Builder mBuilder1;
    AlertDialog dialogg1;
    StorageMethods storageMethods = new StorageMethods();
    String[] persons = {};
    String[] months = {};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_default, container, false);

        mBuilder1 = new AlertDialog.Builder(getActivity());
        View mview1 = getLayoutInflater().inflate(R.layout.custom_loading, null);
        mBuilder1.setView(mview1);
        dialogg1 = mBuilder1.create();

        regularCheck();

        return v;
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

                    dialogg1.dismiss();

                } else {

                    dialogg1.show();
                    dialogg1.setCanceledOnTouchOutside(false);
                    //deleting 2 files if no content available
                    storageMethods.deleteFile(PERSONS_FILE_NAME);
                    storageMethods.deleteFile(MONTHS_FILE_NAME);

                    regularCheck();

                }

            } else {

                dialogg1.dismiss();
                dialogg1.show();
                dialogg1.setCanceledOnTouchOutside(false);
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

    private void getData() {


        getNoOfPersons();
        getNoOfMonths();

    }

    private void getNoOfMonths() {


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


        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");

            months = new String[(0)];


            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String months_123 = jo.getString("Month");

                months = Arrays.copyOf(months, months.length + 1); //create new array from old array and allocate one more element
                months[months.length - 1] = months_123;


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void getNoOfPersons() {


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


    }

}