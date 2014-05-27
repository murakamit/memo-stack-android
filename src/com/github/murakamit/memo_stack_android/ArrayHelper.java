
package com.github.murakamit.memo_stack_android;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ArrayHelper {

    /**
     * create new ArrayList and copy from {@code src} to it. no effect to
     * {@code src}.
     */
    static public ArrayList<Long> new_array_list_from(long[] src) {
        final ArrayList<Long> dest = new ArrayList<Long>(src.length);
        for (long x : src) {
            dest.add(x);
        }
        return dest;
    }

    /**
     * create new Array and copy from {@code src} to it. no effect to
     * {@code src}.
     */
    static public long[] new_array_from(ArrayList<Long> src) {
        final long[] dest = new long[src.size()];
        for (int i = 0; i < src.size(); ++i) {
            dest[i] = src.get(i).longValue();
        }
        return dest;
    }

    /**
     * create new instance. no effect to src.
     */
    static public JSONArray push_front(JSONArray src, JSONObject x) {
        final JSONArray dest = new JSONArray();
        if (src == null) {
            dest.put(x);
        } else {
            final int n = src.length();
            dest.put(x);
            try {
                for (int i = 0; i < n; ++i) {
                    dest.put(src.get(i));
                }
            } catch (JSONException e) {
                // nop
            }
        }
        return dest;
    }

    /**
     * create new instance. no effect to src.
     * 
     * @param src {@code JSONArray}
     * @param id {@code long}
     * @return {@code JSONArray}, or null if not removed.
     */
    static public JSONArray remove(JSONArray src, long id) {
        if (src == null) {
            return null;
        }
        final int n = src.length();
        int target = -1;
        try {
            for (int i = 0; i < n; ++i) {
                if (src.getJSONObject(i).getLong("id") == id) {
                    target = i;
                    break;
                }
            }
        } catch (JSONException e) {
            // nop
        }
        if (target < 0) {
            return null;
        }
        final JSONArray dest = new JSONArray();
        try {
            for (int i = 0; i < n; ++i) {
                if (i != target) {
                    dest.put(src.get(i));
                }
            }
        } catch (JSONException e) {
            // nop
        }
        return dest;
    }

}
