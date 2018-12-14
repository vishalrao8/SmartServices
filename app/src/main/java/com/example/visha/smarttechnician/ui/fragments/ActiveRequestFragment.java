package com.example.visha.smarttechnician.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.visha.smarttechnician.R;
import com.example.visha.smarttechnician.adapters.ActiveRequestAdapter;
import com.example.visha.smarttechnician.ui.user.UserMapActivity;

import java.util.ArrayList;

import static com.example.visha.smarttechnician.utils.StringResourceProvider.CATEGORY;
import static com.example.visha.smarttechnician.utils.StringResourceProvider.TECHNICIAN_USER_ID;

public class ActiveRequestFragment extends ListFragment {

    public static ArrayList<Integer> fragment2arrayList=new ArrayList<>();
    public static ArrayAdapter fragment2ArrayAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_active_request,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView=getListView();
        fragment2ArrayAdapter=new ActiveRequestAdapter(getActivity(),R.layout.item_active_request,fragment2arrayList);
        listView.setAdapter(fragment2ArrayAdapter);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if(CategoryViewFragment.technicianAccepted){

            UserMapsUI(CategoryViewFragment.technicianUserId, CategoryViewFragment.category);

        }
        else{

            Toast.makeText(getActivity(), getString(R.string.home_force_request_touch_warning), Toast.LENGTH_LONG).show();

        }

    }

    public void UserMapsUI(String id, String category){

        Intent intent=new Intent(getActivity(),UserMapActivity.class);
        intent.putExtra(TECHNICIAN_USER_ID,id);
        intent.putExtra(CATEGORY,category);
        startActivity(intent);

    }
}
