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
 * Created by visha on 27 Jan 2018.
 */

public class ActiveArrayAdapter extends ArrayAdapter {

    private Context mContext;
    private List<Integer> objects;

    public ActiveArrayAdapter(@NonNull Context context, int resource, @NonNull List<Integer> objects) {
        super(context, resource, objects);
        this.objects=objects;
        mContext=context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view=convertView;
        if(view==null)
            view= LayoutInflater.from(mContext).inflate(R.layout.service_list_items,parent,false);

        ImageView imageView=view.findViewById(R.id.serviceItemImage);
        TextView textView=view.findViewById(R.id.serviceListText);

        switch (objects.get(0)){

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
        }

        return view;

    }
}
