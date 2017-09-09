package babydriver.newsclient.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import babydriver.newsclient.R;
import babydriver.newsclient.model.NewsBrief;
import babydriver.newsclient.model.NewsRequester;
import babydriver.newsclient.model.Settings;
import babydriver.newsclient.ui.NewsShowFragment.OnListFragmentInteractionListener;
import babydriver.newsclient.model.NewsRequester.onRequestListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link NewsBrief} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder>
{

    private final List<NewsBrief> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final onRequestListener<Integer> mRequestListener;
    private Context mContext;

    private enum NEWS_TYPE
    {
        NEWS_WITH_PICTURE,
        NEWS_WITHOUT_PICTURE
    }

    MyNewsRecyclerViewAdapter(List<NewsBrief> items, OnListFragmentInteractionListener listener, onRequestListener<Integer> requestListener, Context context)
    {

        mValues = items;
        mListener = listener;
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
                news_requester.requestPicture(news.newsPictures.get(0), filename, position, mRequestListener);
            }
            holder.mNewsTitle.setText(holder.mItem.news_Title);
            holder.mNewsSource.setText(holder.mItem.news_Source);
            holder.mNewsTime.setText(format.format(holder.mItem.newsTime));

            holder.mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (null != mListener)
                    {
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });
        }
        if (old_holder instanceof NewsWithoutPictureViewHolder)
        {
            final NewsWithoutPictureViewHolder holder = (NewsWithoutPictureViewHolder)old_holder;
            holder.mItem = mValues.get(position);
            holder.mNewsTitle.setText(holder.mItem.news_Title);
            holder.mNewsSource.setText(holder.mItem.news_Source);
            holder.mNewsTime.setText(format.format(holder.mItem.newsTime));

            holder.mView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (null != mListener)
                    {
                        mListener.onListFragmentInteraction(holder.mItem);
                    }
                }
            });
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder old_holder)
    {
//        if (old_holder instanceof NewsWithPictureViewHolder)
//        {
//            NewsWithPictureViewHolder holder = (NewsWithPictureViewHolder)old_holder;
//            holder.mImage.setImageDrawable(null);
//            holder.mImage.destroyDrawingCache();
//        }
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
        mValues.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        NewsBrief mItem;
        ViewHolder(View view)
        {
            super(view);
        }
    }

    void setPicture(int pos)
    {
        notifyItemChanged(pos);
    }

    private class NewsWithPictureViewHolder extends ViewHolder
    {
        final View mView;
        final TextView mNewsTitle;
        final TextView mNewsSource;
        final TextView mNewsTime;
        final ImageView mImage;

        NewsWithPictureViewHolder(View view)
        {
            super(view);
            mView = view;
            mNewsTitle = view.findViewById(R.id.news1_title);
            mNewsSource = view.findViewById(R.id.news1_source);
            mNewsTime = view.findViewById(R.id.news1_time);
            mImage = view.findViewById(R.id.news1_picture);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mNewsTitle.getText() + "'";
        }
    }

    private class NewsWithoutPictureViewHolder extends ViewHolder
    {
        final View mView;
        final TextView mNewsTitle;
        final TextView mNewsSource;
        final TextView mNewsTime;

        NewsWithoutPictureViewHolder(View view)
        {
            super(view);
            mView = view;
            mNewsTitle = view.findViewById(R.id.news2_title);
            mNewsSource = view.findViewById(R.id.news2_source);
            mNewsTime = view.findViewById(R.id.news2_time);
        }

        @Override
        public String toString()
        {
            return super.toString() + " '" + mNewsTitle.getText() + "'";
        }
    }
}
