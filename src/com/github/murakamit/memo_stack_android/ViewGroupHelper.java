
package com.github.murakamit.memo_stack_android;

import android.view.View;
import android.view.ViewGroup;

public class ViewGroupHelper {

    public static View get_first_child(View v) {
        if (v != null && v instanceof ViewGroup) {
            final ViewGroup vg = (ViewGroup) v;
            return get_first_child(vg);
        } else {
            return null;
        }
    }

    public static View get_first_child(ViewGroup vg) {
        return (vg != null && vg.getChildCount() > 0) ? vg.getChildAt(0) : null;
    }

}
