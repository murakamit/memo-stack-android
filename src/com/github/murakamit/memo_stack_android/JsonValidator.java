
package com.github.murakamit.memo_stack_android;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonValidator {

    /**
     * @param {{@code response}
     * @return {@code null} if valid, or {@code String} if error.
     */
    public static String response(JSONObject response) {
        if (response == null) {
            return "response == null"; // error
        }
        try {
            return response.getString("error");
        } catch (JSONException e) {
            return null; // valid
        }
    }

    /**
     * @param {@code memo}
     * @return {@code null} if valid, or {@code String} if error.
     */
    public static String memo(JSONObject memo) {
        if (memo == null) {
            return "memo == null";
        }
        try {
            final long id = memo.getLong("id");
            if (id <= 0L) {
                return "invalid ID: " + id;
            }
            final String s = memo.getString("text");
            if (s.isEmpty()) {
                return "text is empty.";
            }
            if (s.length() > 300) {
                return "text is too long.";
            }
            return null; // valid
        } catch (JSONException e) {
            return "JSON parse error.";
        }
    }

}
