package com.example.qr_scanner.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.DataBase_Class.MyBool;
import com.example.qr_scanner.DataBase_Class.ProductBio;
import com.example.qr_scanner.DataBase_Class.User;
import com.example.qr_scanner.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewAdapterCompany extends RecyclerView.Adapter<ViewAdapterCompany.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<ProductBio> messengers;
    public ViewAdapterCompany(Context context, ArrayList<ProductBio> messengers) {
        this.messengers = messengers;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAdapterCompany.ViewHolder holder, int position) {
        ProductBio messenger = messengers.get(position);

    }

    @Override
    public int getItemCount() {
        return messengers.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        String dateString;
        RatingBar ratingBar;
        TextView email,comment,count,time_text;
        //
        String emailToString;
        String name;
        long time;
        //
        ImageButton like;
        ImageView imageDataBase,userImage;
        String address;
        String uploadUri;
        String userImageUrl;
        FirebaseAuth mAuth;
        DatabaseReference reference, friendRef, likeRef;
        FirebaseDatabase database;
        int size;
        public ViewHolder(View view) {
            super(view);
            ratingBar = view.findViewById(R.id.ratingBar);
            email = view.findViewById(R.id.name);
            comment = view.findViewById(R.id.comment);
            count = view.findViewById(R.id.count);
            like = view.findViewById(R.id.like);
            time_text = view.findViewById(R.id.time);
            imageDataBase = view.findViewById(R.id.imageDataBase);
            userImage = view.findViewById(R.id.user_image);
            size = 0;
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean b = false;
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                if(data.getKey().toString().equals(User.EMAIL_CONVERT)){
                                    b=true;
                                    MyBool isLike = data.getValue(MyBool.class);
                                    boolean isOk = isLike.isLike();
                                    likeRef.child(User.EMAIL_CONVERT).setValue(new MyBool(!isOk));
                                    int second = Integer.parseInt(count.getText().toString());
                                    if(!isOk){
                                        second++;
                                        count.setText(Integer.toString(second));
                                    }
                                    else{
                                        second--;
                                    }
                                    count.setText(Integer.toString(second));
                                    addMessenger();

                                }
                            }
                            if(!b){
                                likeRef.child(User.EMAIL_CONVERT).setValue(new MyBool(true));
                                int second = Integer.parseInt(count.getText().toString());
                                second++;
                                count.setText(Integer.toString(second));
                                addMessenger();

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            });
        }
        private void addMessenger(){
            Messenger newMessenger = new Messenger(emailToString,name.toString(),comment.getText().toString(),address,count.getText().toString(),"notImage","notImage",time,ratingBar.getRating());
            if(userImageUrl != null){
                newMessenger.setUserRef(userImageUrl);
            }
            if(uploadUri != null){
                newMessenger.setImageRef(uploadUri);
            }
            reference.setValue(newMessenger);
        }

    }


    public ViewAdapterCompany.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_home,parent,false);
        return new ViewAdapterCompany.ViewHolder(view);
    }

}
