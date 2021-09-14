package com.example.spokenglovesapp;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.DatabaseMetaData;
import java.util.ArrayList;

public class SignsFragment extends Fragment {
    ListView signList;
    ArrayList<valuesave>data;
    Button btnAdd;
    SignsListAdapter signsListAdapter;
    DatabaseReference database;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_signs, container, false);
        signList=view.findViewById(R.id.signList);
      MacAddress myMacAddress=new MacAddress();
        final String macadress = myMacAddress.getMacAddr();
        data=new ArrayList<valuesave>();
        database= FirebaseDatabase.getInstance().getReference().child("databaase").child(macadress);
        database.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        for(DataSnapshot datasnap:snapshot.getChildren())
        {
            valuesave valuesave=datasnap.getValue(valuesave.class);
            data.add(valuesave);
        }
    }
    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
});

        signsListAdapter=new SignsListAdapter(getContext(),data);
        signList.setAdapter(signsListAdapter);
        signList.setAdapter(signsListAdapter);
        btnAdd=view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                AddDialog addDialog = new AddDialog();
                addDialog.show(manager, null);



            }
        });
        return view;
    }
}