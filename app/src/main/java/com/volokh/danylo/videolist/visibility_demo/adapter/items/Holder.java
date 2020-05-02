package com.volokh.danylo.videolist.visibility_demo.adapter.items;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.volokh.danylo.videolist.R;

/**
 * Created by danylo.volokh on 09.01.2016.
 */
public class Holder extends RecyclerView.ViewHolder{

    public final TextView positionView;

    public Holder(View itemView) {
        super(itemView);
        positionView = (TextView) itemView.findViewById(R.id.position);
    }
}