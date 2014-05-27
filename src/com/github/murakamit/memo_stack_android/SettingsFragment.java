
package com.github.murakamit.memo_stack_android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SettingsFragment extends BaseFragment implements Poppable, TextWatcher {
    // private static final String DEFAULT_SERVER = "http://10.0.2.2:3000";
    private static final String DEFAULT_SERVER = "http://192.168.1.101:3000";
    private static final String DEFAULT_VERSION = "1.0";

    public SettingsFragment() {
        // nop
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);
        init_url(view);
        init_buttons(view);
        return view;
    }

    private void init_url(View view) {
        final MainActivity activity = (MainActivity) getActivity();
        final SharedPreferences prefs = activity.get_prefs();
        String api_url = generate_api_url(DEFAULT_SERVER, DEFAULT_VERSION);
        String server = DEFAULT_SERVER;
        final String pref_api_url = prefs.getString(Prefs.API_URL, null);
        if (pref_api_url != null) {
            final Pair<String, String> pair = generate_server_and_version(pref_api_url);
            if (pair != null) {
                api_url = pref_api_url;
                server = pair.first;
            }
        }

        if (api_url != pref_api_url) {
            write_api_url(prefs, api_url);
        }

        final TextView tv = (TextView) view.findViewById(R.id.settings_text_api_url);
        tv.setText(api_url);
        final EditText edit = (EditText) view.findViewById(R.id.settings_edit_server);
        edit.setText(server);
        edit.addTextChangedListener(this);
    }

    private void init_buttons(View view) {
        Button b;
        b = (Button) view.findViewById(R.id.settings_button_server_0);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                set_edit(R.id.settings_text_server_0);

            }
        });
        b = (Button) view.findViewById(R.id.settings_button_server_1);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                set_edit(R.id.settings_text_server_1);
            }
        });
    }

    private void set_edit(int view_id) {
        final View view = getView();
        if (view != null) {
            final EditText edit = (EditText) view.findViewById(R.id.settings_edit_server);
            final TextView tv = (TextView) view.findViewById(view_id);
            edit.setText(tv.getText());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        write_api_url();
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // nop
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // nop
    }

    @Override
    public void afterTextChanged(Editable editable) {
        set_api_url(editable);
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
    private void write_api_url() {
        final View view = getView();
        if (view == null) {
            return;
        }
        final CharSequence api_url = get_text(view, R.id.settings_text_api_url);
        if (api_url == null) {
            return;
        }
        final String s = api_url.toString();
        if (s.isEmpty()) {
            return;
        }
        write_api_url(((MainActivity) getActivity()).get_prefs(), s);
    }

    private void write_api_url(SharedPreferences prefs, String api_url) {
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Prefs.API_URL, api_url);
        editor.apply();
    }

    private void set_api_url(Editable editable) {
        final View view = getView();
        if (view == null) {
            return;
        }
        final String server = editable.toString();
        final String version = get_version(view).toString();
        final String api_url = generate_api_url(server, version);
        final TextView tv = (TextView) view.findViewById(R.id.settings_text_api_url);
        tv.setText(api_url);
    }

    private CharSequence get_version(View view) {
        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.settings_rgroup_version);
        switch (rg.getCheckedRadioButtonId()) {
            case R.id.settings_radio_1_0:
                return get_text(view, R.id.settings_radio_1_0);
            default:
                return null;
        }
    }

    private CharSequence get_text(View view, int textview_id) {
        final TextView tv = (TextView) view.findViewById(textview_id);
        return tv.getText();
    }

    // --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- --- ---
    private static String add_slash_unless_last(String s) {
        if (s == null || s.isEmpty()) {
            return "/";
        } else {
            return (s.charAt(s.length() - 1) == '/') ? (new String(s)) : (s + "/");
        }
    }

    private static String remove_last_if_slash(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        } else {
            final int n1 = s.length() - 1;
            return (s.charAt(n1) == '/') ? s.substring(0, n1) : (new String(s));
        }
    }

    private static String generate_api_url(String server, String version) {
        if (server == null || server.isEmpty() ||
                version == null || version.isEmpty()) {
            return null;
        } else {
            return remove_last_if_slash(add_slash_unless_last(server) + version);
        }
    }

    private static Pair<String, String> generate_server_and_version(String api_url) {
        if (api_url == null || api_url.length() < 3) {
            return null;
        } else {
            final String s = remove_last_if_slash(api_url);
            final int i = s.lastIndexOf('/');
            if (i < 1 || i == (s.length() - 1)) {
                return null;
            } else {
                final String server = s.substring(0, i);
                final String version = s.substring(i + 1);
                return Pair.create(server, version);
            }
        }
    }

}
