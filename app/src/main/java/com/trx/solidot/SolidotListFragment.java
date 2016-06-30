package com.trx.solidot;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnTitleSelectedListener}
 * interface.
 */
public class SolidotListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String SAVED_RSS_LIST_KEY = "SAVED_RSS_LIST_KEY";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnTitleSelectedListener mListener;
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList <RSSItem> itemsList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SolidotListFragment() {
    }

    // TODO: Customize parameter initialization
    public static SolidotListFragment newInstance() {
        SolidotListFragment fragment = new SolidotListFragment();
        return fragment;
    }

    // TODO: Customize parameter initialization
    /*
    public static SolidotListFragment newInstance(int columnCount) {
        SolidotListFragment fragment = new SolidotListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i ("--->", "onCreate");
        context = getActivity();

        FetchParseFeedTask fetchTask = new FetchParseFeedTask(this);
        fetchTask.execute(getString(R.string.rssurl));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i ("--->", "onCreateView");

        View view = inflater.inflate(R.layout.fragment_solidotlist, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
        }
        return view;
    }

    @Override
    public void onResume() {
        if (itemsList!=null) {
            if (!itemsList.isEmpty()) {
                updateDataList(itemsList);
            }
        }
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArray(SAVED_RSS_LIST_KEY, itemsList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        Log.i ("--->", "onAttach");
        super.onAttach(context);
        if (context instanceof OnTitleSelectedListener) {
            mListener = (OnTitleSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        Log.i ("--->", "onDetach");
        super.onDetach();
        mListener = null;
    }

    public void updateDataList(ArrayList<RSSItem> rssItems) {
        itemsList = rssItems;
        recyclerView.setAdapter(new SolidotItemRecyclerViewAdapter(rssItems, mListener));

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTitleSelectedListener {
        // TODO: Update argument type and name
        void onArticleSelected (RSSItem item);
    }
}
