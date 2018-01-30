package com.example.visha.smarttechnician;

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

import java.util.ArrayList;

/**
 * Created by visha on 26 Jan 2018.
 */

public class FragmentTwo extends ListFragment {

    public static ArrayList<Integer> fragment2arrayList=new ArrayList<>();
    public static ArrayAdapter fragment2ArrayAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_two,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ListView listView=getListView();
        fragment2ArrayAdapter=new ActiveArrayAdapter(getActivity(),R.layout.service_list_items,fragment2arrayList);
        listView.setAdapter(fragment2ArrayAdapter);

    }

    public void UserMapsUI(String id, String category){

        Intent intent=new Intent(getActivity(),UsersMapActivity.class);
        intent.putExtra("technicianUserId",id);
        intent.putExtra("category",category);
        startActivity(intent);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

            if(FragmentOne.technicianAccepted){

                UserMapsUI(FragmentOne.technicianUserId, FragmentOne.category);

            }
            else{

                Toast.makeText(getActivity(), "You will be automatically redirected to maps once technician accepts your request", Toast.LENGTH_LONG).show();

            }

    }
}
