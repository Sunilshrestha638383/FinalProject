package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.finalproject.adapters.AdapterLeaders;
import com.example.finalproject.adapters.AdapterPost;
import com.example.finalproject.models.ModelLeaders;
import com.example.finalproject.models.ModelPost;
import com.example.finalproject.models.ModelUsrs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class LeaderboardFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    SharedPreferences sharedPreferences;

    RecyclerView recyclerView;
    List<ModelLeaders> leadersList;
    AdapterLeaders adapterLeaders;

    public LeaderboardFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leaderbaord, container, false);

        firebaseAuth =FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.leader_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        leadersList = new ArrayList<>();


        loadLeaders();


        return view;
    }

    private void loadLeaders() {



        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");

        Query mostLikedposts = ref.orderByChild("pLikes");

        mostLikedposts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leadersList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelLeaders modelLeaders = ds.getValue(ModelLeaders.class);


                    leadersList.add(modelLeaders);

                    adapterLeaders = new AdapterLeaders(getActivity(),leadersList);
                    recyclerView.setAdapter(adapterLeaders);


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private  void searchPost(final String searchQuery){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leadersList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModelLeaders modelLeaders = ds.getValue(ModelLeaders.class);

                    if(modelLeaders.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            modelLeaders.getpDescription().toLowerCase().contains(searchQuery.toLowerCase())){
                        leadersList.add(modelLeaders);
                    }



                    adapterLeaders = new AdapterLeaders(getActivity(),leadersList);
                    recyclerView.setAdapter(adapterLeaders);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getActivity(), ""+error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();

        if(user!=null){
            //mUserProfile.setText(user.getEmail());

        }
        else{
            startActivity(new Intent(getActivity(),Login.class));
            getActivity().finish();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("sort Setting", Context.MODE_PRIVATE);
        String mSorting = sharedPreferences.getString("sort","newest");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                if(!TextUtils.isEmpty(s)){
                    searchPost(s);
                }else{
                    loadLeaders();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(!TextUtils.isEmpty(s)){
                    searchPost(s);
                }else{
                    loadLeaders();
                }
                return false;
            }
        });

        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.action_logout){
            firebaseAuth.signOut();
            checkUserStatus();
        }
        if(id == R.id.action_Classify){
            Intent intent = new Intent(getActivity(),AutoTitle.class);
            startActivity(intent);
        }

        if(id == R.id.action_add_post){

            startActivity(new Intent(getActivity(),AddPostActivity.class));
        }

        return super.onOptionsItemSelected(item);



    }
}