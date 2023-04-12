package com.example.roombudget3;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

public class GetAllDataFragment extends Fragment {

    private static final String MONTHS_FILE_NAME = "months.txt";
    String[] months = {};
    String selected_month;

    AlertDialog.Builder mBuilder1;
    AlertDialog dialogg1;
    ListAdapter adapter1;
    ListView listView1;
    int[] ind_amount = {0, 0, 0, 0, 0};

    String default_path = Environment.getExternalStorageDirectory() + "/Android/data/com.android.room_budget/files/";

    AutoCompleteTextView month_act;
    Button get_all_data_btn;
    TextView total_amount_t;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_get_all_data, container, false);

        mBuilder1 = new AlertDialog.Builder(getActivity());
        View mview1 = getLayoutInflater().inflate(R.layout.custom_loading, null);
        mBuilder1.setView(mview1);
        dialogg1 = mBuilder1.create();

        get_all_data_btn = v.findViewById(R.id.get_all_data_btn_id);
        month_act = v.findViewById(R.id.month2id);
        listView1 = v.findViewById(R.id.lv_items2);
        total_amount_t = v.findViewById(R.id.total_amount_id3);

        TextInputLayout month_til = v.findViewById(R.id.month_til);
        month_til.setStartIconTintList(null);

        month_act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String month123 = parent.getItemAtPosition(position).toString();
                selected_month = month123;

            }
        });

        get_all_data_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < 5; i++) {
                    ind_amount[i] = 0;
                }
                getItems(selected_month);

            }
        });

        StorageMethods storageMethods = new StorageMethods();
        months = storageMethods.readFileOfMonths();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                month();
            }
        }, 500);

        return v;
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

    private void getItems(String kini) {

        final String month = kini;
        String param = "?action=getTotal&month=" + month;
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

            int amount_temp = 0;

            String items = " ";
            for (int a = 0; a < 5; a++) {

                if (a == 0) {
                    items = "items1";
                }
                if (a == 1) {
                    items = "items2";
                }
                if (a == 2) {
                    items = "items3";
                }
                if (a == 3) {
                    items = "items4";
                }
                if (a == 4) {
                    items = "items5";
                }

                JSONArray jarray = jobj.getJSONArray(items);
                for (int i = 0; i < jarray.length(); i++) {

                    JSONObject jo = jarray.getJSONObject(i);

                    String name = jo.getString("Name");
                    String month = jo.getString("Month");
                    String amount = jo.getString("Amount");

                    ind_amount[a] = ind_amount[a] + Integer.parseInt(amount);


                    amount_temp = amount_temp + Integer.parseInt(amount);
                    total_amount_t.setText(" ₹  " + Integer.toString(amount_temp));


                    if (i == jarray.length() - 1) {


                        HashMap<String, String> item = new HashMap<>();
                        item.put("position1", name);
                        item.put("position2", month);
                        item.put("position3", Integer.toString(ind_amount[a]));

                        list.add(item);

                    }

                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        adapter1 = new SimpleAdapter(getActivity(), list, R.layout.list_item_row1,
                new String[]{"position1", "position2", "position3"}, new int[]{R.id.lv_1_person_id, R.id.lv_1_month_id, R.id.lv_1_amount_id});

        listView1.setAdapter(adapter1);
        dialogg1.dismiss();

    }
}