package babydriver.newsclient.ui;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import babydriver.newsclient.R;
import babydriver.newsclient.controller.MyApplication;
import babydriver.newsclient.model.NewsBrief;
import babydriver.newsclient.controller.Operation;
import babydriver.newsclient.ui.NewsShowFragment.OnNewsClickedListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder>
{
    private List<NewsBrief> mValues;
    private OnNewsClickedListener mNewsClickedListener;
    private OnButtonClickedListener mButtonClickedListener;
    private Context mContext;
    private int last_news;

    private enum NEWS_TYPE
    {
        NEWS_WITH_PICTURE,
        NEWS_WITHOUT_PICTURE,
        LOAD_MORE
    }

    MyNewsRecyclerViewAdapter(List<NewsBrief> items,
                              OnButtonClickedListener buttonClickedListener,
                              OnNewsClickedListener newsClickedListener,
                              Context context)
    {

        mValues = items;
        mButtonClickedListener = buttonClickedListener;
        mNewsClickedListener = newsClickedListener;
        mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == NEWS_TYPE.NEWS_WITH_PICTURE.ordinal())
            return new NewsWithPictureViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.fragment_news1, parent, false));
        else if (viewType == NEWS_TYPE.NEWS_WITHOUT_PICTURE.ordinal())
            return new NewsWithoutPictureViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.fragment_news2, parent, false));
        else
            return new FooterViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.fragment_news_load_more, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder old_holder, int position)
    {
        if (old_holder instanceof FooterViewHolder)
        {
            ((FooterViewHolder)(old_holder)).load_more.setProgress(0, true);
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        old_holder.mItem = mValues.get(position);
        old_holder.mNewsTitle.setText(old_holder.mItem.news_Title);
        if (Operation.isRead(old_holder.mItem.news_ID))
            old_holder.mNewsTitle.setTextColor(mContext.getColor(R.color.textRead));
        else
            old_holder.mNewsTitle.setTextColor(mContext.getColor(R.color.textUnread));
        old_holder.mNewsSource.setText(old_holder.mItem.news_Source);
        old_holder.pos = old_holder.getAdapterPosition();
        if (old_holder.mItem.newsTime != null) old_holder.mNewsTime.setText(format.format(old_holder.mItem.newsTime));
        else
            old_holder.mNewsTime.setText("");

        if (old_holder instanceof NewsWithPictureViewHolder)
        {
            final NewsWithPictureViewHolder holder = (NewsWithPictureViewHolder)old_holder;
            Resources r = mContext.getResources();
            Picasso.with(mContext).setIndicatorsEnabled(true);
            Picasso.with(mContext)
                    .load(holder.mItem.newsPictures.get(0))
                    .placeholder(R.drawable.placeholder)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .resize((int) r.getDimension(R.dimen.image_outline_width), (int) r.getDimension(R.dimen.image_outline_height))
                    .centerCrop()
                    .into(holder.mImage);
        }

        old_holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mNewsClickedListener)
                {
                    notifyItemChanged(old_holder.pos);
                    mNewsClickedListener.onNewsClicked(old_holder.mItem);
                }
            }
        });
        old_holder.mView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                last_news = old_holder.getAdapterPosition();
                return (mValues.get(last_news).equals(NewsShowFragment.nonNews));
            }
        });
        old_holder.setImage();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (mValues.get(position) == null) return NEWS_TYPE.LOAD_MORE.ordinal();
        if (!MyApplication.isPreviewShowPicture) return NEWS_TYPE.NEWS_WITHOUT_PICTURE.ordinal();
        return (mValues.get(position).newsPictures.size() == 0 ? NEWS_TYPE.NEWS_WITHOUT_PICTURE.ordinal() : NEWS_TYPE.NEWS_WITH_PICTURE.ordinal());
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }

    List<NewsBrief> getList()
    {
        return mValues;
    }

    void clear()
    {
        mValues.clear();
        notifyDataSetChanged();
    }

    void addAll(List<NewsBrief> list)
    {
        int k = list.size();
        mValues.addAll(list);
        notifyItemRangeChanged(k, list.size());
    }

    void addProgressBar()
    {
        mValues.add(null);
        notifyItemInserted(mValues.size() - 1);
    }

    boolean hasProgressBar()
    {
        return mValues.size() != 0 && (mValues.get(mValues.size() - 1) == null);
    }

    void removeProgressBar()
    {
        if (mValues.size() > 0 && mValues.get(mValues.size() - 1) == null)
        {
            mValues.remove(mValues.size() - 1);
            notifyItemRemoved(mValues.size());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener
    {
        NewsBrief mItem;
        View mView;
        TextView mNewsTitle;
        TextView mNewsSource;
        TextView mNewsTime;
        ImageButton mLike;
        ImageButton mDownload;
        int pos;

        ViewHolder(View view)
        {
            super(view);
            mView = view;
            view.setOnCreateContextMenuListener(this);

        }

        void setImage()
        {
            if (mItem.equals(NewsShowFragment.nonNews))
            {
                mLike.setVisibility(View.GONE);
                mDownload.setVisibility(View.GONE);
            }
            else
            {
                mLike.setVisibility(View.VISIBLE);
                mDownload.setVisibility(View.VISIBLE);
                if (Operation.isFavorite(mItem.news_ID))
                    mLike.setImageResource(R.drawable.ic_star_black_24dp);
                else
                    mLike.setImageResource(R.drawable.ic_star_border_black_24dp);
                mDownload.setEnabled(true);
                if (Operation.isDownloaded(mItem.news_ID))
                    mDownload.setImageResource(R.drawable.ic_delete_black_24dp);
                else if (Operation.isDownloading(mItem.news_ID))
                {
                    mDownload.setImageResource(R.drawable.ic_more_horiz_black_24dp);
                    mDownload.setEnabled(false);
                } else
                    mDownload.setImageResource(R.drawable.ic_arrow_downward_black_24dp);
            }
        }

        void set()
        {

            mLike.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    last_news = pos;
                    mButtonClickedListener.onButtonClicked(mContext.getString(R.string.like));
                }
            });
            mDownload.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    last_news = pos;
                    mButtonClickedListener.onButtonClicked(mContext.getString(R.string.download));
                }
            });
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo)
        {
            if (Operation.isFavorite(mItem.news_ID))
                contextMenu.add(R.string.unlike);
            else
                contextMenu.add(R.string.like);
            if (Operation.isDownloaded(mItem.news_ID))
                contextMenu.add(R.string.delete);
            else if (Operation.isDownloading(mItem.news_ID))
                contextMenu.add(R.string.downloading);
            else
                contextMenu.add(R.string.download);
            if (Operation.isDownloading(mItem.news_ID))
                contextMenu.getItem(1).setEnabled(false);
            else
                contextMenu.getItem(1).setEnabled(true);
        }
    }

    private class NewsWithPictureViewHolder extends ViewHolder
    {

        ImageView mImage;

        NewsWithPictureViewHolder(View view)
        {
            super(view);
            mNewsTitle = view.findViewById(R.id.news1_title);
            mNewsSource = view.findViewById(R.id.news1_source);
            mNewsTime = view.findViewById(R.id.news1_time);
            mLike = view.findViewById(R.id.new1_like);
            mDownload = view.findViewById(R.id.news1_download);
            mImage = view.findViewById(R.id.news1_picture);
            set();
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mNewsTitle.getText() + "'";
        }
    }

    private class NewsWithoutPictureViewHolder extends ViewHolder
    {
        NewsWithoutPictureViewHolder(View view)
        {
            super(view);
            mNewsTitle = view.findViewById(R.id.news2_title);
            mNewsSource = view.findViewById(R.id.news2_source);
            mNewsTime = view.findViewById(R.id.news2_time);
            mLike = view.findViewById(R.id.news2_like);
            mDownload = view.findViewById(R.id.news2_download);
            set();
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mNewsTitle.getText() + "'";
        }
    }


    private class FooterViewHolder extends ViewHolder
    {
        ProgressBar load_more;

        FooterViewHolder(View view)
        {
            super(view);
            load_more = view.findViewById(R.id.load_more);
        }
    }


    int getNews()
    {
        return last_news;
    }

    interface OnButtonClickedListener
    {
        void onButtonClicked(String type);
    }

}
