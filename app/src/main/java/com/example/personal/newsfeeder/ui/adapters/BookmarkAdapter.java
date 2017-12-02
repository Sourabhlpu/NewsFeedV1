package com.example.personal.newsfeeder.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.personal.newsfeeder.R;
import com.example.personal.newsfeeder.TheArticle;
import com.example.personal.newsfeeder.data.NewsPreferences;
import com.example.personal.newsfeeder.utilities.NewsFeederDateUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by personal on 10/26/2017.
 */

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.BookmarkViewHolder> {

    List<TheArticle> mBookmark;
    Context mContext;
    private static final String LOG_TAG = BookmarkAdapter.class.getSimpleName();


    public BookmarkAdapter(Context context, ArrayList<TheArticle> bookmarks)
    {
        mContext = context;
        mBookmark = bookmarks;

    }

    public static class  BookmarkViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView mAvatarView;
        TextView mAvatarNameView;
        TextView mDate;
        ImageView mImageView;
        TextView mTheTitleView;
        TextView mTheSubtitleView;
        TextView mTheThreeLinesView;
        ImageView mBookmarkImage;
        ImageView mShareIcon;
        Context context;


        BookmarkViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            mAvatarView = (TextView) itemView.findViewById(R.id.avatar_image);
            mAvatarNameView = (TextView) itemView.findViewById(R.id.avatar_name);
            mDate = (TextView) itemView.findViewById(R.id.Date);
            mImageView = (ImageView) itemView.findViewById(R.id.image_view);
            mTheTitleView = (TextView) itemView.findViewById(R.id.the_title);
            mTheThreeLinesView = (TextView) itemView.findViewById(R.id.the_three_lines);



            ViewStub stub = (ViewStub)itemView.findViewById(R.id.layout_stub);
            stub.setLayoutResource(R.layout.cardview_actions);
            View inflated = stub.inflate();

            mBookmarkImage = (ImageView) inflated.findViewById(R.id.bookmark_image);
            mShareIcon = (ImageView) inflated.findViewById(R.id.share_icon);

        }

    }
    @Override
    public BookmarkViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //create a layout inflater object.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // this will hold the reference to the viewHolder
        View view = inflater.inflate(R.layout.card_item,parent,false);

        BookmarkViewHolder viewHolder = new BookmarkViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BookmarkViewHolder holder, int position) {
        //Log.v(LOG_TAG, "The position variable is " + position);
        //Log.v(LOG_TAG, "The rowIndex is " + mRowIndex);

        holder.mAvatarView.setText(mBookmark.get(position).getmAvatarInitial());
        holder.mAvatarNameView.setText(mBookmark.get(position).getmAvatarName());

        String dateString = NewsFeederDateUtils.getSimpleDate(mBookmark.get(position).getmDate());
        holder.mDate.setText(dateString);

        String imageUrl = mBookmark.get(position).getmImageURL();

        Picasso.with(mContext)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_img)
                .into(holder.mImageView);
        holder.mTheTitleView.setText(mBookmark.get(position).getmTheTitle());
        // holder.mTheSubtitleView.setText(mBookmark.get(position).getmTheSubtitle());
        holder.mTheThreeLinesView.setText(mBookmark.get(position).getmTheThreeLines());
        //holder.mBookmarkView.setImageResource(mBookmark.get(position).getmBookmarkResourceId());
        // holder.mHeartView.setImageResource(mBookmark.get(position).getmHeartResourceId());
        String id = mBookmark.get(position).getmId();
        boolean isBookmarked = NewsPreferences.isBookmarked(id,mContext);
        if(isBookmarked)
        {
            Log.v(LOG_TAG, "bookmarked articles is true ");
            holder.mBookmarkImage.setBackgroundResource(R.drawable.bookmark);
        }


    }


    @Override
    public int getItemCount() {
        return mBookmark.size();
    }

    public void updateDataset(List<TheArticle> bookmarks)
    {
        mBookmark = bookmarks;
        notifyDataSetChanged();
    }


}

