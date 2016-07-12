package com.trx.solidot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        return new SolidotListFragment();
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

        setHasOptionsMenu (true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i ("--->", "onCreateView");

        View view = inflater.inflate(R.layout.fragment_solidotlist, container, false);

        if (itemsList==null) {
            if(savedInstanceState != null) {
                itemsList = savedInstanceState.getParcelableArrayList(SAVED_RSS_LIST_KEY);
                updateDataList(itemsList);
            } else {
                startFetchRSSTask ();
            }
        } else {
            if (!itemsList.isEmpty()) {
                updateDataList (itemsList);
            } else {
                startFetchRSSTask();
            }
        }

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

        mListener.setDrawerIcon();

        return view;
    }

    /**
     * @param menu     The options menu in which you place your items.
     * @param inflater
     * @see #setHasOptionsMenu
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.listmenu, menu);

    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p/>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            startFetchRSSTask ();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (itemsList!=null) {
            if (!itemsList.isEmpty()) {
                updateDataList(itemsList);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(SAVED_RSS_LIST_KEY, itemsList);
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

        mListener.setDrawerIcon();
    }

    @Override
    public void onDetach() {
        Log.i ("--->", "onDetach");
        super.onDetach();
        mListener = null;
    }

    private void startFetchRSSTask() {
        FetchParseFeedTask fetchTask = new FetchParseFeedTask(this);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean bAlterFeed = sharedPreferences.getBoolean(getString(R.string.pref_feed_select_key), false);

        if (bAlterFeed) {
            fetchTask.execute(getString(R.string.rss2url));
        } else {
            fetchTask.execute(getString(R.string.rssurl));
        }
    }

    public void updateDataList(ArrayList<RSSItem> rssItems) {
        itemsList = rssItems;
        SolidotItemRecyclerViewAdapter adptr = new SolidotItemRecyclerViewAdapter(rssItems, mListener);
        recyclerView.setAdapter(adptr);
        adptr.notifyDataSetChanged();
        mListener.sendBackLastList(rssItems);
    }

    public void changeProgress(int v) {
        mListener.changeProgressBar (v);
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
        void sendBackLastList (ArrayList<RSSItem> itemsList);
        void changeProgressBar (int isShow);
        void setDrawerIcon ();

    }

}
