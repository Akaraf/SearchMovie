package com.raaf.android.searchmovie.dataModel

import android.os.Parcel
import android.os.Parcelable

data class Frame(
        var id: Int,//or int
        var url: String?//its frames.image
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString()) {
    }

    constructor() : this(0, "")

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Frame> {
        override fun createFromParcel(parcel: Parcel): Frame {
            return Frame(parcel)
        }

        override fun newArray(size: Int): Array<Frame?> {
            return arrayOfNulls(size)
        }
    }
}
