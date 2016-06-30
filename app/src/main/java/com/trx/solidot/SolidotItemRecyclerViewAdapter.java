package com.trx.solidot;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.trx.solidot.SolidotListFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link RSSItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class SolidotItemRecyclerViewAdapter extends RecyclerView.Adapter<SolidotItemRecyclerViewAdapter.ViewHolder> {

    private final List<RSSItem> solidotArticleList;
    private final OnListFragmentInteractionListener mListener;

    public SolidotItemRecyclerViewAdapter(List<RSSItem> items, OnListFragmentInteractionListener listener) {
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
        holder.mIdView.setText(solidotArticleList.get(position).getId());
        holder.mTitleView.setText(solidotArticleList.get(position).getTitle());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return solidotArticleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mTitleView;
        public final TextView mDescriptionView;
        public final TextView mpubdateView;
        public final TextView dccreatorView;
        public final TextView slashdepartmentView;

        public RSSItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mTitleView = (TextView) view.findViewById(R.id.title);
            mDescriptionView = (TextView) view.findViewById(R.id.description);
            mpubdateView = (TextView) view.findViewById(R.id.pubdate);
            dccreatorView = (TextView) view.findViewById(R.id.dccreator);
            slashdepartmentView = (TextView) view.findViewById(R.id.slashdepartment);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTitleView.getText() + "'";
        }
    }
}
