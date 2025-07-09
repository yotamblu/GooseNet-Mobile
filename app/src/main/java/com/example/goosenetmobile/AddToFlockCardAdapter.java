package com.example.goosenetmobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.goosenetmobile.classes.FlockCard;

import java.util.List;

public class AddToFlockCardAdapter extends BaseAdapter {
    private final Context context;
    private final List<FlockCard> flockCardList;
    private final LayoutInflater inflater;


    public AddToFlockCardAdapter(Context context, List<FlockCard> flockCardList) {
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
        Button addToFlockBtn;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AddToFlockCardAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.add_to_flock_card, parent, false);

            holder = new AddToFlockCardAdapter.ViewHolder();
            holder.flockInitial = convertView.findViewById(R.id.flockInitial);
            holder.flockName = convertView.findViewById(R.id.flockName);
            holder.addToFlockBtn = convertView.findViewById(R.id.addToFlockButton);
            convertView.setTag(holder);
        } else {
            holder = (AddToFlockCardAdapter.ViewHolder) convertView.getTag();
        }

        FlockCard flockCard = flockCardList.get(position);

        holder.flockInitial.setText(String.valueOf(flockCard.getFlockName().charAt(0)));
        holder.flockName.setText(flockCard.getFlockName());



        holder.addToFlockBtn.setOnClickListener(v ->{
                    new Thread(() ->{

                        Activity activity = ((Activity)context);
                        activity.runOnUiThread(() -> ((AddToFlockActivity)activity).showBlockingProgressDialog2());
                        String athleteName = activity.getIntent().getExtras().get("athleteName").toString();
                        boolean didAdd = ApiService.addToFlock(athleteName,flockCard.getFlockName(),context);
                       activity.runOnUiThread(() -> {

                            if(didAdd){
                                Toast.makeText(activity, "Added to flock successfully!", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(activity, "Failed to add to flock,Athlete might be already in this flock!", Toast.LENGTH_SHORT).show();
                            }
                            activity.finish();
                        });
                    }).start();
                }
        );

        return convertView;
    }
 
}
