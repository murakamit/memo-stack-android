
package com.github.murakamit.memo_stack_android;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ListResponse implements Parcelable {

    /**
     * mMaxPage is calculated by self; not in response.
     */
    private long mMaxPage;
    private long mRequestPage;
    private NumberResponse mNumber;
    private JSONArray mData;

    public ListResponse() {
        mMaxPage = 0L;
        mRequestPage = 0L;
        mNumber = null;
        mData = null;
    }

    private ListResponse(long max_page, long request_page,
            NumberResponse number, JSONArray data) {
        mMaxPage = max_page;
        mRequestPage = request_page;
        mNumber = number;
        mData = data;
    }

    public static ListResponse newInstance(JSONObject response) {
        if (JsonValidator.response(response) != null) {
            return null;
        }
        try {
            final NumberResponse number =
                    NumberResponse.newInstance(response.getJSONObject("number"));
            if (number == null) {
                return null;
            }
            final long request_page = response.getLong("request_page");
            final JSONArray data = response.getJSONArray("data");
            return new ListResponse(number.calc_max_page(), request_page, number, data);
        } catch (JSONException e) {
            return null;
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public boolean is_valid() {
        return mNumber != null && mData != null;
    }

    /**
     * @return Total count of items, or 0L
     */
    public long count() {
        return (mNumber == null) ? 0L : mNumber.total();
    }

    /**
     * @return true if it is valid and empty, or false
     */
    public boolean is_empty() {
        return is_valid() ? (count() <= 0L) : true;
    }

    public long request_page() {
        return mRequestPage;
    }

    public JSONArray data() {
        return mData;
    }

    /**
     * @return index of max page(start from 1L), or 0L if error response
     */
    public long max_page() {
        return mMaxPage;
    }

    /**
     * @return index of current page(start from 1L), or 0L if error response
     */
    public long current_page() {
        if (0L < mMaxPage) {
            if (mRequestPage < 1L) {
                return 1L;
            } else {
                return (mMaxPage < mRequestPage) ? mMaxPage : mRequestPage;
            }
        } else {
            return 0L;
        }
    }

    public void push_front(JSONObject x) {
        final long current = current_page();
        if (is_valid()) {
            mNumber.increment_total();
            mMaxPage = mNumber.calc_max_page();
        } else {
            mMaxPage = 1L;
            mRequestPage = 0L;
            mNumber = new NumberResponse(1L, 1L);
        }
        if (current < 2L) {
            mData = ArrayHelper.push_front(mData, x);
        }
    }

    public void remove(long id) {
        if (!is_valid() || mNumber.is_empty()) {
            return;
        }
        final JSONArray ary = ArrayHelper.remove(mData, id);
        if (ary != null) {
            mNumber.decrement_total();
            mData = ary;
            mMaxPage = mNumber.calc_max_page();
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<ListResponse> CREATOR =
            new Parcelable.Creator<ListResponse>() {
                @Override
                public ListResponse createFromParcel(Parcel source) {
                    try {
                        return new ListResponse(source);
                    } catch (JSONException e) {
                        return new ListResponse();
                    }
                }

                @Override
                public ListResponse[] newArray(int size) {
                    return new ListResponse[size];
                }
            };

    public ListResponse(Parcel source) throws JSONException {
        mMaxPage = source.readLong();
        if (mMaxPage > 0L) {
            mRequestPage = source.readLong();
            mNumber = source.readParcelable(NumberResponse.class.getClassLoader());
            mData = new JSONArray(source.readString());
        } else {
            mRequestPage = 0L;
            mNumber = null;
            mData = null;
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mMaxPage);
        if (mMaxPage > 0L) {
            dest.writeLong(mRequestPage);
            dest.writeParcelable(mNumber, 0);
            dest.writeString(mData.toString());
        }
    }

}
