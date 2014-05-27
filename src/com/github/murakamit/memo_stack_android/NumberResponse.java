
package com.github.murakamit.memo_stack_android;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class NumberResponse implements Parcelable {
    private long mTotal;
    private final long mPerPage;

    public NumberResponse() {
        mTotal = 0L;
        mPerPage = 0L;
    }

    public NumberResponse(long total, long per_page) {
        mTotal = total;
        mPerPage = per_page;
    }

    public static NumberResponse newInstance(JSONObject response) {
        if (JsonValidator.response(response) != null) {
            return null;
        }
        try {
            final long total = response.getLong("total");
            final long per_page = response.getLong("per_page");
            return (total < 0L || per_page < 1L) ?
                    null : new NumberResponse(total, per_page);
        } catch (JSONException e) {
            return null;
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    public long total() {
        return mTotal;
    }

    public long per_page() {
        return mPerPage;
    }

    public boolean is_empty() {
        return mTotal <= 0L;
    }

    public void increment_total() {
        mTotal += 1L;
    }

    public void decrement_total() {
        mTotal -= 1L;
    }

    public long calc_max_page() {
        if (mTotal == 0L) {
            return 1L;
        } else {
            if (mTotal > 0L && mPerPage > 0L) {
                final long q = mTotal / mPerPage;
                final long r = mTotal % mPerPage;
                return q + (r > 0L ? 1L : 0L);
            } else {
                return 0L;
            }
        }
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<NumberResponse> CREATOR =
            new Parcelable.Creator<NumberResponse>() {
                @Override
                public NumberResponse createFromParcel(Parcel source) {
                    return new NumberResponse(source);
                }

                @Override
                public NumberResponse[] newArray(int size) {
                    return new NumberResponse[size];
                }
            };

    public NumberResponse(Parcel source) {
        mTotal = source.readLong();
        mPerPage = source.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTotal);
        dest.writeLong(mPerPage);
    }

}
