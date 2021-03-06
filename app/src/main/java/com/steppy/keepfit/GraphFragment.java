package com.steppy.keepfit;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Color;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.charts.BarChart;


import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Turkleton's on 07/02/2017.
 */
public class GraphFragment extends Fragment {
    private DBHelper dbHelper;
    List<String> dates;

    private Spinner unitsSpin;
    private int endYear;
    private int endMonth;
    private int endDay;

    private int startYear;
    private int startMonth;
    private int startDay;

    private String statistics = "Total";
    private String startDate="";
    private String endDate="";
    private Button butStartDate;
    private Button butEndDate;

    private String cutOffDirection="No Selection";
    private String cutOffPercentage="0";
    private Button buttonConfirm;
    TextView emptyState;

    private View graphView;
    private Spinner cutOffSpin;
    private SeekBar cutOffSeek;
    private TextView cutOffTV;

    String unitsSpinString="";
    String percentSpinString="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        graphView = inflater.inflate(R.layout.fragment_graph, container, false);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("History");

        putWarning();

        emptyState = (TextView) graphView.findViewById(R.id.empty_view);

        final Calendar c = Calendar.getInstance();
        endYear = c.get(Calendar.YEAR);
        endMonth = c.get(Calendar.MONTH);
        endDay = c.get(Calendar.DAY_OF_MONTH);

        unitsSpin = (Spinner) graphView.findViewById(R.id.spinnerUnits);
        cutOffSpin = (Spinner) graphView.findViewById(R.id.spinnerCutOff);
        cutOffSeek = (SeekBar) graphView.findViewById(R.id.seekBarCutOff);
        cutOffTV = (TextView) graphView.findViewById(R.id.seektvCutOff);

        setDefaults();

        butStartDate = (Button) graphView.findViewById(R.id.buttonStartDate);
        butStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                startYear=year;
                                startMonth=monthOfYear;
                                startDay=dayOfMonth;
                                butStartDate.setText(dayOfMonth+" "+getMonth(monthOfYear));
                            }
                        }, startYear, startMonth, startDay);
                dpd.show();
            }
        });
        butEndDate = (Button) graphView.findViewById(R.id.buttonEndDate);
        butEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                endYear=year;
                                endMonth=monthOfYear;
                                endDay=dayOfMonth;
                                butEndDate.setText(dayOfMonth+" "+getMonth(monthOfYear));
                            }
                        }, endYear,endMonth,endDay);
                dpd.show();
            }
        });



        unitsSpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                unitsSpinString=unitsSpin.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cutOffSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cutOffTV.setText(progress+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dbHelper = new DBHelper(getActivity());
        buttonConfirm = (Button) graphView.findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popGraph();
            }
        });


        popGraph();
        return graphView;
    }

    public void popGraph(){

        cutOffDirection = cutOffSpin.getSelectedItem().toString();
        cutOffPercentage = Integer.toString(cutOffSeek.getProgress());

        Date date = new GregorianCalendar(startYear,startMonth,startDay).getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        startDate = sdf.format(date);
        date = new GregorianCalendar(endYear,endMonth,endDay).getTime();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        endDate = sdf.format(date);
        Cursor customResult  = dbHelper.getCustomUserOldGoals(statistics,startDate,endDate,cutOffDirection,cutOffPercentage);

        List<BarEntry> entries = new ArrayList<>();
        dates = new ArrayList<>();

        customResult.moveToFirst();

        int i=0;
        while(!customResult.isAfterLast()){
            String name = customResult.getString(customResult.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_NAME));
            String percentage = customResult.getString(customResult.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_PERCENTAGE));
            String dateString = customResult.getString(customResult.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_DATE));
            String progressString = customResult.getString(customResult.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_PROGRESS));
            String goalString = customResult.getString(customResult.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_GOALVALUE));
            String goalUnit = customResult.getString(customResult.getColumnIndex(DBHelper.OLD_GOAL_COLUMN_UNITS));
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            try{
                date = df.parse(dateString);
                if(name.length()>10){
                    name=name.substring(0,8)+"...";
                }
                dates.add(date.getDate()+"/"+date.getMonth()+" "+goalUnit.charAt(0)+"\n"+name);
            }catch (Exception e){
                e.printStackTrace();
            }

            float progress = Float.parseFloat(progressString);
            float goal = Float.parseFloat(goalString);

            //unitsSpinString = unitsSpin.getSelectedItem().toString();

            float progressUnits=  stepsToUnits(progress,unitsSpinString);
            float goalUnits =  stepsToUnits(goal,unitsSpinString);

            float remainder = goalUnits-progressUnits;
            if(remainder<0){
                remainder=0f;
            }

            entries.add(new BarEntry(i,new float[]{progressUnits,remainder},name));

            i++;
            customResult.moveToNext();

        }
        customResult.close();
        dbHelper.close();


        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            final String[] datesArray = dates.toArray(new String[dates.size()]);
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return datesArray[(int) value];
            }
        };

        BarChart chart = (BarChart) graphView.findViewById(R.id.chart);
        chart = clearBarChart(chart);
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(formatter);
        BarDataSet dataSet = new BarDataSet(entries,"");

        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        dataSet.setColors(getColors());

        dataSet.setStackLabels(new String[]{"progress", "goal total", });

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(dataSet);

        BarData barData = new BarData(dataSets);
        chart.setNoDataText("Please select some options to see a graph");
        chart.setData(barData);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);

        if(dates.isEmpty()){
            emptyState.setVisibility(View.VISIBLE);
            chart.setVisibility(View.GONE);
            return;
        }else{
            emptyState.setVisibility(View.GONE);
            chart.setVisibility(View.VISIBLE);
        }

        chart.invalidate();


    }
    public BarChart clearBarChart(BarChart chart){
        List<BarEntry> emptyEntries = new ArrayList<>();
        BarDataSet dataSet = new BarDataSet(emptyEntries,"");
        BarData barData = new BarData(dataSet);
        chart.setData(barData);
        chart.invalidate();
        return chart;
    }


    private int[] getColors() {

        int stacksize = 2;

        // have as many colors as stack-values per entry
        int[] colors = new int[stacksize];

        for (int i = 0; i < colors.length; i++) {
            colors[i] = ColorTemplate.MATERIAL_COLORS[i];
        }

        return colors;
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    public void setDefaults(){
        final Calendar c = Calendar.getInstance();
        endYear = c.get(Calendar.YEAR);
        endMonth = c.get(Calendar.MONTH);
        endDay = c.get(Calendar.DAY_OF_MONTH);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String percentValue = SP.getString("percentageHis","0");
        int percent=0;
        try{
            percent = Integer.parseInt(percentValue);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(percent>0 & percent <100){
            cutOffTV.setText(percent+"");
            cutOffSeek.setProgress(percent);
        }


        String percentValueDrop = SP.getString("percentageDropHis","No selection");
        int percentDrop=1;
        try{
            percentDrop=Integer.parseInt(percentValueDrop);
        }catch (Exception e){
            e.printStackTrace();
        }
        cutOffSpin.setSelection(percentDrop-1);

        String unitsValue = SP.getString("defaultUnitHis","1");
        unitsSpin.setSelection(Integer.parseInt(unitsValue)-1);

        switch (unitsValue){
            case "1":
                unitsSpinString="Steps";
                break;
            case "2":
                unitsSpinString="Kilometres";
                break;
            case "3":
                unitsSpinString="Metres";
                break;
            case "4":
                unitsSpinString="Miles";
                break;
            case "5":
                unitsSpinString="Yards";
                break;
        }

        String array = SP.getString("pastLengthHis","1");

        int curMonth = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        int backLength=0;
        switch (array){
            case "1":
                backLength=-7;
                break;
            case "2":
                backLength=-14;
                break;
            case "3":
                backLength=-curMonth;
                break;
            case "4":
                backLength=-(curMonth*2);
                break;
        }

        c.add(Calendar.DATE,backLength);
        startYear=c.get(Calendar.YEAR);
        startMonth=c.get(Calendar.MONTH);
        startDay=c.get(Calendar.DAY_OF_MONTH);
    }

    public void putWarning() {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean testMode = SP.getBoolean("enableTest", false);
        ImageView iv = (ImageView) getActivity().findViewById(R.id.alertTestMode);
        if (testMode) {
            iv.setVisibility(View.VISIBLE);
        } else {
            iv.setVisibility(View.INVISIBLE);
        }
    }
    public float stepsToUnits(float steps, String units){
        float stepsFloat=0f;
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        float stepsCM = Float.parseFloat(SP.getString("mappingMet","75"));
        float stepsInch = Float.parseFloat(SP.getString("MappingImp","30"));
        stepsFloat=steps;
        switch (units){
            case "Kilometres":
                float cm = stepsCM*steps;
                stepsFloat= cm/100000;
                break;
            case "Metres":
                float cmMetres = stepsCM*steps;
                stepsFloat=cmMetres/100;
                break;
            case "Miles":
                float inches = stepsInch*steps;
                stepsFloat= inches/(1760*36);
                break;
            case "Yards":
                float inchesYards = stepsInch*steps;
                stepsFloat=inchesYards/36;
                break;
        }
        return stepsFloat;
    }
    @Override
    public void onResume() {
        super.onResume();
        putWarning();
        setDefaults();
        popGraph();
    }
}


