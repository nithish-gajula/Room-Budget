package com.example.roombudget3;

import android.graphics.Color;
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
import android.widget.RelativeLayout;
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
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.util.List;

public class ChartsFragment extends Fragment {

    private static final String MONTHS_FILE_NAME = "months.txt";
    AnyChartView anyChartView;
    BarChart barChart;
    PieChart pieChart;
    RadarChart radarChart;
    AutoCompleteTextView month_spinner, chart_type_spinner;
    String[] months = {};
    String default_path = Environment.getExternalStorageDirectory() + "/Android/data/com.android.room_budget/files/";


    String selected_chart_type;
    String[] names = {"Nithish", "Vivek", "LaxmiKanth", "Bharath", "Rahul"};


    String[] demo_persons = {};
    String[] demo_months = {};
    String[] nithish_amount = {};
    String[] vivek_amount = {};
    String[] laxmikanth_amount = {};
    String[] bharath_amount = {};
    String[] rahul_amount = {};

    int[] demo_persons_total = {0, 0, 0, 0, 0};

    String selected_month;
    TextView tv;
    TextInputLayout til;

    AlertDialog.Builder mBuilder1;
    AlertDialog dialogg1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_charts, container, false);


        mBuilder1 = new AlertDialog.Builder(getActivity());
        View mview1 = getLayoutInflater().inflate(R.layout.custom_loading, null);
        mBuilder1.setView(mview1);
        dialogg1 = mBuilder1.create();

        pieChart = view.findViewById(R.id.pie_chart_view);
        radarChart = view.findViewById(R.id.radar_chart_view);
        anyChartView = view.findViewById(R.id.any_chart_view);
        barChart = view.findViewById(R.id.bar_chart_view);
        Button load = view.findViewById(R.id.load_btn_id);
        month_spinner = view.findViewById(R.id.select_month_id);
        chart_type_spinner = view.findViewById(R.id.select_charts_type_id);
        tv = view.findViewById(R.id.textView2);
        til = view.findViewById(R.id.month_invisible);
        RelativeLayout rl = view.findViewById(R.id.rl_layout);

        TextInputLayout chart_til = view.findViewById(R.id.chart_til);
        chart_til.setStartIconTintList(null);
        til.setStartIconTintList(null);


        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selected_chart_type == "Total Charts") {

                    for (int i = 0; i < demo_persons_total.length; i++) {
                        demo_persons_total[i] = 0;
                    }

                    getTotalChartsbtn();
                } else if (selected_chart_type == "Monthly Charts") {
                    getMontlyChartsbtn();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        radarChart.performClick();
                    }
                }, 2000);


            }
        });

        month_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String month123 = parent.getItemAtPosition(position).toString();
                selected_month = month123;


            }

        });

        chart_type_spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final String chart123 = parent.getItemAtPosition(position).toString();
                selected_chart_type = chart123;

                if (selected_chart_type == "Monthly Charts") {
                    til.setVisibility(View.VISIBLE);
                } else {

                    month_spinner.setText("");
                    til.setVisibility(View.GONE);
                }

            }

        });


        getItems();
        StorageMethods storageMethods = new StorageMethods();
        months = storageMethods.readFileOfMonths();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rl.setVisibility(View.VISIBLE);
                month();
                charts();
            }
        }, 3000);

        return view;
    }

    private void month() {

        //month dropdown
        ArrayList<String> n = new ArrayList<>();
        for (int i = 0; i < months.length; i++) {
            n.add(Integer.toString(i + 1));
        }

        ArrayAdapter<String> month_adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item2, n);
        month_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        month_spinner.setAdapter(month_adapter);
    }

    private void charts() {

        //chart type dropdown
        ArrayList<String> m = new ArrayList<>();
        m.add("Total Charts");
        m.add("Monthly Charts");

        ArrayAdapter<String> month_adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item2, m);
        month_adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        chart_type_spinner.setAdapter(month_adapter);
    }


    //selected content methods

    private void getTotalChartsbtn() {

        anyChartView.setVisibility(View.VISIBLE);

        for (int x = 0; x < demo_months.length; x++) {

            if (nithish_amount[x] == null) {
                nithish_amount[x] = "0";
            }
            if (vivek_amount[x] == null) {
                vivek_amount[x] = "0";
            }
            if (laxmikanth_amount[x] == null) {
                laxmikanth_amount[x] = "0";
            }
            if (bharath_amount[x] == null) {
                bharath_amount[x] = "0";
            }
            if (rahul_amount[x] == null) {
                rahul_amount[x] = "0";
            }

        }

        for (int i = 0; i < demo_months.length; i++) {
            demo_persons_total[0] = demo_persons_total[0] + Integer.parseInt(nithish_amount[i]);
            demo_persons_total[1] = demo_persons_total[1] + Integer.parseInt(vivek_amount[i]);
            demo_persons_total[2] = demo_persons_total[2] + Integer.parseInt(laxmikanth_amount[i]);
            demo_persons_total[3] = demo_persons_total[3] + Integer.parseInt(bharath_amount[i]);
            demo_persons_total[4] = demo_persons_total[4] + Integer.parseInt(rahul_amount[i]);
        }

        setUpBarChart();
        setUpPieChart1();
        setupPieChart2();
        setUpRadarChart2();

    }

    private void getMontlyChartsbtn() {
        anyChartView.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);

        for (int k = 0; k < demo_persons_total.length; k++) {
            demo_persons_total[k] = 0;
        }

        String one = month_spinner.getText().toString().trim();
        int one1 = Integer.parseInt(one) - 1;

        for (int x = 0; x < demo_months.length; x++) {

            if (nithish_amount[x] == null) {
                nithish_amount[x] = "0";
            }
            if (vivek_amount[x] == null) {
                vivek_amount[x] = "0";
            }
            if (laxmikanth_amount[x] == null) {
                laxmikanth_amount[x] = "0";
            }
            if (bharath_amount[x] == null) {
                bharath_amount[x] = "0";
            }
            if (rahul_amount[x] == null) {
                rahul_amount[x] = "0";
            }

        }

        for (int i = 0; i < 1; i++) {
            demo_persons_total[0] = demo_persons_total[0] + Integer.parseInt(nithish_amount[one1]);
            demo_persons_total[1] = demo_persons_total[1] + Integer.parseInt(vivek_amount[one1]);
            demo_persons_total[2] = demo_persons_total[2] + Integer.parseInt(laxmikanth_amount[one1]);
            demo_persons_total[3] = demo_persons_total[3] + Integer.parseInt(bharath_amount[one1]);
            demo_persons_total[4] = demo_persons_total[4] + Integer.parseInt(rahul_amount[one1]);
        }


        monthsetUpBarChart();
        monthsetupPieChart2();
        monthsetUpRadarChart();
    }


    //all data charts

    public void setUpBarChart() {

        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (int i = 0; i < demo_persons.length; i++) {

            barEntries.add(new BarEntry(i + 1, demo_persons_total[i]));
        }


        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("");
        for (int i = 0; i < demo_persons.length; i++) {
            xAxisLabel.add(demo_persons[i]);
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        BarDataSet barDataSet = new BarDataSet(barEntries, "entries");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(14f);
        barDataSet.setDrawValues(true);
        barChart.getDescription().setText("Bar Chart");
        barChart.setData(new BarData(barDataSet));
        barChart.setFitBars(true);
        barChart.getBarData().setBarWidth(0.8f);
        barChart.getXAxis().setLabelCount(5);
        barChart.animateY(5000);


    }

    public void setUpPieChart1() {

        Pie pie123 = AnyChart.pie();

        List<DataEntry> dataEntries = new ArrayList<>();

        for (int i = 0; i < demo_persons.length; i++) {
            dataEntries.add(new ValueDataEntry(demo_persons[i], demo_persons_total[i]));
        }

        pie123.data(dataEntries);
        anyChartView.setChart(pie123);

    }

    private void setupPieChart2() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Grand Total");
        pieChart.setCenterTextSize(20);
        pieChart.getDescription().setText("Pie Chart");

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextSize(15);
        l.setDrawInside(false);
        l.setEnabled(true);


        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < demo_persons.length; i++) {
            entries.add(new PieEntry(demo_persons_total[i], demo_persons[i] + "   "));
        }


        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(5000, Easing.EaseInOutQuad);

    }

    private void setUpRadarChart2() {


        //Nithish Data
        ArrayList<RadarEntry> visitors1 = new ArrayList<>();
        for (int i = 0; i < nithish_amount.length; i++) {
            int val = Integer.parseInt(nithish_amount[i]);
            if (nithish_amount[i] == null) {
                val = 0;
            }
            visitors1.add(new RadarEntry(val));
        }

        //Vivek Data
        ArrayList<RadarEntry> visitors2 = new ArrayList<>();
        for (int i = 0; i < vivek_amount.length; i++) {
            int val = Integer.parseInt(vivek_amount[i]);
            visitors2.add(new RadarEntry(val));
        }

        //LaxmiKanth Data
        ArrayList<RadarEntry> visitors3 = new ArrayList<>();
        for (int i = 0; i < laxmikanth_amount.length; i++) {
            int val = Integer.parseInt(laxmikanth_amount[i]);
            visitors3.add(new RadarEntry(val));
        }


        //Bharath Data
        ArrayList<RadarEntry> visitors4 = new ArrayList<>();
        for (int i = 0; i < bharath_amount.length; i++) {
            int val = Integer.parseInt(bharath_amount[i]);

            visitors4.add(new RadarEntry(val));
        }

        //Rahul Data
        ArrayList<RadarEntry> visitors5 = new ArrayList<>();
        for (int i = 0; i < rahul_amount.length; i++) {
            int val = Integer.parseInt(rahul_amount[i]);
            visitors5.add(new RadarEntry(val));
        }


        RadarDataSet radarDataSetForVisitors1 = new RadarDataSet(visitors1, "Nithish");
        radarDataSetForVisitors1.setColors(Color.RED);
        radarDataSetForVisitors1.setLineWidth(2f);
        radarDataSetForVisitors1.setValueTextColor(Color.RED);
        radarDataSetForVisitors1.setValueTextSize(10f);


        RadarDataSet radarDataSetForVisitors2 = new RadarDataSet(visitors2, "Vivek");
        radarDataSetForVisitors2.setColors(Color.BLUE);
        radarDataSetForVisitors2.setLineWidth(2f);
        radarDataSetForVisitors2.setValueTextColor(Color.BLUE);
        radarDataSetForVisitors2.setValueTextSize(10f);


        RadarDataSet radarDataSetForVisitors3 = new RadarDataSet(visitors3, "LaxmiKanth");
        radarDataSetForVisitors3.setColors(Color.GREEN);
        radarDataSetForVisitors3.setLineWidth(2f);
        radarDataSetForVisitors3.setValueTextColor(Color.GREEN);
        radarDataSetForVisitors3.setValueTextSize(10f);


        RadarDataSet radarDataSetForVisitors4 = new RadarDataSet(visitors4, "Bharath");
        radarDataSetForVisitors4.setColors(Color.YELLOW);
        radarDataSetForVisitors4.setLineWidth(2f);
        radarDataSetForVisitors4.setValueTextColor(Color.YELLOW);
        radarDataSetForVisitors4.setValueTextSize(10f);


        RadarDataSet radarDataSetForVisitors5 = new RadarDataSet(visitors5, "Rahul");
        radarDataSetForVisitors5.setColors(Color.CYAN);
        radarDataSetForVisitors5.setLineWidth(2f);
        radarDataSetForVisitors5.setValueTextColor(Color.CYAN);
        radarDataSetForVisitors5.setValueTextSize(10f);


        RadarData radarData = new RadarData();
        radarData.addDataSet(radarDataSetForVisitors1);
        radarData.addDataSet(radarDataSetForVisitors2);
        radarData.addDataSet(radarDataSetForVisitors3);
        radarData.addDataSet(radarDataSetForVisitors4);
        radarData.addDataSet(radarDataSetForVisitors5);


        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(demo_months));
        radarChart.getDescription().setText("Radar Chart");
        radarChart.setData(radarData);
    }


    //monthly charts

    public void monthsetUpBarChart() {


        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < demo_persons.length; i++) {
            barEntries.add(new BarEntry(i + 1, demo_persons_total[i]));
        }

        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("");
        for (int i = 0; i < demo_persons.length; i++) {
            xAxisLabel.add(demo_persons[i]);
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabel));

        BarDataSet barDataSet = new BarDataSet(barEntries, "entries");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(14f);
        barDataSet.setDrawValues(true);
        barChart.setData(new BarData(barDataSet));
        barChart.setFitBars(true);
        barChart.getBarData().setBarWidth(0.8f);
        barChart.getXAxis().setLabelCount(5);
        barChart.animateY(5000);


    }

    private void monthsetUpRadarChart() {

        String one = month_spinner.getText().toString().trim();
        int one1 = Integer.parseInt(one) - 1;
        int one2 = Integer.parseInt(one);

        ArrayList<RadarEntry> visitors1 = new ArrayList<>();
        for (int i = one1; i < one2; i++) {
            int val1 = Integer.parseInt(nithish_amount[i]);
            int val2 = Integer.parseInt(vivek_amount[i]);
            int val3 = Integer.parseInt(laxmikanth_amount[i]);
            int val4 = Integer.parseInt(bharath_amount[i]);
            int val5 = Integer.parseInt(rahul_amount[i]);
            if (nithish_amount[i] == null) {
                val1 = 0;
            }
            if (vivek_amount[i] == null) {
                val2 = 0;
            }
            if (laxmikanth_amount[i] == null) {
                val3 = 0;
            }
            if (bharath_amount[i] == null) {
                val4 = 0;
            }
            if (rahul_amount[i] == null) {
                val5 = 0;
            }
            visitors1.add(new RadarEntry(val1));
            visitors1.add(new RadarEntry(val2));
            visitors1.add(new RadarEntry(val3));
            visitors1.add(new RadarEntry(val4));
            visitors1.add(new RadarEntry(val5));
        }


        RadarDataSet radarDataSetForVisitors1 = new RadarDataSet(visitors1, "Month : " + one);
        radarDataSetForVisitors1.setColors(Color.RED);
        radarDataSetForVisitors1.setLineWidth(2f);
        radarDataSetForVisitors1.setValueTextColor(Color.RED);
        radarDataSetForVisitors1.setValueTextSize(14f);

        RadarData radarData = new RadarData();
        radarData.addDataSet(radarDataSetForVisitors1);

        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(names));
        radarChart.getDescription().setText("Tap to Refresh");
        radarChart.setData(radarData);
    }

    private void monthsetupPieChart2() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Grand Total");
        pieChart.setCenterTextSize(20);
        pieChart.getDescription().setText("Expenses");

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextSize(15);
        l.setDrawInside(false);
        l.setEnabled(true);


        ArrayList<PieEntry> entries = new ArrayList<>();
        for (int i = 0; i < demo_persons.length; i++) {
            entries.add(new PieEntry(demo_persons_total[i], demo_persons[i] + "   "));
        }


        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(5000, Easing.EaseInOutQuad);

    }

    private void getItems() {

        String param = "?action=getChart";
        String url = getResources().getString(R.string.spreadsheet_url);

        dialogg1.show();
        dialogg1.setCanceledOnTouchOutside(false);

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

            String items = " ";
            for (int a = 0; a < 7; a++) {

                if (a == 0) {
                    items = "months";
                } else if (a == 1) {
                    items = "persons";
                } else if (a == 2) {
                    items = "nithish";
                } else if (a == 3) {
                    items = "vivek";
                } else if (a == 4) {
                    items = "laxmikanth";
                } else if (a == 5) {
                    items = "bharath";
                } else if (a == 6) {
                    items = "rahul";
                }

                int[] amount_temp = {};

                int month12 = 1;
                int temp = 0;
                JSONArray jarray = jobj.getJSONArray(items);
                for (int i = 0; i < jarray.length(); i++) {

                    if (items == "months") {

                        JSONObject jo = jarray.getJSONObject(i);
                        String month = jo.getString("Month");

                        demo_months = Arrays.copyOf(demo_months, demo_months.length + 1); //create new array from old array and allocate one more element
                        demo_months[demo_months.length - 1] = month;


                    }
                    amount_temp = Arrays.copyOf(amount_temp, demo_months.length);
                    if (items == "persons") {

                        JSONObject jo = jarray.getJSONObject(i);
                        String name = jo.getString("Name");

                        demo_persons = Arrays.copyOf(demo_persons, demo_persons.length + 1); //create new array from old array and allocate one more element
                        demo_persons[demo_persons.length - 1] = name;


                    } else if (items == "nithish") {

                        JSONObject jo = jarray.getJSONObject(i);
                        String month = jo.getString("Month");
                        String amount = jo.getString("Amount");

                        nithish_amount = Arrays.copyOf(nithish_amount, demo_months.length);
                        int month34 = Integer.parseInt(month);

                        for (int k = 0; k < demo_months.length; k++) {
                            if (month34 == k + 1) {
                                amount_temp[k] = amount_temp[k] + Integer.parseInt(amount);
                                nithish_amount[k] = Integer.toString(amount_temp[k]);
                            }
                        }


                    } else if (items == "vivek") {

                        JSONObject jo = jarray.getJSONObject(i);
                        String month = jo.getString("Month");
                        String amount = jo.getString("Amount");

                        vivek_amount = Arrays.copyOf(vivek_amount, demo_months.length);
                        int month34 = Integer.parseInt(month);

                        for (int k = 0; k < demo_months.length; k++) {
                            if (month34 == k + 1) {
                                amount_temp[k] = amount_temp[k] + Integer.parseInt(amount);
                                vivek_amount[k] = Integer.toString(amount_temp[k]);
                            }
                        }


                    } else if (items == "laxmikanth") {

                        JSONObject jo = jarray.getJSONObject(i);
                        String month = jo.getString("Month");
                        String amount = jo.getString("Amount");

                        laxmikanth_amount = Arrays.copyOf(laxmikanth_amount, demo_months.length);
                        int month34 = Integer.parseInt(month);

                        for (int k = 0; k < demo_months.length; k++) {
                            if (month34 == k + 1) {
                                amount_temp[k] = amount_temp[k] + Integer.parseInt(amount);
                                laxmikanth_amount[k] = Integer.toString(amount_temp[k]);
                            }
                        }


                    } else if (items == "bharath") {

                        JSONObject jo = jarray.getJSONObject(i);
                        String month = jo.getString("Month");
                        String amount = jo.getString("Amount");

                        bharath_amount = Arrays.copyOf(bharath_amount, demo_months.length);
                        int month34 = Integer.parseInt(month);

                        for (int k = 0; k < demo_months.length; k++) {
                            if (month34 == k + 1) {
                                amount_temp[k] = amount_temp[k] + Integer.parseInt(amount);
                                bharath_amount[k] = Integer.toString(amount_temp[k]);
                            }
                        }


                    } else if (items == "rahul") {

                        JSONObject jo = jarray.getJSONObject(i);
                        String month = jo.getString("Month");
                        String amount = jo.getString("Amount");

                        rahul_amount = Arrays.copyOf(rahul_amount, demo_months.length);
                        int month34 = Integer.parseInt(month);

                        for (int k = 0; k < demo_months.length; k++) {
                            if (month34 == k + 1) {
                                amount_temp[k] = amount_temp[k] + Integer.parseInt(amount);
                                rahul_amount[k] = Integer.toString(amount_temp[k]);
                            }
                        }

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        dialogg1.dismiss();


    }

}