
package com.github.murakamit.memo_stack_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ErrorListAdapter extends BaseAdapter {
    private final ArrayList<TimeText> mErrors;

    public ErrorListAdapter(ArrayList<TimeText> errors) {
        mErrors = errors;
    }

    @Override
    public int getCount() {
        return mErrors.size();
    }

    @Override
    public Object getItem(int position) {
        return mErrors.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mErrors.isEmpty()) {
            return null;
        }
        View v = convertView;
        if (v == null) {
            final Context c = parent.getContext();
            final LayoutInflater inflater =
                    (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_error_list_item, null);
        }
        final TimeText tt = mErrors.get(position);
        TextView tv;
        tv = (TextView) v.findViewById(R.id.error_list_item_time);
        tv.setText(tt.time());
        tv = (TextView) v.findViewById(R.id.error_list_item_text);
        tv.setText(tt.text());
        return v;
    }

}
