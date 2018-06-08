package com.jeppeman.jetpackplayground.ui.videodetail

import android.os.Parcelable
import com.jeppeman.jetpackplayground.model.VideoModel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class VideoDetailParameter(val videoModel: VideoModel) : Parcelable