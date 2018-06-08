package com.jeppeman.jetpackplayground.ui.videolist

import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.ui.base.ListItem
import javax.inject.Inject

class VideoListEmptyViewModel @Inject constructor(val parent: VideoListViewModel) : ListItem {
    override val id = "video_list_empty"
    override val type = R.layout.video_list_empty_layout
}