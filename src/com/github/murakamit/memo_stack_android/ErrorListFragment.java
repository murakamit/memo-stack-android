
package com.github.murakamit.memo_stack_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ErrorListFragment extends BaseFragment implements Poppable {
    private static final String BUNDLE_ERRORS = "BUNDLE_ERRORS";
    private ArrayList<TimeText> mErrors;
    private ErrorListAdapter mAdapter;

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public ErrorListFragment() {
        // nop
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            mErrors = new ArrayList<TimeText>();
        } else {
            mErrors = savedInstanceState.getParcelableArrayList(BUNDLE_ERRORS);
        }
        mAdapter = new ErrorListAdapter(mErrors);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_error_list, container, false);
        init_button(view);
        init_list(view);
        return view;
    }

    private void init_button(View view) {
        final Button b = (Button) view.findViewById(R.id.error_list_button_clear);
        b.setEnabled(false);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
    }

    private void clear() {
        if (!mErrors.isEmpty()) {
            mErrors.clear();
            refresh();
            getActivity().invalidateOptionsMenu();
        }
    }

    private void init_list(View view) {
        final ListView lv = (ListView) view.findViewById(android.R.id.list);
        lv.setAdapter(mAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_ERRORS, mErrors);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        refresh();
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    private void refresh() {
        mAdapter.notifyDataSetChanged();
        refresh_views();
    }

    private void refresh_views() {
        final View v = getView();
        if (v == null) {
            return;
        }
        final Button btn = (Button) v.findViewById(R.id.error_list_button_clear);
        final TextView tv = (TextView) v.findViewById(R.id.error_list_text_count);
        if (mErrors.isEmpty()) {
            btn.setEnabled(false);
            tv.setText(R.string.no_reported_errors);
        } else {
            btn.setEnabled(true);
            final int n = mErrors.size();
            String s = n + " error";
            if (n != 1) {
                s += "s";
            }
            tv.setText(s + ".");
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public boolean is_empty() {
        return mErrors.isEmpty();
    }

    public void add(String s) {
        mErrors.add(0, new TimeText(s)); // push_front
        refresh();
        getActivity().invalidateOptionsMenu();
    }

}
