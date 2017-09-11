package babydriver.newsclient.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsBrief;
import babydriver.newsclient.model.NewsRequester;
import babydriver.newsclient.model.Operation;
import babydriver.newsclient.model.Settings;
import babydriver.newsclient.ui.NewsShowFragment.OnNewsClickedListener;
import babydriver.newsclient.model.NewsRequester.OnRequestListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder>
{
    private List<NewsBrief> mValues;
    private OnNewsClickedListener mNewsClickedListener;
    private OnButtonClickedListener mButtonClickedListener;
    private OnRequestListener<Integer> mRequestListener;
    private Context mContext;
    private NewsBrief news;

    private enum NEWS_TYPE
    {
        NEWS_WITH_PICTURE,
        NEWS_WITHOUT_PICTURE
    }

    MyNewsRecyclerViewAdapter(List<NewsBrief> items,
                              OnButtonClickedListener buttonClickedListener,
                              OnNewsClickedListener newsClickedListener,
                              OnRequestListener<Integer> requestListener,
                              Context context)
    {

        mValues = items;
        mButtonClickedListener = buttonClickedListener;
        mNewsClickedListener = newsClickedListener;
        mContext = context;
        mRequestListener = requestListener;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        if (viewType == NEWS_TYPE.NEWS_WITH_PICTURE.ordinal())
            return new NewsWithPictureViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.fragment_news1, parent, false));
        else
            return new NewsWithoutPictureViewHolder(LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.fragment_news2, parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder old_holder, int position)
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
        if (old_holder instanceof NewsWithPictureViewHolder)
        {
            final NewsWithPictureViewHolder holder = (NewsWithPictureViewHolder)old_holder;
            holder.mItem = mValues.get(position);

            holder.mImage.setImageBitmap(null);
            NewsBrief news = mValues.get(position);
            File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String filename = "";
            try
            {
                assert dir != null;
                filename = dir.getPath();
            }
            catch (NullPointerException ignored) {}
            filename = filename + "/" + news.news_ID;
            File file = new File(filename);
            if (file.exists())
            {
                Bitmap map = BitmapFactory.decodeFile(filename);
                holder.mImage.setImageBitmap(map);
            }
            else
            {
                NewsRequester news_requester = new NewsRequester();
                news_requester.normalRequestPicture(news.newsPictures.get(0), filename, position, mRequestListener);
            }
            holder.mNewsTitle.setText(holder.mItem.news_Title);
            holder.mNewsSource.setText(holder.mItem.news_Source);
            if (holder.mItem.newsTime != null) holder.mNewsTime.setText(format.format(holder.mItem.newsTime));
            else
                holder.mNewsTime.setText("");

        }
        if (old_holder instanceof NewsWithoutPictureViewHolder)
        {
            final NewsWithoutPictureViewHolder holder = (NewsWithoutPictureViewHolder)old_holder;
            holder.mItem = mValues.get(position);
            holder.mNewsTitle.setText(holder.mItem.news_Title);
            holder.mNewsSource.setText(holder.mItem.news_Source);
            if (holder.mItem.newsTime != null) holder.mNewsTime.setText(format.format(holder.mItem.newsTime));
            else
                holder.mNewsTime.setText("");
        }
        old_holder.mView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (null != mNewsClickedListener)
                {
                    mNewsClickedListener.onNewsClicked(old_holder.mItem);
                }
            }
        });
        old_holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                news = mValues.get(old_holder.getAdapterPosition());
                return (news.equals(NewsShowFragment.nonNews));
            }
        });
        old_holder.setImage();
    }

    @Override
    public int getItemViewType(int position)
    {
        if (!Settings.isPreviewShowPicture) return NEWS_TYPE.NEWS_WITHOUT_PICTURE.ordinal();
        return (mValues.get(position).newsPictures.size() == 0 ? NEWS_TYPE.NEWS_WITHOUT_PICTURE.ordinal() : NEWS_TYPE.NEWS_WITH_PICTURE.ordinal());
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
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

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener
    {
        NewsBrief mItem;
        View mView;
        TextView mNewsTitle;
        TextView mNewsSource;
        TextView mNewsTime;
        ImageButton mLike;
        ImageButton mDownload;

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
                    news = mItem;
                    mButtonClickedListener.onButtonClicked(mContext.getString(R.string.like));
                    notifyDataSetChanged();
                }
            });
            mDownload.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    news = mItem;
                    mButtonClickedListener.onButtonClicked(mContext.getString(R.string.download));
                    notifyDataSetChanged();
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

    void setPicture(int pos)
    {
        notifyItemChanged(pos);
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

    NewsBrief getNews()
    {
        return news;
    }

    interface OnButtonClickedListener
    {
        void onButtonClicked(String type);
    }

}
