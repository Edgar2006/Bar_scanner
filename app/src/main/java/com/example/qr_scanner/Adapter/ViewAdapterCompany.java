package com.example.qr_scanner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qr_scanner.Class.Rating;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.DataBase_Class.MyBool;
import com.example.qr_scanner.DataBase_Class.ProductBio;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class ViewAdapterCompany extends RecyclerView.Adapter<ViewAdapterCompany.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<ProductBio> productBios;
    public ViewAdapterCompany(Context context, ArrayList<ProductBio> productBios) {
        this.productBios = productBios;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdapterCompany.ViewHolder holder, int position) {
        ProductBio productBio = productBios.get(position);
        holder.productName.setText(productBio.getProductName());
        if(!Objects.equals(productBio.getImageRef(), "noImage")){
            Picasso.get().load(productBio.getImageRef()).into(holder.productImageView);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProductRating").child(productBio.getBarCode());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Rating rating = snapshot.getValue(Rating.class);
                try {
                    holder.scanCount.setText(String.valueOf(rating.countView));
                    float v = rating.rating / (float) rating.countRating;
                    holder.ratingScore.setText(String.valueOf(v));
                    holder.ratingBar.setRating(v);
                }catch (Exception e){

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return productBios.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        RatingBar ratingBar;
        TextView productName,scanCount, ratingScore;
        ImageView productImageView;
        public ViewHolder(View view) {
            super(view);
            ratingBar = view.findViewById(R.id.ratingBar);
            productName = view.findViewById(R.id.productName);
            scanCount = view.findViewById(R.id.count);
            ratingScore = view.findViewById(R.id.ratingScore);
            productImageView = view.findViewById(R.id.productImageView);

        }

    }


    public ViewAdapterCompany.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_company,parent,false);
        return new ViewAdapterCompany.ViewHolder(view);
    }

}
