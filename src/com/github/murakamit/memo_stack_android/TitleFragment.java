
package com.github.murakamit.memo_stack_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class TitleFragment extends ReloadableFragment {
    public TitleFragment() {
        // nop
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_title, container, false);
        init_button(view);
        return view;
    }

    private void init_button(View view) {
        final Button b = (Button) view.findViewById(R.id.title_button_settings);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).open_settings();
            }
        });
    }

    @Override
    protected void reload() {
        ((MainActivity) getActivity()).request_list();
    }

}
