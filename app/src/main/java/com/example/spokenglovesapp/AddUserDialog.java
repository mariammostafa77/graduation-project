 package com.example.spokenglovesapp;

 import android.app.Activity;
 import android.content.ContentResolver;
 import android.content.Context;
 import android.content.Intent;
 import android.graphics.Bitmap;
 import android.net.Uri;
 import android.net.wifi.WifiInfo;
 import android.net.wifi.WifiManager;
 import android.os.Bundle;
 import android.provider.MediaStore;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.view.ViewGroup;
 import android.webkit.MimeTypeMap;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.ImageView;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.fragment.app.DialogFragment;

 import com.google.android.gms.tasks.OnSuccessListener;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.Query;
 import com.google.firebase.database.ValueEventListener;
 import com.google.firebase.storage.FirebaseStorage;
 import com.google.firebase.storage.StorageReference;
 import com.google.firebase.storage.UploadTask;

 import java.io.IOException;

public class AddUserDialog extends DialogFragment {

    EditText etUserName;
    Button addUserImg, btnAddUser;
    ImageView imgUser;
    TextView tvImgURI;

    Uri FilePathUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;

    Query userQuery;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_user_dialog, container, false);

        storageReference = FirebaseStorage.getInstance("gs://garduate.appspot.com").getReference("UserInfo");
        databaseReference = FirebaseDatabase.getInstance().getReference("UserInfo");



        etUserName = view.findViewById(R.id.etUserName);
        addUserImg = view.findViewById(R.id.btnAddUserImg);
        imgUser = view.findViewById(R.id.imgUser);
        tvImgURI = view.findViewById(R.id.tvImgURI);
        btnAddUser = view.findViewById(R.id.btnAddUser);
        addUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);//one can be replaced with any action code
            }
        });
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();

            }
        });

        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {

            FilePathUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), FilePathUri);
                imgUser.setImageBitmap(bitmap);
                tvImgURI.setText(FilePathUri + "");
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getActivity().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }


    public void UploadImage() {

        if (FilePathUri != null) {

            MacAddress myMacAddress=new MacAddress();
            final String macAddress = myMacAddress.getMacAddr();

            StorageReference storageReference2 = storageReference.child(System.currentTimeMillis() + "." + GetFileExtension(FilePathUri));
            storageReference2.putFile(FilePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String TempImageName = etUserName.getText().toString().trim();
                            String ImageUploadId = databaseReference.push().getKey();
                            Toast.makeText(getActivity(), "Image Uploaded Successfully ", Toast.LENGTH_LONG).show();
                            @SuppressWarnings("VisibleForTests")
                            UploadUserInfo imageUploadInfo = new UploadUserInfo(TempImageName, taskSnapshot.getUploadSessionUri().toString(), macAddress, ImageUploadId);
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                            dismiss();
                        }
                    });
        } else {

            Toast.makeText(getActivity(), "Please Select Image ", Toast.LENGTH_LONG).show();

        }
    }

    public void validation() {
        String name = etUserName.getText().toString();
        if (name.length() == 0) {
            etUserName.requestFocus();
            etUserName.setError("FIELD CANNOT BE EMPTY");
        } else {
            userQuery = databaseReference.orderByChild("userName").equalTo(name);
            userQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        etUserName.requestFocus();
                        etUserName.setError("THIS NAME EXIST..TRY AGAIN !");
                    } else {
                        UploadImage();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
}
