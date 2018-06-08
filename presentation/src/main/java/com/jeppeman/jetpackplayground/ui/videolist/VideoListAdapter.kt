package com.jeppeman.jetpackplayground.ui.videolist

import android.util.SparseIntArray
import androidx.recyclerview.widget.GridLayoutManager
import com.jeppeman.jetpackplayground.BR
import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.di.scopes.PerFragment
import com.jeppeman.jetpackplayground.ui.base.BaseAdapter
import javax.inject.Inject

@PerFragment
class VideoListAdapter @Inject constructor() : BaseAdapter() {

    val spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
        override fun getSpanSize(position: Int): Int {
            return when (dataSet[position]) {
                is VideoListItemViewModel -> 1
                else -> 2
            }
        }
    }

    override val viewBindingVariableMap = SparseIntArray().apply {
        listOf(
                R.layout.video_item_layout,
                R.layout.video_list_empty_layout,
                R.layout.video_list_loading_layout
        ).forEach { res ->
            put(res, BR.viewModel)
        }
    }
}