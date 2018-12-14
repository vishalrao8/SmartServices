package com.example.visha.smarttechnician.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.visha.smarttechnician.R;

public class CategoryViewAdapter extends RecyclerView.Adapter<CategoryViewAdapter.ViewHolder>{

    private static final int LENGTH = 5;

    private final String[] serviceName;
    private final Drawable[] serviceImages;

    private onCategoryClickedInterface mOnCategoryClickedInterface;

    public interface onCategoryClickedInterface {

        void onCategoryClicked(int position);

    }

    public CategoryViewAdapter(Context context, onCategoryClickedInterface onCategoryClickedInterface) {

        Resources resources=context.getResources();

        mOnCategoryClickedInterface = onCategoryClickedInterface;

        serviceName = resources.getStringArray(R.array.services_name);
        TypedArray a = resources.obtainTypedArray(R.array.service_images);
        serviceImages = new Drawable[a.length()];

        for (int i = 0; i < a.length(); i++){

            serviceImages[i] = a.getDrawable(i);

        }
        a.recycle();
    }



    @NonNull
    @Override
    public CategoryViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate( R.layout.item_category_view, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewAdapter.ViewHolder holder, int position) {

        holder.imageView.setImageDrawable(serviceImages[position]);
        holder.textView.setText(serviceName[position]);
    }

    @Override
    public int getItemCount() {
        return LENGTH;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView imageView;
        public TextView textView;

        public ViewHolder(View view){

            super(view);

            imageView = itemView.findViewById(R.id.imageview_rvitem_categorytile);
            textView = itemView.findViewById(R.id.textview_rvitem_categoryname);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            mOnCategoryClickedInterface.onCategoryClicked(getAdapterPosition());

        }
    }
}