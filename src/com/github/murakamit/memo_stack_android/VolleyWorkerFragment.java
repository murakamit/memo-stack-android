
package com.github.murakamit.memo_stack_android;

import android.app.Activity;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFilter;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class VolleyWorkerFragment extends BaseFragment implements ErrorListener {
    private static final String SLASH_RESOURCES = "/memos";
    private static final String REQUEST_SHOW = "REQUEST_SHOW";
    private RequestQueue mQueue;

    public VolleyWorkerFragment() {
        // nop
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mQueue = Volley.newRequestQueue(activity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop();
        cancel_all();
        mQueue = null;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.add_error(error.toString());
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public void start() {
        if (mQueue != null) {
            mQueue.start();
        }
    }

    public void stop() {
        if (mQueue != null) {
            mQueue.stop();
        }
    }

    public void cancel_all() {
        if (mQueue != null) {
            mQueue.cancelAll(new FilterAll());
        }
    }

    private static class FilterAll implements RequestFilter {
        @Override
        public boolean apply(Request<?> request) {
            return true;
        }
    }

    public void cancel_items() {
        if (mQueue != null) {
            mQueue.cancelAll(REQUEST_SHOW);
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    private String get_api_url() {
        final MainActivity activity = (MainActivity) getActivity();
        if (activity == null) {
            return null;
        }
        final SharedPreferences prefs = activity.get_prefs();
        return prefs.getString(Prefs.API_URL, null);
    }

    private static boolean is_blank(String s) {
        return s == null || s.isEmpty();
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public void request_list(long page) {
        final String api_url = get_api_url();
        if (is_blank(api_url)) {
            return;
        }
        final String url = api_url + SLASH_RESOURCES + "/?page=" + page;
        mQueue.add(new JsonObjectRequest(url, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        response_list(response);
                    }
                },
                this));
    }

    private void response_list(JSONObject response) {
        final MainActivity activity = (MainActivity) getActivity();
        String error_message = JsonValidator.response(response);
        if (error_message == null) {
            final ListResponse list = ListResponse.newInstance(response);
            if (list == null) {
                error_message = "invalid response.";
            } else {
                activity.response_list(list);
                return;
            }
        }
        activity.add_error(error_message);
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public void request_item(long id) {
        final String api_url = get_api_url();
        if (is_blank(api_url)) {
            return;
        }
        final String url = api_url + SLASH_RESOURCES + "/" + id;
        final Request<JSONObject> req = new JsonObjectRequest(url, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        response_item(response);
                    }
                },
                this);
        req.setTag(REQUEST_SHOW);
        mQueue.add(req);
    }

    private void response_item(JSONObject response) {
        final MainActivity activity = (MainActivity) getActivity();
        String error_message = JsonValidator.response(response);
        if (error_message == null) {
            error_message = JsonValidator.memo(response);
            if (error_message == null) {
                activity.response_item(response);
                return;
            }
        }
        activity.add_error(error_message);
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public void request_destroy(long id) {
        final String api_url = get_api_url();
        if (is_blank(api_url)) {
            return;
        }
        final String url = api_url + SLASH_RESOURCES + "/destroy/" + id;
        mQueue.add(new JsonObjectRequest(Method.POST, url, null,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        response_destroy(response);
                    }
                },
                this));
    }

    private void response_destroy(JSONObject response) {
        final MainActivity activity = (MainActivity) getActivity();
        String error_message = JsonValidator.response(response);
        if (error_message == null) {
            long id = 0L;
            try {
                id = response.getLong("id");
                if (id > 0L && response.getBoolean("destroyed")) {
                    error_message = null;
                    activity.response_destroy(id);
                }
            } catch (JSONException e) {
                error_message = "fail to destroy (ID: ";
                error_message += (id > 0L) ? String.valueOf(id) : "(missing)";
                error_message += ")";
            }
        }
        if (error_message != null) {
            activity.add_error(error_message);
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public void request_create(JSONObject params) {
        final String api_url = get_api_url();
        if (is_blank(api_url)) {
            return;
        }
        final String url = api_url + SLASH_RESOURCES;
        mQueue.add(new JsonObjectRequest(Method.POST, url, params,
                new Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        response_create(response);
                    }
                },
                this));
    }

    private void response_create(JSONObject response) {
        final MainActivity activity = (MainActivity) getActivity();
        String error_message = JsonValidator.response(response);
        if (error_message == null) {
            error_message = JsonValidator.memo(response);
            if (error_message == null) {
                activity.response_create(response);
                return;
            }
        }
        activity.add_error(error_message);
    }

}
