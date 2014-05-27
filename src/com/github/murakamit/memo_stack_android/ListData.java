
package com.github.murakamit.memo_stack_android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListData {
    public ListResponse list;
    public ArrayList<Long> destroying;

    public ListData() {
        list = null;
        destroying = null;
    }

    public ListData(ListResponse list, ArrayList<Long> destroying) {
        this.list = list;
        this.destroying = destroying;
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public int getPageCount() {
        if (list == null) {
            return 0;
        }
        final JSONArray jary = list.data();
        return (jary == null) ? 0 : jary.length();
    }

    public long getTotalCount() {
        return (list == null) ? 0L : list.count();
    }

    public Object getItem(int position) {
        if (list == null) {
            return null;
        }
        JSONArray jary = list.data();
        if (jary == null) {
            return null;
        }
        try {
            return jary.get(position);
        } catch (JSONException e) {
            return null;
        }
    }

    public long getItemId(int position) {
        final JSONObject json = get_json(position);
        if (json == null) {
            return 0L;
        }
        try {
            return json.getLong("id");
        } catch (JSONException e) {
            return 0L;
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public JSONObject get_json(int position) {
        if (list == null) {
            return null;
        }
        JSONArray jary = list.data();
        if (jary == null) {
            return null;
        }
        try {
            return jary.getJSONObject(position);
        } catch (JSONException e) {
            return null;
        }
    }

    public boolean is_destroying(long id) {
        return (destroying == null) ? false : (destroying.indexOf(id) >= 0);
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public void add_destroying(long id) {
        if (destroying == null) {
            destroying = new ArrayList<Long>();
        }
        destroying.add(id);
    }

    public void remove_destroying(long id) {
        if (list != null && !list.is_empty()) {
            list.remove(id);
            if (list.is_empty()) {
                list = null;
            }
        }
        if (destroying != null && !destroying.isEmpty()) {
            destroying.remove(id);
            if (destroying.isEmpty()) {
                destroying = null;
            }
        }
    }

    public void created(JSONObject item) {
        if (list == null) {
            list = new ListResponse();
        }
        list.push_front(item);
        if (destroying != null && !destroying.isEmpty()) {
            try {
                destroying.remove(item.getLong("id"));
                if (destroying.isEmpty()) {
                    destroying = null;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
