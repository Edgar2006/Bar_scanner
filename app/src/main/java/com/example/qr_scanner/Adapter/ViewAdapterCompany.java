package com.example.qr_scanner.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.qr_scanner.Activity.Company.Product_activityBioEdit;
import com.example.qr_scanner.Activity.User.Read;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.Rating;
import com.example.qr_scanner.DataBase_Class.ProductBio;
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
        holder.barCode.setText(productBio.getBarCode());
        if(!Objects.equals(productBio.getImageRef(), StaticString.noImage)){
//            Picasso.get().load(productBio.getImageRef()).into(holder.productImageView);
            Glide.with(holder.itemView.getContext()).load(productBio.getImageRef()).into(holder.productImageView);
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.productRating).child(productBio.getBarCode());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Rating rating = snapshot.getValue(Rating.class);
                try {
                    holder.scanCount.setText(String.valueOf(rating.countView));
                    float v = 0;
                    if(rating.countRating != 0) {
                        v = rating.rating / (float) rating.countRating;
                    }
                    holder.ratingScore.setText(v + "  (" + rating.countRating + ')');
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
        TextView productName,scanCount, ratingScore, barCode;
        ImageView productImageView;
        RelativeLayout edit;
        public ViewHolder(View view) {
            super(view);
            edit = view.findViewById(R.id.edit);
            barCode = view.findViewById(R.id.barCode);
            ratingBar = view.findViewById(R.id.rating_bar);
            productName = view.findViewById(R.id.product_name);
            scanCount = view.findViewById(R.id.count);
            ratingScore = view.findViewById(R.id.rating_score);
            productImageView = view.findViewById(R.id.product_image_view);

            edit.setOnClickListener(v -> {
                Intent intent = new Intent(view.getContext(), Product_activityBioEdit.class);
                intent.putExtra(StaticString.barCode,barCode.getText().toString());
                view.getContext().startActivity(intent);
            });
            productImageView.setOnClickListener(v -> {
                Intent intent = new Intent(view.getContext(), Read.class);
                intent.putExtra(StaticString.barCode,barCode.getText().toString());
                intent.putExtra(StaticString.type,StaticString.company);
                view.getContext().startActivity(intent);
            });
        }

    }


    public ViewAdapterCompany.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_company,parent,false);
        return new ViewAdapterCompany.ViewHolder(view);
    }


}
