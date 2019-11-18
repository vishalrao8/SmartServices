package com.unitedcreation.visha.smartservices.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import com.unitedcreation.visha.smartservices.R;
import com.unitedcreation.visha.smartservices.adapters.ActiveRequestAdapter;
import com.unitedcreation.visha.smartservices.ui.user.UserMapActivity;

import java.util.ArrayList;

import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.CATEGORY;
import static com.unitedcreation.visha.smartservices.utils.StringResourceProvider.TECHNICIAN_USER_ID;

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
