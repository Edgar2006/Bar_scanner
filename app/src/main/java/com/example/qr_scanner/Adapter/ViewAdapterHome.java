package com.example.qr_scanner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qr_scanner.Activity.Read;
import com.example.qr_scanner.DataBase_Class.History;
import com.example.qr_scanner.R;

import java.util.ArrayList;


public class ViewAdapterHome extends RecyclerView.Adapter<ViewAdapterHome.ViewHolder>{
    private LayoutInflater inflater;
    private ArrayList<History> histories;
    public ViewAdapterHome(Context context, ArrayList<History> histories) {
        this.histories = histories;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdapterHome.ViewHolder holder, int position) {
        History history = histories.get(position);
        holder.barCode.setText(history.getBarCode());
    }

    @Override
    public int getItemCount() {
        return histories.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView barCode;
        private Button visit;
        public ViewHolder(View view) {
            super(view);
            barCode = view.findViewById(R.id.barCode);
            visit = view.findViewById(R.id.visit);
            visit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), Read.class);
                    intent.putExtra("bareCode", barCode.getText().toString());
                    v.getContext().startActivity(intent);
                }
            });
        }

    }


    public ViewAdapterHome.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_home,parent,false);
        return new ViewAdapterHome.ViewHolder(view);
    }

}
