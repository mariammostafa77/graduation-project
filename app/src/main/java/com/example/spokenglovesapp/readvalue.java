package com.example.spokenglovesapp;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class readvalue {
  String mm,finalvalue;
    public static ProgressDialog progress;


    //    String connection() {
//
//        connect connect =new connect();
//        if(connect.execute()==null)
//        {
//            return "adsfdvdzxas";
//        }
//        else {
//            return mm;
//        }
////            BluetoothSocket btSocket = finalbtstock.DataHolder.getData();
////            InputStream inputStream = null;
////            try {
////                inputStream = btSocket.getInputStream();
////                inputStream.skip(inputStream.available());
////                byte[] buffer = new byte[10];
////                int bytes, i;
////                i = 0;
////
////                    bytes = inputStream.read(buffer);
////                    mm = new String(buffer, 0, bytes);
////
////                finalvalue=mm;
////
////            }
////            catch (IOException e) {
////                e.printStackTrace();
////                return  "null";
////
////            }
////            return finalvalue;
//    }
        class connect extends AsyncTask<Void, Void, BluetoothSocket>  // UI thread
        {
            @Override
            public void onPreExecute() {
                //  progress = ProgressDialog.show(getContext(), "Connecting...", "Please wait!!!");  //show a progress dialog
            }

            @Override
            public BluetoothSocket doInBackground(Void... voids) {
                BluetoothSocket btSocket = finalbtstock.DataHolder.getData();
                return btSocket;
            }
            public void onPostExecute(BluetoothSocket btSocket) {
                super.onPostExecute(btSocket);
                InputStream inputStream = null;
                try {
                    inputStream = btSocket.getInputStream();
                    inputStream.skip(inputStream.available());
                    byte[] buffer = new byte[1024];
                    int bytes, i;
                    i = 0;

                    while (i<1) {
                        bytes = inputStream.read(buffer);
                        mm = new String(buffer, 0, bytes);
                        i++;
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }

}
