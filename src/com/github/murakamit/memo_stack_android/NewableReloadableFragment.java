
package com.github.murakamit.memo_stack_android;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public abstract class NewableReloadableFragment extends ReloadableFragment {
    static public final int MENU_NEW = ReloadableFragment.MENU_LAST + 1;
    static public final int MENU_LAST = MENU_NEW;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuHelper.add_menu(menu, MENU_NEW, R.string.new_memo, R.drawable.ic_action_new);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_NEW) {
            ((MainActivity) getActivity()).show_fragment_of_new_item();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
