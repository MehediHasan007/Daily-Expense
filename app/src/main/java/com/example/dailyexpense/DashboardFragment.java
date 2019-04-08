package com.example.dailyexpense;


import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class DashboardFragment extends Fragment {

    private SimpleDateFormat dateAndTimeSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private SimpleDateFormat dateSDF = new SimpleDateFormat("dd MMM yyyy");
    private static long seletedDateinMS;
    private long fromDateInput=0, toDateInput=0;
    private String expTypeInput="";
    private double amount=0;

    String[] expenseTypeList;
    private Spinner spinnerId;
    private List<User> userList;
    private DatabaseHelper helper;

    private TextView fromDateTV,toDateTV,totalBDT;

    private DatePickerDialog datePickerDialog;

    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        expenseTypeList = getResources().getStringArray(R.array.expenseTypeList);
        spinnerId = rootView.findViewById(R.id.spinnerId);
        fromDateTV = rootView.findViewById(R.id.fromDateTV);
        toDateTV = rootView.findViewById(R.id.toDateTV);
        totalBDT = rootView.findViewById(R.id.totalBDT);
        userList = new ArrayList<>();

        //Adapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),R.layout.expense_layout_view,R.id.expenseTextViewId,expenseTypeList);
        spinnerId.setAdapter(arrayAdapter);


        //----------------------------------------Date Picker--------------------------------------------------------------------------
        fromDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month+1;
                        String selectedDate = year+"/"+month+"/"+day+" 00:00:00";
                        //e.g- 2019/3/9 00:00:00

                        Date date = null;

                        try {
                            date = dateAndTimeSDF.parse(selectedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        seletedDateinMS = date.getTime();
                        fromDateTV.setText(dateSDF.format(date));

                        fromDateInput = seletedDateinMS;
                        fromDateTV.setText(dateSDF.format(date));
                        if(fromDateInput!=0 && toDateInput!=0)
                        {
                            selectDataByDate(fromDateInput,toDateInput);
                        }

                    }
                };

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),dateSetListener,year,month,day);
                datePickerDialog.show();
            }
        });

        toDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month+1;
                        String selectedDate = year+"/"+month+"/"+day+" 00:00:00";
                        //e.g- 2019/3/9 00:00:00

                        Date date = null;

                        try {
                            date = dateAndTimeSDF.parse(selectedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        seletedDateinMS = date.getTime();
                        toDateTV.setText(dateSDF.format(date));

                        toDateInput = seletedDateinMS;
                        toDateTV.setText(dateSDF.format(date));
                        if(fromDateInput!=0 && toDateInput!=0)
                        {
                            selectDataByDate(fromDateInput,toDateInput);
                        }

                    }
                };

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),dateSetListener,year,month,day);
                datePickerDialog.show();
            }
        });
        //-------------------------------------------ENd Date Picker----------------------------------------------------------------

        helper = new  DatabaseHelper(getContext());
        fromDateInput = getCurrentMonthFirstDate();
        toDateInput = getCurrentDate();

        Cursor cursor = helper.showData(fromDateInput,toDateInput);
        while (cursor.moveToNext()){
            amount += cursor.getDouble(cursor.getColumnIndex(helper.COL_EXPENSE_AMOUNT));
        }
        totalBDT.setText(String.valueOf(amount));

        //SpinnerIdClick
        spinnerId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String expTypeInput = adapterView.getItemAtPosition(i).toString();
                Cursor cursor;
                if(!expTypeInput.equals("Enter expense type")){
                    amount = 0;
                    cursor  = helper.showData(expTypeInput,fromDateInput,toDateInput);
                    userList.clear();
                    setAdapterData(cursor);
                }else if(expTypeInput.equals("Enter expense type")){
                    amount = 0;
                    cursor  = helper.showData(fromDateInput,toDateInput);
                    userList.clear();
                    setAdapterData(cursor);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return rootView;

    }

    private void selectDataByDate(long fromDateInput, long toDateInput) {
        amount = 0;
        Cursor  cursor  = helper.showData(fromDateInput,toDateInput);
        userList.clear();
        setAdapterData(cursor);
    }

    private void setAdapterData(Cursor cursor) {
        amount = 0;
        while (cursor.moveToNext()){
            amount += cursor.getDouble(cursor.getColumnIndex(helper.COL_EXPENSE_AMOUNT));
        }
        totalBDT.setText(String.valueOf(amount));
    }

    private long getCurrentDate() {
        return  System.currentTimeMillis();
    }

    private long getCurrentMonthFirstDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        month = month+1;

        String dateFormat = year + "/" + month + "/" + 1;
        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy/MM/dd");
        Date date =  null;
        try {
            date = dateSdf.parse(dateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long currentTime = date.getTime();

        return currentTime;
        
    }

}
