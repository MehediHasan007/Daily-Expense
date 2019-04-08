package com.example.dailyexpense;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<User> userList;
    private Context context;
    DatabaseHelper helper;
    private SimpleDateFormat dateSDF = new SimpleDateFormat("dd MMMM yyyy");


    public CustomAdapter(List<User> userList,Context context,DatabaseHelper helper) {
        this.userList = userList;
        this.context = context;
        this.helper = helper;
    }
    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_layout,viewGroup,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomAdapter.ViewHolder viewHolder,final int i) {

        final User user = userList.get(i);

        //BottomSheet
        viewHolder.singleCardViewId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                bundle.putString("Expense_Type",user.getExpenseType());
                bundle.putString("Amount",String.valueOf(user.getExpenseAmount()));
                bundle.putString("Expense_Date",dateSDF.format(Long.valueOf(user.getExpenseDate())));
                bundle.putString("Expense_Time",user.getExpenseTime());

                BottomSheet bottomSheet = new BottomSheet();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(((AppCompatActivity) context).getSupportFragmentManager(),"");
            }

        });


        viewHolder.billTypeTV.setText(user.getExpenseType());
        viewHolder.dateTV.setText(dateSDF.format(Long.valueOf(user.getExpenseDate())));
        viewHolder.expenseBillTV.setText(String.valueOf(user.getExpenseAmount()));
        viewHolder.popUpMenuIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,viewHolder.popUpMenuIV);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.deleteMenu:
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                                alertDialogBuilder.setTitle(R.string.alert_title);
                                alertDialogBuilder.setMessage(R.string.message_title);
                                alertDialogBuilder.setIcon(R.drawable.question);
                                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        helper.deleteData(user.getId());
                                        userList.remove(i);
                                        notifyDataSetChanged();
                                    }
                                });
                                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                                return true;
                            case R.id.updateMenu:
                                context.startActivity(new Intent(context,AddExpenseActivity.class)
                                        .putExtra("id",user.getId())
                                        .putExtra("expenseType",user.getExpenseType())
                                        .putExtra("expenseAmount",String.valueOf(user.getExpenseAmount()))
                                        .putExtra("expenseDate",dateSDF.format(Long.valueOf(user.getExpenseDate())))
                                        .putExtra("expenseTime",user.getExpenseTime())
                                        .putExtra("expenseImage",user.getExpenseImage()));
                                return true;
                        }
                        return false;
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView billTypeTV,dateTV,expenseBillTV;
        private ImageView popUpMenuIV;
        private CardView singleCardViewId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            billTypeTV = itemView.findViewById(R.id.billTypeTV);
            dateTV = itemView.findViewById(R.id.dateTV);
            expenseBillTV = itemView.findViewById(R.id.expenseBillTV);
            popUpMenuIV = itemView.findViewById(R.id.popUpMenuIV);
            singleCardViewId = itemView.findViewById(R.id.singleCardViewId);
        }
    }
}
