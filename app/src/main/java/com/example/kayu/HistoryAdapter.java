package com.example.kayu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyVueHolder> {

    List<FoodDescription> products;
    private OnHistoryListener onHistoryListener;

    HistoryAdapter(List<FoodDescription> products, OnHistoryListener onHistoryListener){
        this.products = products;
        this.onHistoryListener = onHistoryListener;
    }
    @NonNull
    @Override
    public MyVueHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        android.view.View view = layoutInflater.inflate(R.layout.item_historique,parent,false);
        return new MyVueHolder(view, onHistoryListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyVueHolder holder, int position) {
    holder.display(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class MyVueHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView productName;
        private TextView productScore;
        OnHistoryListener onHistoryListener;
        MyVueHolder(android.view.View itemView, OnHistoryListener onHistoryListener){
            super(itemView);
            productName = (TextView) itemView.findViewById(R.id.NameTV);
            productScore = (TextView) itemView.findViewById(R.id.ScoreTV);
            this.onHistoryListener = onHistoryListener;
            itemView.setOnClickListener(this);
        }
        void display(FoodDescription product){
            productName.setText(product.name);
            productScore.setText("Nutriscore : " + product.nutriScore);

        }

        @Override
        public void onClick(View view) {
            onHistoryListener.onHistoryClick(getAdapterPosition());
        }
    }
    public interface OnHistoryListener{
        void onHistoryClick(int position);
    }
}
