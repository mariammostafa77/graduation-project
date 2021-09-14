package com.example.spokenglovesapp;

import android.net.Uri;

public class valuesave {
    private String value,name,id;
          private   String imagename ;
    public valuesave(String value, String name,String urlimage,String id)
    {
        this.imagename=urlimage;
        this.value=value;
        this.name=name;
        this.id=id;



    }

    public valuesave() {
    }

    public String getValue() {
        return value;
    }
    public String getName() {
        return name;
    }
    public String getImagename() { return imagename; }
    public String getId() { return id; }


}
