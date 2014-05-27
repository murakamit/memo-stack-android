
package com.github.murakamit.memo_stack_android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private static final String[] FULL_CLASSNAMES_OF_FRAGMENT_WITH_UI = {
            TitleFragment.class.getName(),
            SettingsFragment.class.getName(),
            ErrorListFragment.class.getName(),
            MemoListFragment.class.getName(),
            MemoShowFragment.class.getName(),
            MemoNewFragment.class.getName(),
    };

    private static final String[] FULL_CLASSNAMES_OF_FRAGMENT_WITHOUT_UI = {
            VolleyWorkerFragment.class.getName(),
    };

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    private TitleFragment mTitleFragment;
    private SettingsFragment mSettingsFragment;
    private ErrorListFragment mErrorListFragment;
    private MemoListFragment mMemoListFragment;
    private MemoShowFragment mMemoShowFragment;
    private MemoNewFragment mMemoNewFragment;

    private VolleyWorkerFragment mVolleyWorkerFragment;

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    private static ArrayList<String> full_classnames_of_fragments() {
        final int n = FULL_CLASSNAMES_OF_FRAGMENT_WITH_UI.length +
                FULL_CLASSNAMES_OF_FRAGMENT_WITHOUT_UI.length;
        final ArrayList<String> ary = new ArrayList<String>(n);
        for (String s : FULL_CLASSNAMES_OF_FRAGMENT_WITH_UI) {
            ary.add(s);
        }
        for (String s : FULL_CLASSNAMES_OF_FRAGMENT_WITHOUT_UI) {
            ary.add(s);
        }
        return ary;
    }

    private static String fullname2lastname(String fullname) {
        return fullname.substring(fullname.lastIndexOf('.') + 1);
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            register_fragments();
        } else {
            restore_fragments(savedInstanceState);
        }
    }

    private void register_fragments() {
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        RubySyntax.Array.each(FULL_CLASSNAMES_OF_FRAGMENT_WITH_UI,
                new SetMemberVariablesForFragment(fm, ft, R.id.container));
        RubySyntax.Array.each(FULL_CLASSNAMES_OF_FRAGMENT_WITHOUT_UI,
                new SetMemberVariablesForFragment(fm, ft, 0));
        ft.commit();
    }

    private void restore_fragments(Bundle bunble) {
        final FragmentManager fm = getSupportFragmentManager();
        for (String full_classname : full_classnames_of_fragments()) {
            String fieldname = "m" + fullname2lastname(full_classname);
            Fragment f = fm.getFragment(bunble, full_classname);
            try {
                MainActivity.class.getDeclaredField(fieldname).set(this, f);
            } catch (Exception e) {
                e.printStackTrace();
                throw new InternalError(e.toString());
            }
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    private class SetMemberVariablesForFragment implements RubySyntax.void_1<String> {
        private final FragmentManager fm;
        private final FragmentTransaction ft;
        private final int view_id;

        public SetMemberVariablesForFragment(FragmentManager fm,
                FragmentTransaction ft, int view_id) {
            this.fm = fm;
            this.ft = ft;
            this.view_id = view_id;
        }

        @Override
        public void execute(String full_classname) {
            try {
                final BaseFragment bf =
                        (BaseFragment) Class.forName(full_classname).newInstance();
                final String fieldname = "m" + fullname2lastname(full_classname);
                final Field field = MainActivity.class.getDeclaredField(fieldname);
                field.setAccessible(true); // avoid IllegalAccessException
                field.set(MainActivity.this, bf);
                if (fm.findFragmentByTag(full_classname) == null) {
                    ft.add(view_id, bf, full_classname);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new InternalError(e.toString());
            }
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        try {
            check_if_fragments_are_registered_and_equal_to_member_variables();
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalError(e.toString());
        }
        if (savedInstanceState == null) {
            detach_fragments_without(mTitleFragment);
        }
        show_contents();
    }

    private void check_if_fragments_are_registered_and_equal_to_member_variables()
            throws IllegalAccessException, IllegalArgumentException,
            NoSuchFieldException, InternalError {
        final FragmentManager fm = getSupportFragmentManager();
        for (String fullname : full_classnames_of_fragments()) {
            Fragment f = fm.findFragmentByTag(fullname);
            if (f == null) {
                throw new InternalError("for lack of Fragment: " + fullname);
            }
            String fieldname = "m" + fullname2lastname(fullname);
            if (!this.getClass().getDeclaredField(fieldname).get(this).equals(f)) {
                throw new InternalError("for lack of member variable: " + fieldname);
            }
        }
    }

    private void detach_fragments_without(BaseFragment bf) {
        final ArrayList<Fragment> ary = new ArrayList<Fragment>();
        final FragmentManager fm = getSupportFragmentManager();
        for (String classname : full_classnames_of_fragments()) {
            Fragment found = fm.findFragmentByTag(classname);
            if (found != null && found != bf) {
                ary.add(found);
            }
        }
        if (!ary.isEmpty()) {
            final FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : ary) {
                ft.detach(f);
            }
            ft.commit();
        }
    }

    private void show_contents() {
        findViewById(R.id.container).setVisibility(View.VISIBLE);
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        put_fragments(outState);
    }

    private void put_fragments(Bundle bundle) {
        final FragmentManager fm = getSupportFragmentManager();
        for (String tag : full_classnames_of_fragments()) {
            Fragment f = fm.findFragmentByTag(tag);
            if (f != null) {
                fm.putFragment(bundle, tag, f);
            }
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.system, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.menu_show_errors).setVisible(!mErrorListFragment.is_empty());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_show_errors:
                show_fragment(mErrorListFragment);
                return true;
            case R.id.menu_settings:
                open_settings();
                return true;
            case R.id.menu_quit:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public void open_settings() {
        show_fragment(mSettingsFragment);
    }

    public SharedPreferences get_prefs() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Override
    protected void onResume() {
        super.onResume();
        mVolleyWorkerFragment.start();
        if (get_current_fragment() == mTitleFragment) {
            mVolleyWorkerFragment.request_list(1L);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mVolleyWorkerFragment.stop();
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    private void show_fragment(BaseFragment bf) {
        if (bf == null) {
            return;
        }
        final BaseFragment current = get_current_fragment();
        if (current == bf) {
            return;
        }
        final FragmentManager fm = getSupportFragmentManager();
        final FragmentTransaction ft = fm.beginTransaction();
        ft.detach(current).attach(bf);
        if (bf instanceof Poppable) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    private BaseFragment get_current_fragment() {
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment f = fm.findFragmentByTag(get_current_fragment_classname());
        return (f != null && f instanceof BaseFragment) ? (BaseFragment) f : null;
    }

    private String get_current_fragment_classname() {
        final View container = findViewById(R.id.container);
        final View v = ViewGroupHelper.get_first_child(container);
        if (v == null) {
            return null;
        } else {
            final Object obj = v.getTag();
            return (obj instanceof String) ? (String) obj : null;
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public void add_error(String s) {
        if (mErrorListFragment != null) {
            mErrorListFragment.add(s);
        }
    }

    public void close_fragment_if_current(BaseFragment bf) {
        if (bf == get_current_fragment() && bf instanceof Poppable) {
            getSupportFragmentManager().popBackStack();
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    /**
     * request current page, or 1st page
     */
    public void request_list() {
        request_list(mMemoListFragment.get_page_number_to_reload());
    }

    public void request_list(long page) {
        mVolleyWorkerFragment.request_list(page);
    }

    public void response_list(ListResponse list) {
        mMemoListFragment.reset(list);
        show_fragment(mMemoListFragment);
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public void request_item(long id) {
        mVolleyWorkerFragment.request_item(id);
        mMemoShowFragment.reset(null);
        show_fragment(mMemoShowFragment);
    }

    public void response_item(JSONObject json) {
        mMemoShowFragment.reset(json);
    }

    public void cancel_items() {
        mVolleyWorkerFragment.cancel_items();
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public void request_destroy(long id) {
        mMemoListFragment.destroying(id);
        mVolleyWorkerFragment.request_destroy(id);
    }

    public void response_destroy(long id) {
        mMemoListFragment.destroyed(id);
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public void show_fragment_of_new_item() {
        show_fragment(mMemoNewFragment);
    }

    public void request_create(String text) {
        final JSONObject params = new JSONObject();
        try {
            params.put("text", text);
            mVolleyWorkerFragment.request_create(params);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void response_create(JSONObject item) {
        mMemoListFragment.created(item);
    }

}
