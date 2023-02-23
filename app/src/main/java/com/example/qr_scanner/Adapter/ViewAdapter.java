package com.example.qr_scanner.Adapter;

import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.DataBase_Class.MyBool;
import com.example.qr_scanner.R;
import com.example.qr_scanner.DataBase_Class.User;
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

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Messenger> messengers;
    public ViewAdapter(Context context, ArrayList<Messenger> messengers) {
        this.messengers = messengers;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Messenger messenger = messengers.get(position);
        holder.address = messenger.getAddress();
        holder.email.setText(messenger.getName());
        holder.emailToString = messenger.getEmail();
        String temp = Function.convertor(holder.emailToString);
        holder.name = messenger.getName();
        holder.comment.setText(messenger.getComment());
        holder.count.setText(messenger.getCount());
        holder.mAuth = FirebaseAuth.getInstance();
        holder.database = FirebaseDatabase.getInstance();
        holder.time = messenger.getTime();
        holder.reference  = FirebaseDatabase.getInstance().getReference("Product").child(holder.address).child(temp);
        holder.friendRef = FirebaseDatabase.getInstance().getReference("Friends").child(holder.address).child(temp);
        holder.likeRef = FirebaseDatabase.getInstance().getReference("Friends").child(holder.address).child(temp);
        holder.uploadUri = messenger.getImageRef();
        holder.userImageUrl = messenger.getUserRef();
        holder.ratingBar.setRating(messenger.getRatingBarScore());
        holder.friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    if(data.getKey().toString().equals(User.EMAIL_CONVERT)){
                        MyBool isLike = data.getValue(MyBool.class);
                        boolean isOk = isLike.isLike();
                        if(!isOk){
                            holder.like.setImageResource(R.drawable.dislike);
                        }
                        else{
                            holder.like.setImageResource(R.drawable.like);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        if(!Objects.equals(holder.uploadUri, "noImage")) {
            Picasso.get().load(messenger.getImageRef()).into(holder.imageDataBase);
        }
        else{
            holder.imageDataBase.setVisibility(View.GONE);
            holder.comment.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;

        }
        if(!Objects.equals(holder.userImageUrl, "noImage")) {
            Picasso.get().load(holder.userImageUrl).into(holder.userImage);
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MMMM d, yyyy");
        holder.dateString = formatter.format(new Date(messenger.getTime()));
        holder.time_text.setText(holder.dateString);

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

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

}
