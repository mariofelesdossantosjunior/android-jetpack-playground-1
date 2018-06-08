package com.jeppeman.jetpackplayground.ui.base

import androidx.annotation.LayoutRes

interface ListItem {
    val id: String
    val type: Int
        @LayoutRes get
}