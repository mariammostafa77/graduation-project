package com.example.spokenglovesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


public class editfinal extends Activity {
    public static ProgressDialog progress;

    TextView etname,etvalue;
    String name,value,id;
    String bitmap;
    ImageView img;
    Bitmap imageBitmap;
    Button editbu,update,addimage;
    DatabaseReference datasave ;
    WifiManager wifi;
    Uri  uri;
    int REQUEST_IMAGE_CAPTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editfinal);
        etname = findViewById(R.id.eteditSignName);
        etvalue = findViewById(R.id.eteditValue);
        img = findViewById(R.id.editimageView);
        addimage = findViewById(R.id.addimagebtn);
        name = getIntent().getStringExtra("name");
        value = getIntent().getStringExtra("value");
        bitmap = getIntent().getStringExtra("image");
        id = getIntent().getStringExtra("id");
       MacAddress myMacAddress= new MacAddress();
        final String macadress = myMacAddress.getMacAddr();;
        datasave = FirebaseDatabase.getInstance().getReference("databaase").child(macadress);

        uri = Uri.parse(bitmap);
        Glide.with(this).load(uri).into(img);
        etname.setText(name);
        etvalue.setText(value);
        editbu = findViewById(R.id.geteditvalue);
        editbu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new connect().execute();
            }
        });
        update = findViewById(R.id.btnedit);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploade();
            }
        });
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
            if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                //  Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) data.getExtras().get("data");
                img.setImageBitmap(imageBitmap);
            }
        }




    private void uploade( ) {
        FirebaseStorage storageReference;
        storageReference = FirebaseStorage.getInstance("gs://garduate.appspot.com");
        final StorageReference storag=storageReference.getReference().child(etname.getText().toString());
        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte [] b=stream.toByteArray();
        storag.putBytes(b)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        storag.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                if (uri == null)
                                {
                                    Toast.makeText(editfinal.this,"no",Toast.LENGTH_LONG).show();
                                }
                                else{

                                    valuesave value = new valuesave(etvalue.getText().toString(), etname.getText().toString(),uri.toString(),id);

                                    datasave.child(id).setValue(value);
                                    img.setImageResource(0);
                                    etname.setText("");
                                    etvalue.setText("");
                                    Intent intent=new Intent(editfinal.this,SignsListAdapter.class);
                                    startActivity(intent);




                                }
                            }
                        });


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });

    }
    @SuppressLint("StaticFieldLeak")
    public class connect extends AsyncTask<Void, Void, BluetoothSocket>  // UI thread
    {
        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(editfinal.this,
                    "Connecting...",
                    "Please wait!!!");  //show a progress dialog

        }

        @Override
        protected BluetoothSocket doInBackground(Void... voids) {
            BluetoothSocket btSocket = finalbtstock.DataHolder.getData();
            return btSocket;
        }
        protected void onPostExecute(BluetoothSocket btSocket) {
            super.onPostExecute(btSocket);
            InputStream inputStream = null;
            try {
                inputStream = btSocket.getInputStream();
                inputStream.skip(inputStream.available());
                byte[] buffer = new byte[1024];
                int bytes, i;
                i = 0;
                String mm;
                while (i<1) {
                    etname.setText("");
                    bytes = inputStream.read(buffer);
                    mm = new String(buffer,0,bytes);
                    System.out.print(mm);
                    etname.append(mm);
                    i++;

             /*       while (i <= 1) {
                        String mm;
                     //   bytes = inputStream.read(buffer);
                        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                        //r.ready();
                        r.skip(20);
                        mm =r.readLine();
                        etValue.append(mm);
                        i++;
                        r.close();
                    }*/
                    progress.dismiss();
                }

            }
            catch( IOException e){
                e.printStackTrace();
            }

        }
    }
}