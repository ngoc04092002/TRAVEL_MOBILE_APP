package com.example.travel_mobile_app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.travel_mobile_app.R;
import com.example.travel_mobile_app.models.Location;

import java.util.ArrayList;

public class SuggetionAdapter extends ArrayAdapter<Location> {
    //khai bao tham so adapter
    Activity context;
    int idLayout;
    ArrayList<Location> myList;
    //tao constructor de goi den va tuyen tham so vao

    public SuggetionAdapter(Activity context, int idLayout, ArrayList<Location> myList) {
        super(context, idLayout, myList);
        this.context = context;
        this.idLayout = idLayout;
        this.myList = myList;
    }
    //goi ham getView de lay du lieu va hien thi du lieu

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //tao nen de chua layout
        LayoutInflater myflactor = context.getLayoutInflater();
        //dat idlayout len nen de tao view
        convertView = myflactor.inflate(idLayout,null);

        Location myitem = myList.get(position);

        ImageView img_item = convertView.findViewById(R.id.imgsug);
        Glide.with(context)  // Replace 'context' with your activity or fragment context
                .load(myitem.getImglink())  // Replace with your image URL
                .into(img_item);  // Replace 'imageView' with your ImageView object


        TextView txt_tendiadiem =convertView.findViewById(R.id.name);
        txt_tendiadiem.setText(myitem.getName());
        return convertView;




    }
}
