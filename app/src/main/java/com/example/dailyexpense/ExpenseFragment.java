package com.example.dailyexpense;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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


public class ExpenseFragment extends Fragment {
    private SimpleDateFormat dateAndTimeSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private SimpleDateFormat dateSDF = new SimpleDateFormat("dd MMM yyyy");
    private static long seletedDateinMS;
    private long fromDateInput=0, toDateInput=0;
    String expTypeInput = "";

    private FloatingActionButton floatingActionButtonId;
    private RecyclerView userRecyclerViewId;
    private List<User> userList = new ArrayList<>();

    String[] expenseTypeList;
    private Spinner spinnerId;

    private TextView expenseFromDateTV, expenseToDateTV;

    private DatePickerDialog datePickerDialog;

    private DatabaseHelper helper;


    public ExpenseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expense, container, false);

        expenseTypeList = getContext().getResources().getStringArray(R.array.expenseTypeList);

        spinnerId = rootView.findViewById(R.id.spinnerId);
        expenseFromDateTV = rootView.findViewById(R.id.expenseFromDateTV);
        expenseToDateTV = rootView.findViewById(R.id.expenseToDateTV);

        floatingActionButtonId = rootView.findViewById(R.id.floatingActionId);

        //FloatingActionButton
        floatingActionButtonId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddExpenseActivity.class));
            }
        });

        //ArrayAdapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.expense_layout_view, R.id.expenseTextViewId, expenseTypeList);
        spinnerId.setAdapter(arrayAdapter);


        //UserRequiredData
        spinnerId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String expTypeInput = adapterView.getItemAtPosition(i).toString();
                Cursor cursor;
                if(fromDateInput==0 && toDateInput == 0 && !expTypeInput.equals("Enter expense type")){
                    cursor  = helper.showData(expTypeInput);
                    userList.clear();
                    setAdapterData(cursor);
                }else if(fromDateInput!=0 && !expTypeInput.equals("Enter expense type")){
                    if(toDateInput == 0){
                        toDateInput = getCurrentDate();
                    }
                    cursor  = helper.showData(expTypeInput,fromDateInput,toDateInput);
                    userList.clear();
                    setAdapterData(cursor);
                }else if(fromDateInput !=0 && expTypeInput.equals("Enter expense type")){
                    if(toDateInput == 0){
                        toDateInput = getCurrentDate();
                    }
                    cursor  = helper.showData(fromDateInput,toDateInput);
                    userList.clear();
                    setAdapterData(cursor);
                }
                else{
                    cursor  = helper.showData();
                    userList.clear();
                    setAdapterData(cursor);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //----------------------------------------Date Picker--------------------------------------------------------------------------
        expenseFromDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String selectedDate = year + "/" + month + "/" + day + " 00:00:00";
                        //e.g- 2019/3/9 00:00:00

                        Date date = null;

                        try {
                            date = dateAndTimeSDF.parse(selectedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        seletedDateinMS = date.getTime();
                        expenseFromDateTV.setText(dateSDF.format(date));
                        fromDateInput = seletedDateinMS;

                        if(fromDateInput!=0 && toDateInput!=0)
                        {
                            selectDataByDate(fromDateInput,toDateInput);
                        }else if (fromDateInput!=0 && toDateInput==0){
                            selectDataByDate(fromDateInput,getCurrentDate());
                        }

                    }
                };

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });

        expenseToDateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month = month + 1;
                        String selectedDate = year + "/" + month + "/" + day + " 00:00:00";
                        //e.g- 2019/3/9 00:00:00

                        Date date = null;

                        try {
                            date = dateAndTimeSDF.parse(selectedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        seletedDateinMS = date.getTime();
                        toDateInput = seletedDateinMS;
                        expenseToDateTV.setText(dateSDF.format(date));
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

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, year, month, day);
                datePickerDialog.show();
            }
        });
        //-------------------------------------------ENd Date Picker----------------------------------------------------------------


        //RecyclerView
        userRecyclerViewId = rootView.findViewById(R.id.userRecyclerViewId);
        userRecyclerViewId.setLayoutManager(new LinearLayoutManager(getContext()));

        //Database helper
        helper = new DatabaseHelper(getContext());
        Cursor cursor = helper.showData();
        setAdapterData(cursor);


        return rootView;
    }

    //Method Declaration
    private void selectDataByDate(long fromDate, long toDate) {
        Cursor  cursor  = helper.showData(fromDate,toDate);
        userList.clear();
        setAdapterData(cursor);
    }

    private long getCurrentDate() {
        return System.currentTimeMillis();
    }

    private void setAdapterData(Cursor cursor) {

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(helper.COL_ID));
            String expenseType = cursor.getString(cursor.getColumnIndex(helper.COL_EXPENSE_TYPE));
            int expenseAmount = cursor.getInt(cursor.getColumnIndex(helper.COL_EXPENSE_AMOUNT));
            String expenseDate = cursor.getString(cursor.getColumnIndex(helper.COL_EXPENSE_DATE));
            String expenseTime = cursor.getString(cursor.getColumnIndex(helper.COL_EXPENSE_TIME));
            String expenseImage = cursor.getString(cursor.getColumnIndex(helper.COL_EXPENSE_IMAGE));

            userList.add(new User(id, expenseType, expenseAmount, expenseDate, expenseTime, expenseImage));
        }
        CustomAdapter customAdapter = new CustomAdapter(userList, getContext(), helper);
        userRecyclerViewId.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();


    }

}
