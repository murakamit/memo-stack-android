
package com.github.murakamit.memo_stack_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

public class MemoListFragment extends NewableReloadableFragment
        implements OnItemClickListener {

    private static final String BUNDLE_LIST = "BUNDLE_LIST";
    private static final String BUNDLE_DESTROYING = "BUNDLE_DESTROYING";

    private ListData mListData; // TODO
    private MemoListAdapter mAdapter;

    public MemoListFragment() {
        // nop
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListData = new ListData();
        if (savedInstanceState != null) {
            final ListResponse list = savedInstanceState.getParcelable(BUNDLE_LIST);
            final long[] destroying = savedInstanceState.getLongArray(BUNDLE_DESTROYING);
            if (list != null && !list.is_empty()) {
                mListData.list = list;
            }
            if (destroying != null && destroying.length > 0) {
                mListData.destroying = ArrayHelper.new_array_list_from(destroying);
            }
        }
        mAdapter = new MemoListAdapter(mListData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_memo_list, container, false);
        init_buttons(view);
        init_list(view);
        return view;
    }

    private void init_buttons(View view) {
        Button btn;
        btn = (Button) view.findViewById(R.id.memo_list_button_prev);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                request_page(-1L);
            }
        });
        btn = (Button) view.findViewById(R.id.memo_list_button_next);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                request_page(1L);
            }
        });
    }

    private void request_page(long delta) {
        final ListResponse list = mListData.list;
        if (list == null) {
            return;
        }
        final long page = list.current_page() + delta;
        if (0L < page && page <= list.max_page()) {
            ((MainActivity) getActivity()).request_list(page);
        }
    }

    private void init_list(View view) {
        final ListView lv = (ListView) view.findViewById(android.R.id.list);
        lv.setEmptyView(view.findViewById(android.R.id.empty));
        lv.setOnItemClickListener(this);
        lv.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        final ListResponse list = mListData.list;
        if (list != null && !list.is_empty()) {
            outState.putParcelable(BUNDLE_LIST, list);
        }
        final ArrayList<Long> destroying = mListData.destroying;
        if (destroying != null && !destroying.isEmpty()) {
            outState.putLongArray(BUNDLE_DESTROYING,
                    ArrayHelper.new_array_from(destroying));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        final ListView lv = (ListView) getView().findViewById(android.R.id.list);
        lv.setAdapter(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListData = null;
        mAdapter = null;
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Override
    public void onItemClick(AdapterView<?> listvew, View v, int position, long id) {
        ((MainActivity) getActivity()).request_item(id);
    }

    @Override
    protected void reload() {
        final MainActivity activity = (MainActivity) getActivity();
        activity.request_list();
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        refresh();
    }

    private void refresh() {
        mAdapter.notifyDataSetChanged();
        refresh_views();
    }

    private void refresh_views() {
        final View view = getView();
        if (view == null) {
            return;
        }
        final TextView tv_count =
                (TextView) view.findViewById(R.id.memo_list_text_count);
        final TextView tv_page =
                (TextView) view.findViewById(R.id.memo_list_text_page);
        final Button btn_prev =
                (Button) view.findViewById(R.id.memo_list_button_prev);
        btn_prev.setEnabled(false);
        final Button btn_nextv =
                (Button) view.findViewById(R.id.memo_list_button_next);
        btn_nextv.setEnabled(false);
        long count = mListData.getTotalCount();
        if (count > 0) {
            final ArrayList<Long> destroying = mListData.destroying;
            if (destroying != null) {
                count -= destroying.size();
            }
            String s = count + " memo";
            if (count != 1) {
                s += "s";
            }
            tv_count.setText(s + ".");
            long current = 1L;
            long max = 1L;
            final ListResponse list = mListData.list;
            if (list != null) {
                current = list.current_page();
                max = list.max_page();
            }
            tv_page.setText("Page: " + current + " / " + max);
            if (1L < current) {
                btn_prev.setEnabled(true);
            }
            if (current < max) {
                btn_nextv.setEnabled(true);
            }
        } else {
            tv_count.setText(R.string.empty);
            tv_page.setText(R.string.page);
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public void reset(ListResponse list) {
        mListData.list = list;
        mListData.destroying = null;
        refresh();
    }

    public void destroying(long id) {
        mListData.add_destroying(id);
        refresh();
    }

    public void destroyed(long id) {
        mListData.remove_destroying(id);
        refresh();
    }

    public void created(JSONObject item) {
        mListData.created(item);
        refresh();
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    /**
     * current page number, or 1L
     */
    public long get_page_number_to_reload() {
        final ListResponse list = mListData.list;
        return (list == null || list.is_empty()) ? 1L : list.current_page();
    }

}
