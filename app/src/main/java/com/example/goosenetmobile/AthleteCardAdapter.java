package com.example.goosenetmobile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.goosenetmobile.classes.AthleteCard;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AthleteCardAdapter extends BaseAdapter {

    private final Context context;
    private final List<AthleteCard> athleteList;
    private final LayoutInflater inflater;

    public AthleteCardAdapter(Context context, List<AthleteCard> athleteList) {
        this.context = context;
        this.athleteList = athleteList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return athleteList.size();
    }

    @Override
    public Object getItem(int position) {
        return athleteList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        CircleImageView athleteProfileImage;
        TextView athleteName;
        MaterialButton viewPageButton;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_athlete_card, parent, false);

            holder = new ViewHolder();
            holder.athleteProfileImage = convertView.findViewById(R.id.athleteProfileImage);
            holder.athleteName = convertView.findViewById(R.id.athleteName);
            holder.viewPageButton = convertView.findViewById(R.id.viewPageButton);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        AthleteCard athlete = athleteList.get(position);

        holder.athleteName.setText(athlete.getAthleteName());

        Bitmap bmp = GooseNetUtil.base64ToBitmap(athlete.getImageData());
        if (bmp != null) {
            holder.athleteProfileImage.setImageBitmap(bmp);
        } else {
            holder.athleteProfileImage.setImageResource(R.drawable.loading);
        }

        holder.viewPageButton.setOnClickListener(v ->{
                    Intent athleteProfileIntent = new Intent(context, AthleteProfileActivity.class);
                    athleteProfileIntent.putExtra("athleteName",athlete.getAthleteName());
                    athleteProfileIntent.putExtra("imageData",athlete.getImageData());
                    context.startActivity(athleteProfileIntent);
        }
        );

        return convertView;
    }
}
