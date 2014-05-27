
package com.github.murakamit.memo_stack_android;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.Time;

public class TimeText implements Parcelable {
    private String mTime;
    private String mText;

    public TimeText() {
        mTime = null;
        mText = null;
    }

    public TimeText(String s) {
        final Time t = new Time();
        t.setToNow();
        mTime = t.format2445();
        mText = s;
    }

    public String time() {
        return mTime;
    }

    public String text() {
        return mText;
    }

    // -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<TimeText> CREATOR =
            new Parcelable.Creator<TimeText>() {
                @Override
                public TimeText createFromParcel(Parcel source) {
                    return new TimeText(source);
                }

                @Override
                public TimeText[] newArray(int size) {
                    return new TimeText[size];
                }
            };

    public TimeText(Parcel source) {
        final String s = source.readString();
        if (s.isEmpty()) {
            mTime = null;
            mText = null;
        } else {
            mTime = s;
            mText = source.readString();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (mTime == null || mText == null) {
            dest.writeString("");
        } else {
            dest.writeString(mTime);
            dest.writeString(mText);
        }
    }

}
