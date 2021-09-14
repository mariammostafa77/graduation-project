package com.example.spokenglovesapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class Chat extends AppCompatActivity {

    EditText etMessage;
    FloatingActionButton fab;
    TextView nameOfUser;
    Button glovesData;
    static BluetoothAdapter btAdapter;

    public static String message;
    private static final int REQIEST_CODE_SPEACH_INPUT = 1000;
    ImageView imgBack,userImg;
    public static ProgressDialog progress;

    private  ArrayList<Message>messagesList=new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;
    private DatabaseReference databaseReference,df;
    String currentChatId;

    String mac_address,currentUserName,currentUserUrl;
    Query userInfoQuery;
    Query glovesDataQuery;

    MacAddress myMacAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        fab = findViewById(R.id.fab);
        etMessage = findViewById(R.id.etMessage);
        imgBack=findViewById(R.id.imgBack);
        nameOfUser=findViewById(R.id.nameOfUser);
        userImg=findViewById(R.id.userImg);
        glovesData=findViewById(R.id.glovesData);
        myMacAddress=new MacAddress();


        btAdapter = BluetoothAdapter.getDefaultAdapter();

        glovesData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btAdapter.enable()) {
                    new connect().execute();
                }

                else {
                    progress = ProgressDialog.show(Chat.this, "Connecting...", "Please wait!!!");  //show a progress dialog
                    Toast.makeText(Chat.this, "dia", Toast.LENGTH_LONG).show();
                    btAdapter.enable();
                    new connect().execute();
                }

            }
        });

        userMessagesList=findViewById(R.id.messageRecycle);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        messagesList=new ArrayList<>();



        Bundle bundle=getIntent().getExtras();
        currentChatId=bundle.getString("id");
        currentUserName=bundle.getString("name");
        currentUserUrl=bundle.getString("url");
        nameOfUser.setText(currentUserName);
        databaseReference= FirebaseDatabase.getInstance().getReference("MessagesDetails");
        userInfoQuery=databaseReference.orderByChild("chatId").equalTo(currentChatId);

        //Picasso.get().load(currentUserUrl).fit().centerCrop().into(userImg);


        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Chat.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Chat.this.finish();
            }
        });
        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                checkEditText();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkEditText();
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkEditText();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = etMessage.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    getSpeechInput(getCurrentFocus());
                } else {
                    sendMessage();
                    etMessage.setText("");
                    glovesData.setVisibility(View.VISIBLE);


                }
            }
        });

    }

    public void checkEditText() {
        message = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_keyboard_voice_24));
            glovesData.setVisibility(View.VISIBLE);

        } else {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_send_24));
            glovesData.setVisibility(View.INVISIBLE);

        }
    }

    public void getSpeechInput(View view) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");
        try {
            startActivityForResult(intent, REQIEST_CODE_SPEACH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQIEST_CODE_SPEACH_INPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etMessage.setText(result.get(0));
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        retrieveMessages();
    }
    private void sendMessage(){
       // wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //wInfo = wifiManager.getConnectionInfo();
        mac_address = myMacAddress.getMacAddr();
        Message message=new Message();
        String messageText= etMessage.getText().toString();
        message.setMessage(messageText);
        message.setSenderId(mac_address);
        message.setChatId(currentChatId);
        databaseReference.push().setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                retrieveMessages();
                }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Chat.this,"Error..!",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void retrieveMessages(){
        userInfoQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    messagesList.clear();
                    for (DataSnapshot npsnapshot : snapshot.getChildren()){
                        Message l=npsnapshot.getValue(Message.class);
                        messagesList.add(l);
                    }
                    messageAdapter=new MessageAdapter(messagesList);
                    userMessagesList.setAdapter(messageAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @SuppressLint("StaticFieldLeak")
    public class connect extends AsyncTask<Void, Void, BluetoothSocket>  // UI thread
    {
        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(Chat.this, "Connecting...", "Please wait!!!");  //show a progress dialog
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
                    etMessage.setText("");
                    bytes = inputStream.read(buffer);
                    mm = new String(buffer,0,bytes);
                    System.out.print(mm);
                    etMessage.append(mm);


                    i++;

                }

            }
            catch( IOException e){
                e.printStackTrace();
                Toast.makeText(Chat.this, "please connect to your gloves ", Toast.LENGTH_LONG).show();

            }
            progress.dismiss();
        }
    }
}

