package com.eliabe.task.data.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Task (
    var id: String = "",
    var description: String = "",
    var status: Status = Status.TODO
):Parcelable