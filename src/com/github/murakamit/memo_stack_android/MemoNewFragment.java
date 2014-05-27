
package com.github.murakamit.memo_stack_android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class MemoNewFragment extends BaseFragment implements Poppable {

    public MemoNewFragment() {
        // nop
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_memo_new, container, false);
        init_buttons(view);
        return view;
    }

    private void init_buttons(View view) {
        Button b;
        b = (Button) view.findViewById(R.id.memo_new_clear);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
        b = (Button) view.findViewById(R.id.memo_new_cancel);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        b = (Button) view.findViewById(R.id.memo_new_post);
        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });
    }

    private EditText get_edit() {
        final View view = getView();
        return (view == null) ?
                null : (EditText) view.findViewById(R.id.memo_new_edit);
    }

    private void clear() {
        final EditText edit = get_edit();
        if (edit != null) {
            edit.setText(null);
        }
    }

    private void close() {
        ((MainActivity) getActivity()).close_fragment_if_current(this);
    }

    private void post() {
        final EditText edit = get_edit();
        if (edit == null) {
            return;
        }
        final String text = edit.getText().toString();
        if (text == null || text.isEmpty()) {
            return;
        }
        ((MainActivity) getActivity()).request_create(text);
        close();
    }

}
