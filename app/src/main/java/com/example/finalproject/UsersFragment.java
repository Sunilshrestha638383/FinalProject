package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.SearchView;

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

import com.example.finalproject.adapters.AdapterUser;
import com.example.finalproject.models.ModelUsrs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    AdapterUser adapterUser;
    FirebaseAuth firebaseAuth;
    List<ModelUsrs> usrsList;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_users, container, false);

        firebaseAuth =FirebaseAuth.getInstance();

        recyclerView = view.findViewById(R.id.users_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        usrsList = new ArrayList<>();

        getAllUsers();

        return view;

    }

    private void getAllUsers() {
       final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usrsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUsrs modelUsrs = ds.getValue(ModelUsrs.class);

                    if(!modelUsrs.getUid().equals(fuser.getUid())) {
                        usrsList.add(modelUsrs);
                    }
                        adapterUser = new AdapterUser(getActivity(),usrsList);
                        recyclerView.setAdapter(adapterUser);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUser(String query) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usrsList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelUsrs modelUsrs = ds.getValue(ModelUsrs.class);

                    if(!modelUsrs.getUid().equals(fuser.getUid())) {

                        if (modelUsrs.getName().toLowerCase().contains(query.toLowerCase()) ||
                                modelUsrs.getEmail().toLowerCase().contains(query.toLowerCase())) {
                            usrsList.add(modelUsrs);

                        }
                    }
                        adapterUser = new AdapterUser(getActivity(),usrsList);
                    adapterUser.notifyDataSetChanged();
                        recyclerView.setAdapter(adapterUser);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);

        menu.findItem(R.id.action_add_post).setVisible(false);
        menu.findItem(R.id.action_Classify).setVisible(false);


        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                if(!TextUtils.isEmpty(s.trim())){
                    searchUser(s);

                }else{
                    getAllUsers();
                }

                return false;

            }

            @Override
            public boolean onQueryTextChange(String s) {

                if(!TextUtils.isEmpty(s.trim())){
                    searchUser(s);

                }else{
                    getAllUsers();
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
//        if(id == R.id.action_uploadimage){
//            Intent intent = new Intent(getActivity(),MainActivity.class);
//            startActivity(intent);
//        }
        return super.onOptionsItemSelected(item);



    }
}