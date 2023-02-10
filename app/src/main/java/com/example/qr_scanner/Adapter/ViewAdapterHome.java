package com.example.qr_scanner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qr_scanner.Activity.Read;
import com.example.qr_scanner.Activity.ScanActivity;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.History;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.DataBase_Class.MyBool;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


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
