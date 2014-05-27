
package com.github.murakamit.memo_stack_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MemoShowFragment extends ReloadableFragment implements Poppable {
    private static final String BUNDLE_ITEM = "BUNDLE_ITEM";
    private JSONObject mItem;

    public MemoShowFragment() {
        // nop
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mItem = read_bundle(savedInstanceState);
        }
    }

    private JSONObject read_bundle(Bundle b) {
        if (b == null) {
            return null;
        }
        final String s = b.getString(BUNDLE_ITEM);
        if (s == null) {
            return null;
        }
        try {
            return new JSONObject(s);
        } catch (JSONException e) {
            return null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_memo_show, container, false);
        init_button(view);
        return view;
    }

    private void init_button(View view) {
        final Button b = (Button) view.findViewById(R.id.memo_show_delete);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                open_dialog();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).cancel_items();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mItem != null) {
            outState.putString(BUNDLE_ITEM, mItem.toString());
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    private void open_dialog() {
        final DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
                b.setTitle("Confirm");
                b.setMessage("Are you sure you want to delete this?");
                b.setPositiveButton(R.string.delete, new DeleteListener());
                b.setNegativeButton(R.string.cancel, null);
                return b.create();
            }
        };
        df.show(getChildFragmentManager(), null);
    }

    private class DeleteListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            final MainActivity activity = (MainActivity) getActivity();
            if (activity != null) {
                activity.request_destroy(get_id());
                activity.close_fragment_if_current(MemoShowFragment.this);
            }
        }
    }

    private long get_id() {
        if (mItem == null) {
            return 0L;
        } else {
            try {
                return mItem.getLong("id");
            } catch (JSONException e) {
                return 0L;
            }
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        refresh();
    }

    private void refresh() {
        final View view = getView();
        if (view == null) {
            return;
        }
        String s_id = null;
        String s_text = null;
        String s_updated_at = null;
        boolean b = false;
        if (mItem != null) {
            try {
                s_id = mItem.getString("id");
                s_text = mItem.getString("text");
                s_updated_at = mItem.getString("updated_at");
                b = true;
            } catch (JSONException e) {
                // nop
            }
        }
        TextView tv;
        tv = (TextView) view.findViewById(R.id.memo_show_id);
        tv.setText(s_id);
        tv = (TextView) view.findViewById(R.id.memo_show_text);
        tv.setText(s_text);
        tv = (TextView) view.findViewById(R.id.memo_show_updated_at);
        tv.setText(s_updated_at);
        final Button btn = (Button) view.findViewById(R.id.memo_show_delete);
        btn.setEnabled(b);
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Override
    protected void reload() {
        final long id = get_id();
        if (id > 0L) {
            ((MainActivity) getActivity()).request_item(id);
        }
    }

    public void reset(JSONObject json) {
        final View v = getView();
        final ProgressBar pb = (v == null) ?
                null : (ProgressBar) v.findViewById(R.id.memo_show_progress);
        if (json == null) {
            if (pb != null) {
                pb.setVisibility(View.VISIBLE);
            }
            mItem = null;
        } else {
            if (pb != null) {
                pb.setVisibility(View.GONE);
            }
            mItem = (JsonValidator.response(json) == null &&
                    JsonValidator.memo(json) == null) ? json : null;
        }
        refresh();
    }
}
