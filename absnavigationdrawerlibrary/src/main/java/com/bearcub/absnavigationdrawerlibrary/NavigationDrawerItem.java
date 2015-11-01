package com.bearcub.absnavigationdrawerlibrary;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Home on 6/12/2015.
 */
public class NavigationDrawerItem implements Parcelable {
    public int imageId;
    public String label;

    public NavigationDrawerItem(){}

    public NavigationDrawerItem(Parcel in) {
        imageId = in.readInt();
        label = in.readString();
    }

    public static final Parcelable.Creator<NavigationDrawerItem> CREATOR = new Parcelable.Creator<NavigationDrawerItem>() {
        public NavigationDrawerItem createFromParcel(Parcel in) {
            return new NavigationDrawerItem(in);
        }
        public NavigationDrawerItem[] newArray(int size) {
            return new NavigationDrawerItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(imageId);
        dest.writeString(label);
    }
}
