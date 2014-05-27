
package com.github.murakamit.memo_stack_android;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class MemoListAdapter extends BaseAdapter {
    private final ListData mListData;

    public MemoListAdapter(ListData ld) {
        mListData = ld;
    }

    @Override
    public int getCount() {
        return (mListData == null) ? 0 : mListData.getPageCount();
    }

    @Override
    public Object getItem(int position) {
        return (mListData == null) ? null : mListData.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return (mListData == null) ? null : mListData.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mListData == null) {
            return null;
        }
        final JSONObject json = mListData.get_json(position);
        if (json == null) {
            return null;
        }
        View v = convertView;
        if (v == null) {
            final Context c = parent.getContext();
            final LayoutInflater inflater =
                    (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.fragment_memo_list_item, null);
        }
        try {
            final long id = json.getLong("id");
            final boolean b = mListData.is_destroying(id);
            TextView tv = (TextView) v.findViewById(R.id.memo_list_item_id);
            tv.setText(String.valueOf(id));
            set_textview(v, R.id.memo_list_item_id, String.valueOf(id), b);
            set_textview(v, R.id.memo_list_item_text, json.getString("text"), b);
        } catch (JSONException e) {
            v = null;
        }
        return v;
    }

    private static void set_textview(View view, int view_id, String s, boolean strike) {
        final TextView tv = (TextView) view.findViewById(view_id);
        tv.setText(s);
        int flags = tv.getPaintFlags();
        if (strike) {
            flags |= Paint.STRIKE_THRU_TEXT_FLAG;
        } else {
            flags &= ~Paint.STRIKE_THRU_TEXT_FLAG;
        }
        tv.getPaint().setFlags(flags);
    }

}
