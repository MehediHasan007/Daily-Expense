package com.example.dailyexpense;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class AddExpenseActivity extends AppCompatActivity {

    private String expenseType,expenseTime,expenseAmount,expenseDate;
    private String expenseImageInput="";
    private int id;

    private SimpleDateFormat dateAndTimeSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private SimpleDateFormat dateSDF = new SimpleDateFormat("dd MMMM yyyy");
    private static long seletedDateinMS;

    String[] expenseTypeList;
    private Spinner spinnerId;

    private TextView enterExpenseDateET, enterExpenseTimeET;
    private EditText enterExpenseAmountET;
    private ImageView selectImageId;
    private Button selectImageBtn, addExpenseBtn;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private String amPM;
    //Database
    private DatabaseHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        getSupportActionBar().setTitle("Add Expense");


        //permission for external URI
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            return;
        }

        expenseTypeList = getResources().getStringArray(R.array.expenseTypeList);
        spinnerId = findViewById(R.id.spinnerId);
        enterExpenseAmountET = findViewById(R.id.enterExpenseAmountET);
        enterExpenseDateET = findViewById(R.id.enterExpenseDateET);
        enterExpenseTimeET = findViewById(R.id.enterExpenseTimeET);
        selectImageId = findViewById(R.id.selectImageId);//Image id
        selectImageBtn = findViewById(R.id.selectImageBtn);
        addExpenseBtn = findViewById(R.id.addExpenseBtn);

        helper = new DatabaseHelper(this);

        //spinnerAdapter
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.expense_layout_view, R.id.expenseTextViewId, expenseTypeList);
        spinnerId.setAdapter(arrayAdapter);

        //UpdateAddexpense
        if (getIntent().getStringExtra("expenseType")==null){

        }
        else {
            expenseType = getIntent().getStringExtra("expenseType");
            expenseTime = getIntent().getStringExtra("expenseTime");
            expenseAmount = getIntent().getStringExtra("expenseAmount");
            expenseDate = getIntent().getStringExtra("expenseDate");
            expenseImageInput = getIntent().getStringExtra("expenseImage");
            id = getIntent().getIntExtra("id", 0);

            enterExpenseAmountET.setText(expenseAmount);
            enterExpenseDateET.setText(expenseDate);
            enterExpenseTimeET.setText(expenseTime);
            if(!expenseImageInput.equals("")) {
                Bitmap bitmap = decodeBase64(expenseImageInput);
                selectImageId.setImageBitmap(bitmap);
            }
            spinnerId.setSelection(arrayAdapter.getPosition(expenseType));

            addExpenseBtn.setText("Update Expense");
            getSupportActionBar().setTitle("Update Expense");
        }


        //----------------------------------------Date Picker--------------------------------------------------------------------------
        enterExpenseDateET.setOnClickListener(new View.OnClickListener() {
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
                        enterExpenseDateET.setText(dateSDF.format(date));

                    }
                };

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddExpenseActivity.this,dateSetListener,year,month,day);
                datePickerDialog.show();
            }
        });

        //----------------------------------------Time Picker--------------------------------------------------------------------------

        enterExpenseTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int currentMinute = calendar.get(Calendar.HOUR_OF_DAY);
                int currentHour = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(AddExpenseActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay >= 12) {
                            amPM = "PM";
                        } else {
                            amPM = "AM";
                        }
                        enterExpenseTimeET.setText(hourOfDay + ":" + minute + amPM);
                    }
                }, currentHour, currentMinute, false);
                timePickerDialog.show();
            }
        });



    }

    // Add Expense Button-------------------------------------------
    public void addExpenseInfo(View view) throws ParseException {

        if(expenseType!=null && expenseAmount != null && expenseDate!=null && expenseTime!=null && expenseImageInput!=null){
            String exType = spinnerId.getSelectedItem().toString();
            String exAmount = enterExpenseAmountET.getText().toString();
            long MSDate = seletedDateinMS;
            String exTime = enterExpenseTimeET.getText().toString();

            if(exType.equals("Enter expense type")){
                Toast.makeText(AddExpenseActivity.this, "Enter Meaningful Expense Type", Toast.LENGTH_SHORT).show();
            } else if (exAmount.equals("")) {
                Toast.makeText(AddExpenseActivity.this, "Enter Expense Amount", Toast.LENGTH_SHORT).show();
            } else {
                helper.updateData(id,exType,Integer.valueOf(exAmount),MSDate, exTime, expenseImageInput);
                Toast.makeText(AddExpenseActivity.this, "Update Successfull", Toast.LENGTH_SHORT).show();

                enterExpenseAmountET.setText("");
                enterExpenseDateET.setText("");
                enterExpenseTimeET.setText("");
            }

        }else{
            String expenseType = spinnerId.getSelectedItem().toString();
            String expenseAmount = enterExpenseAmountET.getText().toString();
            String expenseDate = enterExpenseDateET.getText().toString();
            long MSDate = seletedDateinMS;
            String expenseTime = enterExpenseTimeET.getText().toString();

            if(expenseType.equals("Enter expense type")){
                Toast.makeText(AddExpenseActivity.this, "Enter Meaningful Expense Type", Toast.LENGTH_SHORT).show();
            } else if (expenseAmount.equals("")) {
                Toast.makeText(AddExpenseActivity.this, "Enter Expense Amount", Toast.LENGTH_SHORT).show();
            } else if (expenseDate.equals("")) {
                Toast.makeText(AddExpenseActivity.this, "Enter Expense Date", Toast.LENGTH_SHORT).show();
            } else {
                long value = helper.insertData(expenseType, Integer.valueOf(expenseAmount),MSDate, expenseTime,expenseImageInput);
                Toast.makeText(AddExpenseActivity.this, "Expense No: "+value, Toast.LENGTH_SHORT).show();

            }

        }


    }

    //SelectImage
    public void selectFromCameraOrPhotos(View view) {
        PopupMenu popupMenu = new PopupMenu(AddExpenseActivity.this,selectImageBtn);
        popupMenu.getMenuInflater().inflate(R.menu.image_popup_menu,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()){
                    case R.id.imageFromCamera:
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,0);
                        return true;
                    case R.id.imageFromPhotos:
                        Intent photosIntent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(photosIntent,1);
                        return true;
                }
                return false;
            }
        });
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == 0 && resultCode == RESULT_OK) {

                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                expenseImageInput = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 80);
                selectImageId.setImageBitmap(bitmap);

            }
            if (requestCode == 1 ) {

                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddExpenseActivity.this.getContentResolver(), uri);

                    expenseImageInput = encodeToBase64(bitmap, Bitmap.CompressFormat.JPEG, 80);
                    selectImageId.setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }
    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

}
