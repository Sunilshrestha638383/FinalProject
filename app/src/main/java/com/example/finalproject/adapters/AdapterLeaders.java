package com.example.finalproject.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.R;
import com.example.finalproject.models.ModelLeaders;
import com.example.finalproject.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AdapterLeaders extends RecyclerView.Adapter<AdapterLeaders.MyHolder>{

    Context context;
    List<ModelLeaders> leadersList;

    String myUid;
    private DatabaseReference likesRef;
    private DatabaseReference postsRef1;
    boolean mProcessLike=false;

    public AdapterLeaders(Context context, List<ModelLeaders> leadersList) {
        this.context = context;
        this.leadersList = leadersList;

        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef1 = FirebaseDatabase.getInstance().getReference().child("Posts");

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.leader, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        String uid = leadersList.get(position).getUid();
        String uEmail = leadersList.get(position).getuEmail();
        String uName = leadersList.get(position).getuName();
        String uDp = leadersList.get(position).getuDp();
        String pId = leadersList.get(position).getpId();
        String pTitle = leadersList.get(position).getpTitle();
        String pDescription = leadersList.get(position).getpDescription();
        String pImage = leadersList.get(position).getpImage();
        String pTimeStamp = leadersList.get(position).getpTime();
        String pLikes = leadersList.get(position).getpLikes();




        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("hh/MM/YYYY hh:mm aaaa",calendar).toString();

        holder.lNameTv.setText(uName);
//        holder.pTimeTv.setText(pTime);
       holder.lTitleTv.setText(pTitle);
//        holder.pDescriptionTv.setText(pDescription);

        holder.lLikesTv.setText(pLikes+"Likes");

//        setLikes(holder, pId);




        try {
            Picasso.get().load(uDp).placeholder(R.drawable.ic_default_image_green).into(holder.lPictureIv);

        }catch (Exception e){


        }

        if(pImage.equals("noImage")){

            holder.pImageIv.setVisibility(View.GONE);

        }else {
            try {
                Picasso.get().load(pImage).into(holder.pImageIv);
            }catch (Exception e){

            }

        }



//        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(context, "more",Toast.LENGTH_LONG).show();
//            }
//        });
//        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//                int pLikes = Integer.parseInt(leadersList.get(position).getpLikes());
//                mProcessLike = true;
//
//                //get id of post liked
//                String postIde = leadersList.get(position).getpId();
//                likesRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                        if(mProcessLike){
//                            if(snapshot.child(postIde).hasChild(myUid)){
//                                postsRef1.child(postIde).child("pLikes").setValue(""+(pLikes-1));
//                                likesRef.child(postIde).child(myUid).removeValue();
//                                mProcessLike = false;
//                            }else {
//                                postsRef1.child(postIde).child("pLikes").setValue(""+(pLikes+1));
//                                likesRef.child(postIde).child(myUid).setValue("Liked");
//                                mProcessLike = false;
//                            }
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//            }
//        });


    }

//    private void setLikes(MyHolder holder1, String postKey) {
//
//        likesRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                if(snapshot.child(postKey).hasChild(myUid)){
//                    holder1.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vote_green,0, 0 ,0);
//                    holder1.likeBtn.setText("Liked");
//                }else{
//                    holder1.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_vote,0, 0 ,0);
//                    holder1.likeBtn.setText("Like");
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    @Override
    public int getItemCount() {
        return leadersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        ImageView lPictureIv, pImageIv;
        TextView lNameTv, pTimeTv, lTitleTv, pDescriptionTv,lLikesTv;
        ImageButton moreBtn;
        Button likeBtn, commentBtn, shareBtn;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            lPictureIv = itemView.findViewById(R.id.lPictureIv);
            lNameTv = itemView.findViewById(R.id.lNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            lTitleTv = itemView.findViewById(R.id.lTitle);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            lLikesTv = itemView.findViewById(R.id.lLikesTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);





        }
    }

}
