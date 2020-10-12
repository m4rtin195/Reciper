package com.martin.reciper.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.martin.reciper.R;
import com.martin.reciper.RecipeAdapter;
import com.martin.reciper.RecipeRcrd;

import java.util.ArrayList;

public class HomeFragment extends Fragment
{
    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState)
    {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        final Button searchBtn = root.findViewById(R.id.button_search);
        final EditText queryEdt = root.findViewById(R.id.edit_query);

        final Button deleteBtn = root.findViewById(R.id.button2);
        deleteBtn.setVisibility(View.GONE);

        final View textfield = root.findViewById(R.id.textView31);
        final ConstraintLayout layout = (ConstraintLayout) root.findViewById(R.id.baseLayout);

        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //getActivity().findViewById(R.id.textView31).setVisibility(View.GONE);
                //container.removeView(textfield);
                ((ViewGroup)container.getParent()).removeView(textfield);
            }
        });

        ListView recepty_listview = (ListView) root.findViewById(R.id.zoznam_receptov);
        //ArrayList<String> recepty_array = new ArrayList<String>();
        //ArrayList<RecipeRcrd> recepty_array = new ArrayList<RecipeRcrd>();
        //ArrayAdapter adapter = new ArrayAdapter<LinearLayout>(getActivity(), android.R.layout.simple_list_item_1, recepty_array);
        //recepty_listview.setAdapter(adapter);

/*
        ArrayAdapter adapter = new ArrayAdapter<RecipeAdapter>(getActivity(),)

        zaznam.addView(nazov);

        adapter.add(zaznam);
        adapter.notifyDataSetChanged();
*/



        searchBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_SEARCH);
                intent.setPackage("com.google.android.youtube");
                intent.putExtra("query", queryEdt.getText().toString());
                startActivity(intent);
            }
        });

        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>()
        {
            @Override
            public void onChanged(@Nullable String s)
            {
                textView.setText(s);
            }
        });
        return root;
    }
}