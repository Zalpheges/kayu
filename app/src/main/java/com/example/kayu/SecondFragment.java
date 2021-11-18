package com.example.kayu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.kayu.databinding.FragmentSecondBinding;

import java.util.List;

public class SecondFragment extends Fragment {

    public FoodDescription foodDs;
    public View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_second, container, false);


        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).toHistory();
            }
        });

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onReady(FoodDescription foodInfo)
    {
        foodDs = foodInfo;
        TextView nT = (TextView) view.findViewById(R.id.nameText);
        TextView sT = (TextView) view.findViewById(R.id.nutriScoreText);
        TextView iT = (TextView) view.findViewById(R.id.ingredientsText);
        TextView aT = (TextView) view.findViewById(R.id.allergensText);
        if(foodDs.name!=null) {
            nT.setText(foodDs.name);
            sT.setText(foodDs.nutriScore);
            iT.setText("Ingredients : " + foodDs.ingredients);
            aT.setText("Allergens : " + foodDs.allergens);
        }else{
            nT.setText("Chocolat");
            sT.setText("Chocolat");
            iT.setText("Chocolat");
            aT.setText("Chocolat");

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



}