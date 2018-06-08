package com.jeppeman.jetpackplayground.navigation

import androidx.navigation.NavDirections
import com.jeppeman.jetpackplayground.ui.videodetail.VideoDetailParameter
import com.jeppeman.jetpackplayground.ui.videolist.VideoListFragmentDirections

sealed class NavigationRequest(val destination: NavDirections) {
    data class ListToDetail(val param: VideoDetailParameter)
        : NavigationRequest(VideoListFragmentDirections.actionVideoListFragmentToVideoDetailFragment(param))
}