package com.unitedcreation.visha.smartservices.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.unitedcreation.visha.smartservices.R;

import java.util.List;

public class ActiveRequestAdapter extends ArrayAdapter {

    private Context context;
    private List<Integer> activeRequestCode;

    public ActiveRequestAdapter(@NonNull Context context, int resource, @NonNull List<Integer> activeRequestCode) {

        super(context, resource, activeRequestCode);

        this.activeRequestCode = activeRequestCode;
        this.context =context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view=convertView;

        if(view == null)
            view = LayoutInflater.from(context).inflate(R.layout.item_active_request, parent,false);

        ImageView imageView = view.findViewById(R.id.imageview_activereq_categorytile);
        TextView textView = view.findViewById(R.id.textview_activereq_categoryname);

        switch (activeRequestCode.get(0)){

            case 0:
                textView.setText(view.getResources().getString(R.string.electronics));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.electrician));
                break;
            case 1:
                textView.setText(view.getResources().getString(R.string.plumber));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.plumber));
                break;
            case 2:
                textView.setText(view.getResources().getString(R.string.carpenter));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.carpenter));
                break;
            case 3:
                textView.setText(view.getResources().getString(R.string.painter));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.painter));
                break;
            case 4:
                textView.setText(view.getResources().getString(R.string.portable_devices));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.computer));
                break;
            case 5:
                textView.setText(view.getResources().getString(R.string.washing_machine));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.washing_machine));
                break;
            case 6:
                textView.setText(view.getResources().getString(R.string.refrigerator));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.refrigerator));
                break;
            case 7:
                textView.setText(view.getResources().getString(R.string.cctv));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.cctv));
                break;
            case 8:
                textView.setText(view.getResources().getString(R.string.television));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.television));
                break;
            case 9:
                textView.setText(view.getResources().getString(R.string.printer));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.printer));
                break;
            case 10:
                textView.setText(view.getResources().getString(R.string.gardening));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.gardering));
                break;
            case 11:
                textView.setText(view.getResources().getString(R.string.car_wash));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.car_wash));
                break;
            case 12:
                textView.setText(view.getResources().getString(R.string.air_conditioner));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.air_conditioner));
                break;
        }
        return view;
    }
}
