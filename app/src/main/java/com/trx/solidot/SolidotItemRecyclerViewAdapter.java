package com.trx.solidot;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trx.solidot.SolidotListFragment.OnTitleSelectedListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RSSItem} and makes a call to the
 * specified {@link OnTitleSelectedListener}.
 * TODO: Replace the implementation with code for your data type.
 */
class SolidotItemRecyclerViewAdapter extends RecyclerView.Adapter<SolidotItemRecyclerViewAdapter.ViewHolder> {

    private final List<RSSItem> solidotArticleList;
    private final OnTitleSelectedListener mListener;

    SolidotItemRecyclerViewAdapter(List<RSSItem> items, OnTitleSelectedListener listener) {
        solidotArticleList = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_solidotitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = solidotArticleList.get(position);
        //holder.mIdView.setText(solidotArticleList.get(position).getId());
        holder.mTitleView.setText(solidotArticleList.get(position).getTitle());
        holder.mDescriptionView.setText(solidotArticleList.get(position).getDescription());
        holder.mPubdateView.setText(solidotArticleList.get(position).getPubDate());
        holder.mDccreatorView.setText(solidotArticleList.get(position).getDc_creator());
        holder.mSlashdepartmentView.setText(solidotArticleList.get(position).getSlash_department());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onArticleSelected( holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return solidotArticleList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        //public final TextView mIdView;
        final TextView mTitleView;
        final TextView mDescriptionView;
        final TextView mPubdateView;
        final TextView mDccreatorView;
        final TextView mSlashdepartmentView;

        RSSItem mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            //mIdView = (TextView) view.findViewById(R.id.id);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mDescriptionView = (TextView) view.findViewById(R.id.description);
            mPubdateView = (TextView) view.findViewById(R.id.pubdate);
            mDccreatorView = (TextView) view.findViewById(R.id.dccreator);
            mSlashdepartmentView = (TextView) view.findViewById(R.id.slashdepartment);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
