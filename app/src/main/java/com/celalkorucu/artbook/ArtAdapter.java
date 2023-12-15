package com.celalkorucu.artbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.celalkorucu.artbook.databinding.RecyclerRowBinding;

import java.sql.SQLOutput;
import java.util.ArrayList;


public class ArtAdapter  extends RecyclerView.Adapter<ArtAdapter.ArtHolder> {


    ArrayList<Art> artArrayList;
    public ArtAdapter(ArrayList<Art> artArrayList){
        this.artArrayList=artArrayList;
    }

    @NonNull
    @Override
    public ArtAdapter.ArtHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);

        return new ArtHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtAdapter.ArtHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.recyclerTextView.setText(artArrayList.get(position).getArtName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext() , ArtActivity.class);
                intent.putExtra("artId" , artArrayList.get(position).getId());
                intent.putExtra("artName",artArrayList.get(position).getArtName());
                intent.putExtra("info","old");
                holder.itemView.getContext().startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {

        for (int i = 0 ; i<artArrayList.size() ; i++){

            System.out.println(artArrayList.get(i).getArtName());
        }
        System.out.println("ALAN : "+artArrayList.size());
        return artArrayList.size();
    }

    public class ArtHolder extends  RecyclerView.ViewHolder{
        private RecyclerRowBinding binding ;

        public ArtHolder(RecyclerRowBinding binding){
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
