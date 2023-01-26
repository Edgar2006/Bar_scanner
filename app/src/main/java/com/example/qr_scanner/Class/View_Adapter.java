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
        holder.count.setText(Integer.toString(friend.getFriend_list().size()));
        holder.mAuth = FirebaseAuth.getInstance();
        holder.database = FirebaseDatabase.getInstance();
        holder.reference  = FirebaseDatabase.getInstance().getReference("Product").child(holder.address).child(holder.email.getText().toString().replace(".", "|"));
        holder.friend_ref  = FirebaseDatabase.getInstance().getReference("Friends").child(holder.address).child(holder.email.getText().toString().replace(".", "|"));
        holder.list_friend = friend.getFriend_list();
        holder.count.setText(String.valueOf(friend.getFriend_list().size()));
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
        DatabaseReference reference,friend_ref;
        FirebaseDatabase database;
        ArrayList<String> list_friend;

        public ViewHolder(View view) {
            super(view);
            email = view.findViewById(R.id.name);
            comment = view.findViewById(R.id.comment);
            count = view.findViewById(R.id.count);
            like = view.findViewById(R.id.like);
            Toast.makeText(view.getContext(), User.EMAIL, Toast.LENGTH_SHORT).show();

            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    friend_ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!list_friend.isEmpty()){list_friend.clear();}
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                String data = snapshot.getValue(String.class);
                                list_friend.add(data);
                            }
                            //Log.d("_0:",list_friend.toString());
                            //Toast.makeText(view.getContext(), list_friend.toString(), Toast.LENGTH_SHORT).show();
                            int index = 0;
                            for(int i=0;i<list_friend.size();i++){
                                if(User.EMAIL.equals(list_friend.get(i))){
                                    Toast.makeText(view.getContext(), "fuckkkkkkkkkkkkkkkkkk", Toast.LENGTH_SHORT).show();
                                    list_friend.remove(i);
                                    index=1;
                                    i--;
                                }
                                else {
                                    Toast.makeText(view.getContext(), "suka" + list_friend.get(i), Toast.LENGTH_SHORT).show();
                                }
                            }
                            if(index == 0){
                                list_friend.add(User.EMAIL);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Getting Post failed, log a message
                            Log.w("TAG:", "loadPost:onCancelled", databaseError.toException());
                        }
                    });
                    //Log.d("_1:",list_friend.toString());
                    HashSet<String> hashSet = new HashSet<>(list_friend);
                    list_friend = new ArrayList<String>(hashSet);
                    friend_ref.setValue(list_friend);
                    add_messenger();
                }
            });

        }
        private void add_messenger(){
            Messenger newMessenger = new Messenger(address,comment.getText().toString(),email.getText().toString());
            Map<String,Object> map = new HashMap<>();
            map.put("address",newMessenger.getAddress());
            map.put("comment",newMessenger.getComment());
            map.put("email",newMessenger.getEmail());
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
