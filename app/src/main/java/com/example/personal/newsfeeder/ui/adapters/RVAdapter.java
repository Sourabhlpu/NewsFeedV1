package com.example.personal.newsfeeder.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.personal.newsfeeder.R;
import com.example.personal.newsfeeder.Section;
import com.example.personal.newsfeeder.TheArticle;
import com.example.personal.newsfeeder.data.NewsPreferences;
import com.example.personal.newsfeeder.utilities.NewsFeederDateUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by personal on 3/12/2017.
 * This adapter is responsible for creating providing the data for the main activity.
 * This class extends form the Recycler View's Adapter class and takes in the Recycler View's ViewHolder as generic parameter.
 */

public class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = RVAdapter.class.getSimpleName();

    // it is the constant that is used for items that has horizontal scroll for the sections
    private final int CARD_WITH_HORIZONTAL_SCROLL = 1;
    // this constant is used to when we have normal card view
    private final int CARD_WITHOUT_HORIZONTAL_SCROLL = 2;


   /*
    * the interface that is defined below handles the clicks on the articles.
    * In the constructor we set this variable equal to the MainActivity that implements the
    * on click of this interface.
    */
    final private ListItemOnClickHandler mOnClickHandler;

    final private BookmarkOnClickHandler mOnBookmarkClickHandler;

    final private ShareOnClickHandler mOnShareClickHandler;

    // mArticles stores the data that we received from the loaders in the form of list
    List<TheArticle> mArticles;
    Context mContext;


    /*
     * Declaring the interface that is used to handle the on click
     * on individual articles.

     */

    public interface ListItemOnClickHandler {

        void onClick(TheArticle article);
    }

    public interface BookmarkOnClickHandler {

        void onBookmarkClick(TheArticle article);
    }
    public interface ShareOnClickHandler
    {
        void onShareClick(TheArticle article);
    }

    /*
     * the constructor for the RVAdapter class.
     * @param context just initializes the context of the activity
     * @param articles it's the list of articles that are created in the loader's onFinish method
     * @param onClickHandler in the main activity we pass 'this' as main activity implements this interface
     */


    public RVAdapter(Context context, List<TheArticle> articles, ListItemOnClickHandler onClickHandler
            ,BookmarkOnClickHandler onBookMarkClickHandler, ShareOnClickHandler onShareClickHandler) {

        //the usual initialization of the member variables happening

        mArticles = articles;
        mContext = context;
        mOnClickHandler = onClickHandler;
        mOnBookmarkClickHandler = onBookMarkClickHandler;
        mOnShareClickHandler = onShareClickHandler;
    }

    //this is the necessary override that just gives the size of the list of articles

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    /*
     * when we have heterogeneous  items in the recycler view we use the below function.
     * So as we want a horizontal scrolling list at the top of the recycler view we use the position in the recycler view
     * to create different views.
     */

    @Override
    public int getItemViewType(int position) {

        //if its the first element of the recycler view just return a constant that represents a horizontal scroll view

        if(position == 0)
        {
            return CARD_WITH_HORIZONTAL_SCROLL;
        }

        // if its not the first position then just return a constant that represents the normal cardview.
        else{
            return CARD_WITHOUT_HORIZONTAL_SCROLL;
        }
    }

    /*
     * in the onCreateViewHolder method we inflate the layout that is used for the individual item in the list.
     * Since we have two types of items in our recycler view we inflate two different layouts depending on the
     * viewType parameter.
     * After the appropriate layout is inflated we just create an instance of the view holder class and pass
     * in the view that was inflated
     */

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //create a layout inflater object.
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // this will hold the reference to the viewHolder
        RecyclerView.ViewHolder viewHolder;

        // lets decide what's the kind of view type we want to inflate
    switch (viewType)
    {
        //if its horizontal scroll view.
        case CARD_WITH_HORIZONTAL_SCROLL:
            //inflate  the view using  layout file 'card_item_with_horizontal_scroll'.
            View v1 = inflater.inflate(R.layout.card_item_with_horizontal_scroll, parent, false);
            // pass this view to the View holder class that creates the horizontal scroll view.
            viewHolder = new ArticleViewHolderWithHorizontalScroll(v1);
            break;

        // the default behaviour when we just want a simple card layout
        default:
            //inflate the correct layout file
            View v2 = inflater.inflate(R.layout.card_item,parent,false);
            //initialize the view holder and pass in the correct view
            viewHolder = new ArticleViewHolder(v2);
            break;
    }

    //return the view holder
    return viewHolder;

    }

    /*
     * in the onBindViewHolder method we just set the values to the view holder members.
     * this is where we set the values to our final views in the layout file.
     */

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        //again we use the switch to take action according to the veiw type
        switch (holder.getItemViewType())
        {
            case CARD_WITH_HORIZONTAL_SCROLL:

                //since holder is generic here, we cast it to appropriate viewholder type.
                ArticleViewHolderWithHorizontalScroll vh1 = (ArticleViewHolderWithHorizontalScroll) holder;
                //we use the below method to configure our views in the layout
                configureArticleViewHolderWithHorizontalScroll(vh1,position);
                break;

            //same as above case but with different item view type
            case CARD_WITHOUT_HORIZONTAL_SCROLL:
                ArticleViewHolder vh2 = (ArticleViewHolder)holder;
                configureArticleViewHolder(vh2,position);
        }


    }

    //just a method we override

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void updateDataset(final List<TheArticle> articles, RecyclerView view) {

        if(mArticles.isEmpty())
        {
            mArticles = articles;
            this.notifyDataSetChanged();
        }
        else
        {
            final int currentSize = this.getItemCount();

            // Delay before notifying the adapter since the scroll listeners
            // can be called while RecyclerView data cannot be changed.
            // post moves the runnable task to another thread i.e. into the message queue and thus,
            // there is some delay in notifying the adapter
            view.post(new Runnable() {

                @Override
                public void run() {

                    //Log.v(LOG_TAG,"THE SIZE of the items in the list " + currentSize);
                    //mArticles.clear();
                    mArticles.addAll(articles);
                    //Log.v(LOG_TAG,"The articles after the addAll func" + mArticles.toString());
                    RVAdapter.this.notifyItemRangeInserted(currentSize,articles.size()-1);
                }
            });

        }



    }

    public void replaceAll(List<TheArticle> articles)
    {
        if(mArticles != null)
        {
            mArticles.clear();
            mArticles.addAll(articles);
            notifyDataSetChanged();
        }
    }

    /*
     * view holder class is responsible for doing the expensive findViewById task which is done once and that's it.
     * this view holder is for the horizontal scrolling list. So here we do the normal procedure that we do
     * to make the objects of the sections class and then add it the the sections arraylist and finally
     * We also implement the onClicklistener for each items in the list
     */

    public  class ArticleViewHolderWithHorizontalScroll extends RecyclerView.ViewHolder implements
    View.OnClickListener{

        RecyclerView horizontalList;
        Context context;
        ArrayList<Section> sections;
        SectionRVAdapter horizontalAdapter;


        ArticleViewHolderWithHorizontalScroll(View itemView) {
            super(itemView);
            context = itemView.getContext();

            sections = new ArrayList<Section>();


            itemView.setOnClickListener(this);


            //creating the section class objects

            Section technology = new Section(R.drawable.placeholder, "Technology");
            Section film = new Section(R.drawable.placeholder, "Films");
            Section sports = new Section(R.drawable.placeholder, "Sports");
            Section politics = new Section(R.drawable.placeholder, "Politics");
            Section culture = new Section(R.drawable.placeholder, "Culture");
            Section education = new Section(R.drawable.placeholder, "Education");
            Section business = new Section(R.drawable.placeholder, "Business");

            //adding the above objects to the arraylist

            sections.add(technology);
            sections.add(film);
            sections.add(sports);
            sections.add(politics);
            sections.add(culture);
            sections.add(education);
            sections.add(business);

            horizontalList = (RecyclerView) itemView.findViewById(R.id.section_recycler_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            mOnClickHandler.onClick(mArticles.get(position));

        }
    }

    /*
     * this is the view holder that we use for the normal cardview items. So it just does what view holders do.
     * yes...just do the expensive search for the views in the layout.
     */

    public  class ArticleViewHolder extends RecyclerView.ViewHolder{
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


        ArticleViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            mAvatarView = (TextView) itemView.findViewById(R.id.avatar_image);
            mAvatarNameView = (TextView) itemView.findViewById(R.id.avatar_name);
            mDate = (TextView) itemView.findViewById(R.id.Date);
            mImageView = (ImageView) itemView.findViewById(R.id.image_view);
            mTheTitleView = (TextView) itemView.findViewById(R.id.the_title);
            mTheThreeLinesView = (TextView) itemView.findViewById(R.id.the_three_lines);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mOnClickHandler.onClick(mArticles.get(position));
                }
            });

            ViewStub stub = (ViewStub)itemView.findViewById(R.id.layout_stub);
            stub.setLayoutResource(R.layout.cardview_actions);
            View inflated = stub.inflate();

            mBookmarkImage = (ImageView) inflated.findViewById(R.id.bookmark_image);
            mShareIcon = (ImageView) inflated.findViewById(R.id.share_icon);

            inflated.findViewById(R.id.bookmark_image).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mOnBookmarkClickHandler.onBookmarkClick(mArticles.get(position));
                }
            });

            inflated.findViewById(R.id.share_icon).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    mOnShareClickHandler.onShareClick(mArticles.get(position));
                }
            });
        }

    }

    /*
     * just a helper method for the onBindViewHolder method to set the views in the layout file.
     * This method just initializes the recycler view for the horizontal scroll view
     */

    private void configureArticleViewHolderWithHorizontalScroll(ArticleViewHolderWithHorizontalScroll holder, int position)
    {



        holder.horizontalList.setLayoutManager(new LinearLayoutManager(holder.context, LinearLayoutManager.HORIZONTAL, false));
        holder.horizontalAdapter = new SectionRVAdapter(holder.context,holder.sections);
        holder.horizontalList.setAdapter(holder.horizontalAdapter);
        holder.horizontalAdapter.setDataset(holder.sections);

    }

    /*
     * this method is used to set the views for the cardlayout file.
     */

    private void configureArticleViewHolder(ArticleViewHolder holder, int position)
    {
        //Log.v(LOG_TAG, "The position variable is " + position);
        //Log.v(LOG_TAG, "The rowIndex is " + mRowIndex);
        holder.mAvatarView.setText(mArticles.get(position).getmAvatarInitial());
        holder.mAvatarNameView.setText(mArticles.get(position).getmAvatarName());

        String dateString = NewsFeederDateUtils.getSimpleDate(mArticles.get(position).getmDate());
        holder.mDate.setText(dateString);

        String imageUrl = mArticles.get(position).getmImageURL();

        Picasso.with(mContext)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error_img)
                .into(holder.mImageView);
        holder.mTheTitleView.setText(mArticles.get(position).getmTheTitle());
        // holder.mTheSubtitleView.setText(mArticles.get(position).getmTheSubtitle());
        holder.mTheThreeLinesView.setText(mArticles.get(position).getmTheThreeLines());
        //holder.mBookmarkView.setImageResource(mArticles.get(position).getmBookmarkResourceId());
        // holder.mHeartView.setImageResource(mArticles.get(position).getmHeartResourceId());
        String id = mArticles.get(position).getmId();
        if(!NewsPreferences.getmBookmarkIds(mContext).isEmpty() )
        {
              boolean isBookmarked = NewsPreferences.getmBookmarkIds(mContext)
                .contains(mArticles.get(position).getmId());
        if(isBookmarked)
        {
            Log.v(LOG_TAG, "bookmarked articles is true ");
            holder.mBookmarkImage.setBackgroundResource(R.drawable.bookmark);
        }
        }


    }

    public void clear()
    {
        mArticles.clear();
        //notifyDataSetChanged();
    }
}
