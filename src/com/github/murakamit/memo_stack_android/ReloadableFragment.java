
package com.github.murakamit.memo_stack_android;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public abstract class ReloadableFragment extends BaseFragment {
    static protected final int MENU_FRONT = 100;
    static protected final int MENU_RELOAD = MENU_FRONT;
    static protected final int MENU_LAST = MENU_RELOAD;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuHelper.add_menu(menu, MENU_RELOAD, R.string.reload, R.drawable.ic_action_refresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == MENU_RELOAD) {
            reload();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    protected abstract void reload();
}
