package com.example.visha.smarttechnician;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by visha on 26 Jan 2018.
 */

public class mArrayAdapter extends ArrayAdapter {

    private Context mContext;
    private List<String> objects;

    public mArrayAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.mContext=context;
        this.objects=objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view=convertView;
        if(view==null)
            view= LayoutInflater.from(mContext).inflate(R.layout.service_list_items,parent,false);

        ImageView imageView=view.findViewById(R.id.serviceItemImage);
        TextView textView=view.findViewById(R.id.serviceListText);

        switch (position){

            case 0:
                textView.setText(objects.get(position));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.electrician));
                break;
            case 1:
                textView.setText(objects.get(position));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.plumber));
                break;
            case 2:
                textView.setText(objects.get(position));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.carpenter));
                break;
            case 3:
                textView.setText(objects.get(position));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.painter));
                break;
            case 4:
                textView.setText(objects.get(position));
                imageView.setImageDrawable(view.getResources().getDrawable(R.drawable.computer));
                break;
        }

        return  view;
    }
}
