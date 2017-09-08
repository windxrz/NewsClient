package babydriver.newsclient.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import babydriver.newsclient.R;
import babydriver.newsclient.ui.NewsShowFragment.OnListFragmentInteractionListener;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Objects;

/**
 * {@link RecyclerView.Adapter} that can display a {@link NewsBrief} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MyNewsRecyclerViewAdapter extends RecyclerView.Adapter<MyNewsRecyclerViewAdapter.ViewHolder>
{

    private final List<NewsBrief> mValues;
    private final OnListFragmentInteractionListener mListener;

    private enum NEWS_TYPE
    {
        NEWS_WITH_PICTURE,
        NEWS_WITHOUT_PICTURE
    }

    public MyNewsRecyclerViewAdapter(List<NewsBrief> items, OnListFragmentInteractionListener listener)
    {
        mValues = items;
        mListener = listener;
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
        if (old_holder instanceof NewsWithPictureViewHolder)
        {
            final NewsWithPictureViewHolder holder = (NewsWithPictureViewHolder)old_holder;
            holder.mItem = mValues.get(position);

//            try
//            {
//                URL url = new URL(holder.mItem.news_Pictures);
//                Log.e("url", holder.mItem.news_Pictures);
//                InputStream is = (InputStream)url.getContent();
//                Log.e("url", holder.mItem.news_Pictures);
//                Drawable drawable = Drawable.createFromStream(is, "src");
//                holder.mImage.setImageDrawable(drawable);
//            }
//            catch (Exception e)
//            {
//                Log.e(e.toString(), e.toString());
//            }
            holder.mNewsTitle.setText(holder.mItem.news_Title);
            holder.mNewsSource.setText(holder.mItem.news_Source);
            holder.mNewsTime.setText(holder.mItem.news_Time);

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
            holder.mNewsTime.setText(holder.mItem.news_Time);

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
    public int getItemViewType(int position)
    {
        return (Objects.equals(mValues.get(position).news_Pictures, "") ? NEWS_TYPE.NEWS_WITHOUT_PICTURE.ordinal() : NEWS_TYPE.NEWS_WITH_PICTURE.ordinal());
    }

    @Override
    public int getItemCount()
    {
        return mValues.size();
    }

    public void clear()
    {
        mValues.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<NewsBrief> list)
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
