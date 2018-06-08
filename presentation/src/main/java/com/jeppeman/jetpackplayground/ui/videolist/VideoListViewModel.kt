package com.jeppeman.jetpackplayground.ui.videolist

import androidx.lifecycle.LiveData
import com.jeppeman.jetpackplayground.ui.base.IViewModel
import com.jeppeman.jetpackplayground.ui.base.ListItem

interface VideoListViewModel : IViewModel {
    val items: LiveData<List<ListItem>>
    fun refresh()
}
