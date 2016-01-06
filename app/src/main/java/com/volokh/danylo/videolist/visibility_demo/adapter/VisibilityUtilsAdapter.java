package com.volokh.danylo.videolist.visibility_demo.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.volokh.danylo.videolist.R;
import com.volokh.danylo.videolist.visibility_demo.adapter.items.VisibilityItem;

import java.util.List;

/**
 * Created by danylo.volokh on 06.01.2016.
 */
class Holder extends RecyclerView.ViewHolder{

    public final View coloredView;

    public Holder(View itemView) {
        super(itemView);
        coloredView = itemView.findViewById(R.id.colored_view);
    }
}

public class VisibilityUtilsAdapter extends RecyclerView.Adapter<Holder> {

    private final List<VisibilityItem> mList;

    public VisibilityUtilsAdapter(List<VisibilityItem> list) {
        mList = list;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.visibility_adapter_item, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.height = view.getResources().getDisplayMetrics().widthPixels;
        return new Holder(view);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        int color = holder.itemView.getResources().getColor(android.R.color.holo_blue_dark);
        holder.coloredView.setBackgroundColor(color);
    }

}
