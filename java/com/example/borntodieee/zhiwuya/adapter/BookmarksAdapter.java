package com.example.borntodieee.zhiwuya.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.borntodieee.zhiwuya.R;
import com.example.borntodieee.zhiwuya.bean.News;
import com.example.borntodieee.zhiwuya.interfaze.OnRecyclerViewOnClickListener;

import java.util.ArrayList;
import java.util.List;

public class BookmarksAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private final LayoutInflater inflater;

    private List<News.Question> zhihuList;

    private OnRecyclerViewOnClickListener listener;


    public BookmarksAdapter(@NonNull Context context, ArrayList<News.Question> zhihuList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.zhihuList = zhihuList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new NormalViewHolder(inflater.inflate(R.layout.home_list_item_layout, parent, false), this.listener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (!zhihuList.isEmpty()) {
                News.Question q = zhihuList.get(position);

                Glide.with(context)
                        .load(q.getImages().get(0))
                        .placeholder(R.drawable.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .error(R.drawable.placeholder)
                        .centerCrop()
                        .into(((NormalViewHolder) holder).imageView);

                ((NormalViewHolder) holder).textViewTitle.setText(q.getTitle());
            }
        }



    @Override
    public int getItemCount() {
        return zhihuList.size();
    }

    public void setItemListener(OnRecyclerViewOnClickListener listener) {
        this.listener = listener;
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView imageView;
        TextView textViewTitle;
        OnRecyclerViewOnClickListener listener;

        public NormalViewHolder(View itemView, OnRecyclerViewOnClickListener listener) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.imageViewCover);
            textViewTitle = (TextView) itemView.findViewById(R.id.textViewTitle);
            this.listener = listener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.OnItemClick(v,getLayoutPosition());
            }
        }
    }

}
