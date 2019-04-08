package com.example.dailyexpense;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BottomSheet extends BottomSheetDialogFragment {
    TextView bottomSheetExpenseType,bottomSheetExpenseAmount,bottomSheetExpenseDate,bottomSheetExpenseTime;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout,container,false);

        bottomSheetExpenseType = view.findViewById(R.id.bottomSheetExpenseType);
        bottomSheetExpenseAmount = view.findViewById(R.id.bottomSheetExpenseAmount);
        bottomSheetExpenseDate = view.findViewById(R.id.bottomSheetExpenseDate);
        bottomSheetExpenseTime = view.findViewById(R.id.bottomSheetExpenseTime);

        if(getArguments()!=null){
            String ExpenseType = getArguments().getString("Expense_Type");
            String Amount = getArguments().getString("Amount");
            String ExpenseDate = getArguments().getString("Expense_Date");
            String ExpenseTime = getArguments().getString("Expense_Time");

            bottomSheetExpenseType.setText(ExpenseType);
            bottomSheetExpenseAmount.setText(Amount);
            bottomSheetExpenseDate.setText(ExpenseDate);
            bottomSheetExpenseTime.setText(ExpenseTime);
        }

        return view;
    }

}
