package com.example.personal.newsfeeder;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by personal on 4/11/2017.
 */

public class SectionRVAdapter extends RecyclerView.Adapter<SectionRVAdapter.SectionViewHolder> {

    List<Section> mSection;
    Context mContext;

    public SectionRVAdapter(Context context, ArrayList<Section> sections)
    {
        mContext = context;
        mSection = sections;

    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder
    {
        CardView cardView;
        ImageView imageView;
        TextView textView;

        public SectionViewHolder(View itemView)
        {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.section_card_view);
            imageView = (ImageView)itemView.findViewById(R.id.section_image);
            textView = (TextView)itemView.findViewById(R.id.section_text);

        }

    }

    @Override
    public int getItemCount() {
        return mSection.size();
    }

    @Override
    public SectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.section_card_item,parent,false);
        SectionViewHolder svh = new SectionViewHolder(v);
        return svh;
    }

    @Override
    public void onBindViewHolder(SectionViewHolder holder, int position) {
        holder.textView.setText(mSection.get(position).getmSectionName());
        holder.imageView.setImageResource(mSection.get(position).getmImageSource());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void setDataset(ArrayList<Section> sections)
    {
        mSection = sections;
        notifyDataSetChanged();
    }
}
