package com.example.qr_scanner.Adapter;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.example.qr_scanner.Activity.All.OpenImageActivity;
import com.example.qr_scanner.Activity.User.UserAllCommentShowActivity;
import com.example.qr_scanner.Class.Function;
import com.example.qr_scanner.Class.ModelLanguage;
import com.example.qr_scanner.Class.StaticString;
import com.example.qr_scanner.DataBase_Class.Messenger;
import com.example.qr_scanner.DataBase_Class.MyBool;
import com.example.qr_scanner.R;
import com.example.qr_scanner.DataBase_Class.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.io.Resources;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ViewAdapter extends RecyclerView.Adapter<ViewAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<Messenger> messengers;
    public ViewAdapter(Context context, ArrayList<Messenger> messengers) {
        this.messengers = messengers;
        this.inflater = LayoutInflater.from(context);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Messenger messenger = messengers.get(position);
        holder.address = messenger.getAddress();
        holder.email.setText(messenger.getName());
        holder.emailToString = messenger.getEmail();
        String temp = Function.CONVERTOR(holder.emailToString);
        holder.name = messenger.getName();
        holder.comment.setText(messenger.getComment().toString().trim());
        holder.sourceLanguageText = messenger.getComment().toString().trim();
        holder.count.setText(messenger.getCount());
        holder.mAuth = FirebaseAuth.getInstance();
        holder.database = FirebaseDatabase.getInstance();
        holder.time = messenger.getTime();
        holder.reference  = FirebaseDatabase.getInstance().getReference(StaticString.product).child(holder.address).child(temp);
        holder.friendRef = FirebaseDatabase.getInstance().getReference(StaticString.friends).child(holder.address).child(temp);
        holder.likeRef = FirebaseDatabase.getInstance().getReference(StaticString.friends).child(holder.address).child(temp);
        holder.uploadUri = messenger.getImageRef();
        holder.userImageUrl = messenger.getUserRef();
        holder.ratingBar.setRating(messenger.getRatingBarScore());
        holder.ratingBarScore.setText(Float.toString(messenger.getRatingBarScore()));
        holder.friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    if(data.getKey().equals(User.EMAIL_CONVERT)){
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
        if(!Objects.equals(holder.uploadUri, StaticString.noImage)) {
            Glide.with(holder.itemView.getContext()).load(holder.uploadUri).into(holder.imageDataBase);
        }
        else{
            holder.imageDataBase.setVisibility(View.GONE);
            holder.comment.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        }
        if(!Objects.equals(holder.userImageUrl, StaticString.noImage)) {
            Glide.with(holder.itemView.getContext()).load(holder.userImageUrl).into(holder.userImage);
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
        TextView email,comment,count,time_text,ratingBarScore,translateView;
        String emailToString,name,address,uploadUri,userImageUrl;
        long time;
        ImageButton like;
        ImageView imageDataBase,userImage,more;
        FirebaseAuth mAuth;
        DatabaseReference reference, friendRef, likeRef;
        FirebaseDatabase database;
        RelativeLayout userClick;
        int size;


        private TranslatorOptions transitionOptions;
        private Translator translator;
        private ProgressDialog progressDialog;
        private ArrayList<ModelLanguage> languageArrayList;

        private String sourceLanguageCode = "en";
        private String sourceLanguageTitle = "English";
        private String destinationLanguageCode = "ur";
        private String destinationLanguageTitle = "Urdu";
        private String sourceLanguageText;


        public ViewHolder(View view) {
            super(view);
            init(view);

            like.setOnClickListener(view1 -> friendRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean b = false;
                    for(DataSnapshot data: dataSnapshot.getChildren()){
                        if(data.getKey().equals(User.EMAIL_CONVERT)){
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
            }));
            userClick.setOnClickListener(v -> {
                Intent intent = new Intent(view.getContext(), UserAllCommentShowActivity.class);
                intent.putExtra(StaticString.email,emailToString);
                intent.putExtra(StaticString.user,name);
                intent.putExtra(StaticString.userImage,userImageUrl);
                view.getContext().startActivity(intent);
            });
            imageDataBase.setOnClickListener(v -> {
                Intent intent = new Intent(view.getContext(), OpenImageActivity.class);
                intent.putExtra(StaticString.url,uploadUri);
                view.getContext().startActivity(intent);
            });
            more.setOnClickListener(v -> deleteComment(view));

            translateView.setOnClickListener(v -> {
                if (!Objects.equals(sourceLanguageText, comment.getText().toString())){
                    comment.setText(sourceLanguageText);
                    translateView.setText(R.string.translate);
                }
                else {
                    translateView.setText(R.string.see_original);
                    getTextLanguageIdentifier(sourceLanguageText);
                    destinationLanguageCode = Locale.getDefault().getLanguage();
                    Toast.makeText(itemView.getContext(), "My Language " + destinationLanguageCode, Toast.LENGTH_SHORT).show();
                    loadAvailableLanguages();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        startTranslations();
                    }, 100);
                }
            });

        }

        private void init(View view){
            translateView = view.findViewById(R.id.translate);
            ratingBarScore = view.findViewById(R.id.rating_bar_score);
            more = view.findViewById(R.id.more);
            userClick = view.findViewById(R.id.rel1);
            ratingBar = view.findViewById(R.id.rating_bar);
            email = view.findViewById(R.id.name);
            comment = view.findViewById(R.id.comment);
            count = view.findViewById(R.id.count);
            like = view.findViewById(R.id.like);
            time_text = view.findViewById(R.id.time);
            imageDataBase = view.findViewById(R.id.image_data_base);
            userImage = view.findViewById(R.id.user_image);
            size = 0;
            progressDialog = new ProgressDialog(view.getContext());
            progressDialog.setTitle("Please wait");
            progressDialog.setCanceledOnTouchOutside(false);
        }


        private void getTextLanguageIdentifier(String text){
            LanguageIdentifier languageIdentifier =
                    LanguageIdentification.getClient();
            languageIdentifier.identifyLanguage(text)
                    .addOnSuccessListener(
                            languageCode -> {
                                if (languageCode.equals("und")) {
                                    Toast.makeText(itemView.getContext(), "Can't identify language.", Toast.LENGTH_SHORT).show();
                                } else {
                                    sourceLanguageCode = languageCode;
                                    Toast.makeText(itemView.getContext(), "Language " + languageCode, Toast.LENGTH_SHORT).show();
                                }
                            })
                    .addOnFailureListener(
                            e -> {
                                Toast.makeText(itemView.getContext(), "Can't identify language.", Toast.LENGTH_SHORT).show();
                            });
        }



        private void  loadAvailableLanguages(){
            languageArrayList = new ArrayList<>();
            List<String> languageCodeList = TranslateLanguage.getAllLanguages();
            for (String languageCode : languageCodeList){
                String languageTitle = new Locale(languageCode).getDisplayLanguage();

                Log.d(TAG,"loadAvailableLanguages: languageCode: "+languageCode);
                Log.d(TAG,"LoadAvailableLanguages: LanguageTitle: "+languageTitle);
                ModelLanguage modelLanguage = new ModelLanguage (languageCode, languageTitle);
                languageArrayList.add(modelLanguage);


            }
        }

        private void startTranslations(){
            progressDialog.setMessage("Processing language model...");
            progressDialog.show();


            transitionOptions = new TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLanguageCode)
                    .setTargetLanguage(destinationLanguageCode)
                    .build();
            translator = Translation.getClient(transitionOptions);

            DownloadConditions downloadConditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();

            translator.downloadModelIfNeeded(downloadConditions)
                    .addOnSuccessListener(unused -> {
                        Log.e(TAG, "onSuccess: model ready, starting translate...");
                        progressDialog.setMessage("Translating...");
                        translator.translate(sourceLanguageText)
                                .addOnSuccessListener(s -> {
                                    progressDialog.dismiss();
                                    comment.setText(s);
                                    Toast.makeText(itemView.getContext(), s , Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Log.e(TAG, e.getMessage());
                                    Log.e(TAG, e.getLocalizedMessage());

                                    Toast.makeText(itemView.getContext(), "Failed to translate due to " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Log.e(TAG, e.getMessage());
                        Log.e(TAG, e.getLocalizedMessage());

                        Toast.makeText(itemView.getContext(), "Failed to ready model due to " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    });
        }









        private void deleteComment(View view){
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(view.getContext());
            builder.setMessage("Do you want delete comment?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        Toast.makeText(view.getContext(), "Thank you " + User.EMAIL, Toast.LENGTH_SHORT).show();
                        if(!Objects.equals(User.EMAIL, emailToString)) {
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.deleteComment).child(address);
                            Messenger newMessenger = new Messenger(emailToString, name, comment.getText().toString(), address, count.getText().toString(), StaticString.noImage, StaticString.noImage, time, ratingBar.getRating());
                            if (userImageUrl != null) {
                                newMessenger.setUserRef(userImageUrl);
                            }
                            if (uploadUri != null) {
                                newMessenger.setImageRef(uploadUri);
                            }
                            reference.setValue(newMessenger);
                        }
                        else{
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(StaticString.product).child(address);
                            reference.removeValue();
                        }
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        dialog.cancel();
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("Do you want delete comment");
            alert.show();
        }
        private void addMessenger(){
            Messenger newMessenger = new Messenger(emailToString, name,comment.getText().toString(),address,count.getText().toString(),StaticString.noImage,StaticString.noImage,time,ratingBar.getRating());
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
