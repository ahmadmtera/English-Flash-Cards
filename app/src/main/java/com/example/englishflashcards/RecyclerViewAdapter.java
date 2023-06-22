package com.example.englishflashcards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import data.FlashCard;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    Context context;
    ArrayList<FlashCard> flashCardArrayList;
    public RecyclerViewAdapter (Context context, ArrayList<FlashCard> flashCardArrayList){
        this.context = context;
        this.flashCardArrayList = flashCardArrayList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recycler_view_row, parent, false);
        return new RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.wordTextView.setText(flashCardArrayList.get(position).getFront());
        holder.definitionTextView.setText(flashCardArrayList.get(position).getBack());
    }

    @Override
    public int getItemCount() {
        return flashCardArrayList.size();
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView wordTextView;
        TextView definitionTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            wordTextView = itemView.findViewById(R.id.wordTextView);
            definitionTextView = itemView.findViewById(R.id.definitionTextView);

        }
    }
}
