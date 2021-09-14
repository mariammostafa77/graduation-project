package com.example.spokenglovesapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class SignsListAdapter extends ArrayAdapter<valuesave> {
    ArrayList<valuesave> data;
    Context context;
    Integer index;
    AlertDialog.Builder builder;
    String currentuser,sign,bitmap,id;
    DatabaseReference datasave ;
    WifiManager wifi;
    DatabaseReference mdata;
    private FirebaseAuth auth;
    public SignsListAdapter(Context context,ArrayList<valuesave>prodects) {
        super(context, 0, prodects);
        this.context = context;
        this.data=prodects;
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final valuesave value= (valuesave) getItem(position);
        LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View row=inflater.inflate(R.layout.sign_list_item,parent,false);
        final TextView tvName=row.findViewById(R.id.etSignName);
        final ImageView img=row.findViewById(R.id.img);
        wifi = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        //WifiInfo info = wifi.getConnectionInfo();
        MacAddress myMacAddress=new MacAddress();
        final String macadress = myMacAddress.getMacAddr();
       datasave= FirebaseDatabase.getInstance().getReference("databaase").child(macadress);
        Button btnDelete=row.findViewById(R.id.btnDeleteUser);
        btnDelete.setTag(position);
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), editfinal.class);
                intent.putExtra("name", tvName.getText());
                if(value.getName()==tvName.getText())
                {
                    sign=value.getValue();
                    bitmap=value.getImagename();
                    id=value.getId();
                }
                intent.putExtra("image", bitmap);
                intent.putExtra("value", sign);
                intent.putExtra("id", id);
                context.startActivity(intent);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index = (Integer) v.getTag();
                builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.dialog_message2)
                        .setCancelable(false)
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                value.getId();
                                FirebaseStorage storageReference;
                                storageReference = FirebaseStorage.getInstance("gs://garduate.appspot.com");
                                datasave.child(value.getId()).removeValue();
                                final StorageReference storag=storageReference.getReference().child(value.getName());
                                storag.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                    }
                                });
                                data.remove(index.intValue());
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        final valuesave content=data.get(position);
        tvName.setText(value.getName());
        sign=value.getValue();
        Glide.with(context).load(Uri.parse(value.getImagename())).into(img);
        return row;
    }
}
