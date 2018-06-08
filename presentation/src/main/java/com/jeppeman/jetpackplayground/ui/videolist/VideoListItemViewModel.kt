package com.jeppeman.jetpackplayground.ui.videolist

import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.model.VideoModel
import com.jeppeman.jetpackplayground.navigation.NavigationRequest
import com.jeppeman.jetpackplayground.ui.base.ListItem
import com.jeppeman.jetpackplayground.ui.videodetail.VideoDetailParameter

class VideoListItemViewModel(val videoModel: VideoModel, val parent: VideoListViewModel) : ListItem {
    override val id = videoModel.id
    override val type = R.layout.video_item_layout

    fun onClick() {
        parent.requestNavigation(NavigationRequest.ListToDetail(VideoDetailParameter(videoModel)))
    }
}