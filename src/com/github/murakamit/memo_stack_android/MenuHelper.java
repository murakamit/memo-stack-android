
package com.github.murakamit.memo_stack_android;

import android.view.Menu;
import android.view.MenuItem;

public class MenuHelper {
    /**
     * call add_menu(menu, item_id, string_id, icon_res,
     * MenuItem.SHOW_AS_ACTION_IF_ROOM)
     * 
     * @param menu {@code Menu}
     * @param item_id {@code int}
     * @param string_id {@code int}
     * @param icon_res {@code int}
     */
    static public void add_menu(Menu menu, int item_id, int string_id, int icon_res) {
        add_menu(menu, item_id, string_id, icon_res, MenuItem.SHOW_AS_ACTION_IF_ROOM);
    }

    static public void add_menu(Menu menu, int item_id, int string_id, int icon_res,
            int actionEnum) {
        if (menu.findItem(item_id) == null) {
            final MenuItem mi = menu.add(Menu.NONE, item_id, Menu.NONE, string_id);
            mi.setIcon(icon_res);
            mi.setShowAsAction(actionEnum);
        }
    }
}
