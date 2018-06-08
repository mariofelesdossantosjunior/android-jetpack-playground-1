package com.jeppeman.jetpackplayground.ui.videolist

import androidx.lifecycle.MutableLiveData
import com.jeppeman.jetpackplayground.domain.interactor.GetVideosUseCase
import com.jeppeman.jetpackplayground.model.mapper.VideoModelMapper
import com.jeppeman.jetpackplayground.ui.base.BaseViewModel
import com.jeppeman.jetpackplayground.ui.base.ListItem
import timber.log.Timber
import javax.inject.Inject

class VideoListViewModelImpl @Inject constructor(
        val getVideosUseCase: GetVideosUseCase,
        val videoModelMapper: VideoModelMapper)
    : BaseViewModel(), VideoListViewModel {

    override val items = MutableLiveData<List<ListItem>>()

    private fun onFail(error: Throwable) {
        Timber.e(error)
        items.value = listOf(VideoListEmptyViewModel(this))
    }

    private fun onSuccess(videoViewModels: List<VideoListItemViewModel>) {
        items.value = videoViewModels
    }

    private fun addLoadingItem() {
        items.value = listOf(VideoListLoadingViewModel())
    }

    override fun onInitialize() {
        refresh()
    }

    override fun refresh() {
        getVideosUseCase.publish()
                .doOnSubscribe { addLoadingItem() }
                .map { videos -> videoModelMapper.transformToViewModel(videos, this) }
                .observeOnPostExecution()
                .subscribe(::onSuccess, ::onFail)
                .disposeOnCleared()
    }
}