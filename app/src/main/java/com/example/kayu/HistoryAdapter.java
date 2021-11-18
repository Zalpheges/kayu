package com.example.kayu;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyVueHolder> {

    List<FoodDescription> products;

    HistoryAdapter(List<FoodDescription> products){
        this.products = products;
    }
    @NonNull
    @Override
    public MyVueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        android.view.View view = layoutInflater.inflate(R.layout.item_historique,parent,false);
        return new MyVueHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVueHolder holder, int position) {
    holder.display(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class MyVueHolder extends RecyclerView.ViewHolder{
        private TextView productName;
        private TextView productScore;

        MyVueHolder(android.view.View itemView){
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.NameTV);
            productScore = (TextView) itemView.findViewById(R.id.ScoreTV);

        }
        void display(FoodDescription product){
            productName.setText(product.name);
            productScore.setText(product.nutriScore);

        }
    }
}
