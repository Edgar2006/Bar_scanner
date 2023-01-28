package com.example.qr_scanner.Class;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qr_scanner.R;
import com.example.qr_scanner.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class View_Adapter extends RecyclerView.Adapter<View_Adapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Friend> friends;
    public View_Adapter(Context context, ArrayList<Friend> friends) {
        this.friends = friends;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend friend = friends.get(position);
        holder.address = friend.getMessenger().getAddress();
        holder.email.setText(friend.getMessenger().getEmail());
        holder.comment.setText(friend.getMessenger().getComment());
        holder.count.setText(friend.getMessenger().getCount());
        holder.mAuth = FirebaseAuth.getInstance();
        holder.database = FirebaseDatabase.getInstance();
        holder.reference  = FirebaseDatabase.getInstance().getReference("Product").child(holder.address).child(holder.email.getText().toString().replace(".", "|"));
        holder.friend_ref  = FirebaseDatabase.getInstance().getReference("Friends").child(holder.address).child(holder.email.getText().toString().replace(".", "|"));
        holder.like_ref = FirebaseDatabase.getInstance().getReference("Friends").child(holder.address).child(holder.email.getText().toString().replace(".", "|"));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView email,comment,count;
        ImageButton like;
        String address;
        FirebaseAuth mAuth;
        DatabaseReference reference,friend_ref,like_ref;
        FirebaseDatabase database;
        int size;
        public ViewHolder(View view) {
            super(view);
            email = view.findViewById(R.id.name);
            comment = view.findViewById(R.id.comment);
            count = view.findViewById(R.id.count);
            like = view.findViewById(R.id.like);
            like_ref = friend_ref;
            size = 0;
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friend_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            boolean b = false;
                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                Toast.makeText(view.getContext(), "_-_" + data.getKey().toString(), Toast.LENGTH_SHORT).show();
                                if(data.getKey().toString().equals(User.EMAIL_CONVERT)){
                                    b=true;
                                    MyBool isLike = data.getValue(MyBool.class);
                                    boolean isOk = isLike.isLike();
                                    like_ref.child(User.EMAIL_CONVERT).setValue(new MyBool(!isOk));
                                    int second = Integer.parseInt(count.getText().toString());
                                    if(!isOk){
                                        second++;
                                        count.setText(Integer.toString(second));
                                    }
                                    else{
                                        second--;
                                    }
                                    count.setText(Integer.toString(second));
                                    add_messenger();

                                }
                            }
                            if(!b){
                                like_ref.child(User.EMAIL_CONVERT).setValue(new MyBool(true));
                                int second = Integer.parseInt(count.getText().toString());
                                second++;
                                count.setText(Integer.toString(second));
                                add_messenger();

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
            });
        }
        private void add_messenger(){
            Messenger newMessenger = new Messenger(email.getText().toString(),comment.getText().toString(),address,count.getText().toString());
            Map<String,Object> map = new HashMap<>();
            map.put("email",newMessenger.getEmail());
            map.put("comment",newMessenger.getComment());
            map.put("address",newMessenger.getAddress());
            map.put("count",newMessenger.getCount());
            reference.updateChildren(map);
        }
        private int find(int size,ArrayList<String> friends,String email){
            for(int i=0;i<size;i++){
                if(friends.get(i) == email){
                    return i;
                }
            }
            return -1;
        }
    }



    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view);
    }

}
