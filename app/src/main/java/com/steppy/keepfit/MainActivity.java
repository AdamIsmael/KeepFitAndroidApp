package com.steppy.keepfit;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;


import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;


import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity  {
    private BottomBar bottomBar;
    private Button but;
    private MainActivity mai;
    Toolbar toolbar;
    View dateButtonMain;
    ArrayList<Goal> ItemGoalList = new ArrayList<Goal>();

    Calendar c = Calendar.getInstance();
    int yearr;
    int monthh;
    int day;
    static final int dialog_id = 0;

    DBHelper dbHelper;

    public void showDialogOnButtonClick(){
        but = (Button) findViewById(R.id.date);
        but.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        showDialog(dialog_id);
                    }
                }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id ){
        if(id==dialog_id){
            return new DatePickerDialog(this,dpickerListener,yearr,monthh,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            yearr = year;
            monthh=month;
            day = dayOfMonth;

        }
    };

    public Context getContext() {
        return mai;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


//        dbHelper = new DBHelper(this);
//        final Cursor cursor = dbHelper.getAllGoals();
//        cursor.moveToFirst();

        File dir = getFilesDir();
        File file = new File(dir, "goals.txt");
        boolean deleted = file.delete();


        mai=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(" ");
        //showDialogOnButtonClick();

        dateButtonMain = getLayoutInflater().inflate(R.layout.datepicker, null);
        toolbar.addView(dateButtonMain);

        //fragmentTransaction.add(R.id.progressMiddle, mainFragment);
        final Calendar cal = Calendar.getInstance();
        yearr = cal.get(Calendar.YEAR);
        monthh = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        MainFragment mainFragment = new MainFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.progressMiddle, mainFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        //Toast.makeText(MainActivity.this,"month"+monthh,Toast.LENGTH_LONG).show();

        but = (Button) findViewById(R.id.date);

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setDefaultTab(R.id.tab_main);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_goals) {
                    toolbar.removeView(dateButtonMain);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    ViewGoalsFragment goalsFragment = new ViewGoalsFragment();
                    fragmentTransaction.replace(R.id.progressMiddle, goalsFragment);
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();


                    //viewGoals();
                }else if(tabId == R.id.tab_graph){
                    toolbar.removeView(dateButtonMain);
                    GraphFragment graphFragment = new GraphFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.progressMiddle, graphFragment);
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();
                }
                else if(tabId == R.id.tab_main){
                    try {
                        toolbar.addView(dateButtonMain);
                    }catch(Exception e){

                    }
                    MainFragment mainFragment = new MainFragment();
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    fragmentTransaction.replace(R.id.progressMiddle, mainFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                }
            }
        });
    }

    public static class GraphFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_graph, container, false);
        }
    }
//    public static class MainFragment extends Fragment {
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                                 Bundle savedInstanceState) {
//            // Inflate the layout for this fragment
//            final View mainView = inflater.inflate(R.layout.fragment_main, container, false);
//            FloatingActionButton myFab = (FloatingActionButton) mainView.findViewById(R.id.fab);
//            myFab.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    Toast.makeText(MainActivity.this,"main fab press",Toast.LENGTH_LONG).show();
//                }
//            });
//            return inflater.inflate(R.layout.fragment_main, container, false);
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
