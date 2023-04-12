package com.example.roombudget3;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GetDataFragment extends Fragment {

    private static final String PERSONS_FILE_NAME = "persons.txt";
    private static final String MONTHS_FILE_NAME = "months.txt";
    String[] persons = {};
    String[] months = {};
    String selected_person;
    String selected_month;

    String[] persons123 = {};

    AlertDialog.Builder mBuilder1;
    AlertDialog dialogg1;
    ListAdapter adapter;
    ListView listView;

    AutoCompleteTextView person_act, month_act;
    TextView total_amount_t;
    Button get_data;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_get_data, container, false);

        mBuilder1 = new AlertDialog.Builder(getActivity());
        View mview1 = getLayoutInflater().inflate(R.layout.custom_loading, null);
        mBuilder1.setView(mview1);
        dialogg1 = mBuilder1.create();

        person_act = v.findViewById(R.id.person1id);
        month_act = v.findViewById(R.id.month1id);
        get_data = v.findViewById(R.id.get_data_btn_id);
        listView = v.findViewById(R.id.lv_items);
        total_amount_t = v.findViewById(R.id.total_Amount_id);

        TextInputLayout person_til = v.findViewById(R.id.person_til);
        TextInputLayout month_til = v.findViewById(R.id.month_til);

        person_til.setStartIconTintList(null);
        month_til.setStartIconTintList(null);

        person_act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String person123 = parent.getItemAtPosition(position).toString();
                selected_person = person123;

            }
        });

        month_act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String month123 = parent.getItemAtPosition(position).toString();
                selected_month = month123;

            }

        });

        get_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getItems(selected_person, selected_month);

            }
        });

        StorageMethods storageMethods = new StorageMethods();
        persons =  storageMethods.readFileOfPersons();
        months = storageMethods.readFileOfMonths();

        Log.i("string","array" + Arrays.toString(persons123));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                person();
                month();

            }
        }, 500);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Log.i("string","array" + Arrays.toString(persons123));

            }
        }, 5000);

        return v;
    }

    private void person() {

        //person dropdown
        ArrayAdapter<String> person_adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.list_item2, persons);
        person_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        person_act.setAdapter(person_adapter);
    }

    private void month() {

        //month dropdown
        ArrayList<String> m = new ArrayList<>();
        for (int i = 0; i < months.length; i++) {
            m.add(Integer.toString(i + 1));
        }
        m.add("All");

        ArrayAdapter<String> month_adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item2, m);
        month_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        month_act.setAdapter(month_adapter);
    }

    private void getItems(String kani, String kini) {

        final String name = kani;
        final String month = kini;
        String param = "?action=getItems&person=" + name + "&month=" + month;
        String url = getResources().getString(R.string.spreadsheet_url);

        dialogg1.show();
        dialogg1.setCanceledOnTouchOutside(true);

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

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);

    }

    private void parseItems(String jsonResposnce) {

        ArrayList<HashMap<String, String>> list = new ArrayList<>();

        try {
            JSONObject jobj = new JSONObject(jsonResposnce);
            JSONArray jarray = jobj.getJSONArray("items");

            int amount_temp = 0;

            for (int i = 0; i < jarray.length(); i++) {

                JSONObject jo = jarray.getJSONObject(i);

                String name = jo.getString("Name");
                String id = jo.getString("Id");
                String date = jo.getString("Date");
                String month = jo.getString("Month");
                String amount = jo.getString("Amount");
                String description = jo.getString("Description");

                date = date.replace("-","/");
                date = date + "ABCDE";

                Boolean b = null;
                if(Character.toString(date.charAt(2)).equals("/")){
                    b = true;
                }else if(Character.toString(date.charAt(4)).equals("/")){
                    b = false;
                }

                String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
                String[] temp;
                date = date.substring(0,10);

                if(b){

                    temp = date.split("/");
                    date = String.format("%s %s %s", temp[0], months[Integer.parseInt(temp[1]) - 1], temp[2]);
                }else if(!b){

                    temp = date.split("/");
                    date = String.format("%s %s %s", temp[1], months[Integer.parseInt(temp[2])], temp[0]);
                }


                amount_temp = amount_temp + Integer.parseInt(amount);

                HashMap<String, String> item = new HashMap<>();
                item.put("position1", id);
                item.put("position2", month);
                item.put("position3", amount);
                item.put("position4", name);
                item.put("position5", date);
                item.put("position6", description);

                list.add(item);


            }
            total_amount_t.setText(" ₹  " + amount_temp);


        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter = new SimpleAdapter(getActivity(), list, R.layout.list_item_row2,
                new String[]{"position1", "position2", "position3", "position4","position5","position6"}, new int[]{R.id.lv_2_row_id_id, R.id.lv_2_month_id, R.id.lv_2_amount_id, R.id.lv_2_person_id,R.id.lv_2_date_id , R.id.lv_2_description_id});


        listView.setAdapter(adapter);
        dialogg1.dismiss();


    }



}