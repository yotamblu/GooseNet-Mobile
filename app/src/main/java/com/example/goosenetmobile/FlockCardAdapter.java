package com.example.goosenetmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.goosenetmobile.classes.AthleteCard;
import com.example.goosenetmobile.classes.FlockCard;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FlockCardAdapter extends BaseAdapter {
    private final Context context;
    private final List<FlockCard> flockCardList;
    private final LayoutInflater inflater;


    public FlockCardAdapter(Context context, List<FlockCard> flockCardList) {
        this.context = context;
        this.flockCardList = flockCardList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return flockCardList.size();
    }

    @Override
    public Object getItem(int position) {
        return flockCardList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView flockName;
        TextView flockInitial;
        Button addWorkoutBtn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FlockCardAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.flock_card, parent, false);

            holder = new FlockCardAdapter.ViewHolder();
            holder.flockInitial = convertView.findViewById(R.id.flockInitial);
            holder.flockName = convertView.findViewById(R.id.flockName);
            holder.addWorkoutBtn = convertView.findViewById(R.id.addWorkoutButton);
            convertView.setTag(holder);
        } else {
            holder = (FlockCardAdapter.ViewHolder) convertView.getTag();
        }

        FlockCard flockCard = flockCardList.get(position);

        holder.flockInitial.setText(String.valueOf(flockCard.getFlockName().charAt(0)));
        holder.flockName.setText(flockCard.getFlockName());



        holder.addWorkoutBtn.setOnClickListener(v ->{
                    Intent athleteProfileIntent = new Intent(context, AddWorkoutActivity.class);
                    athleteProfileIntent.putExtra("flockName",flockCard.getFlockName());
                    context.startActivity(athleteProfileIntent);

                }
        );

        return convertView;
    }
 
}
