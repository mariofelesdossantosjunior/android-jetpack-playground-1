package com.jeppeman.jetpackplayground.ui.videolist

import android.os.Bundle
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.view.doOnLayout
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.GridLayoutManager
import com.jeppeman.jetpackplayground.R
import com.jeppeman.jetpackplayground.animation.SimpleAnimationListener
import com.jeppeman.jetpackplayground.databinding.FragmentVideoListBinding
import com.jeppeman.jetpackplayground.extensions.findViewWithTransitionName
import com.jeppeman.jetpackplayground.extensions.observe
import com.jeppeman.jetpackplayground.navigation.NavigationRequest
import com.jeppeman.jetpackplayground.ui.base.BaseFragment
import com.jeppeman.jetpackplayground.ui.base.ListItem
import kotlinx.android.synthetic.main.fragment_video_list.*
import javax.inject.Inject

class VideoListFragment : BaseFragment<FragmentVideoListBinding, VideoListViewModel>() {
    private val videoListAnimController: LayoutAnimationController by lazy {
        AnimationUtils.loadLayoutAnimation(requireContext(), R.anim.layout_animation_from_bottom)
    }

    @Inject
    override lateinit var viewModel: VideoListViewModel
    @Inject
    internal lateinit var videoListAdapter: VideoListAdapter
    override val layoutRes = R.layout.fragment_video_list

    private fun runVideoListAnimation(items: List<ListItem>) {
        if (items.size > 1) {
            videoList?.apply {
                layoutAnimation = videoListAnimController
                layoutAnimationListener = SimpleAnimationListener(onEnd = {
                    videoList?.layoutAnimation = null
                })
                scheduleLayoutAnimation()
            }
        }
    }

    private fun navigateToDetail(navigationRequest: NavigationRequest.ListToDetail) {
        val videoId = navigationRequest.param.videoModel.id
        navigate(
                destination = navigationRequest.destination,
                extras = view?.findViewWithTransitionName(videoId)?.let { view ->
                    FragmentNavigatorExtras(view to videoId)
                } ?: FragmentNavigatorExtras()
        )
    }

    private fun onItemsFetched(items: List<ListItem>) {
        videoListAdapter.updateItems(items)
        runVideoListAnimation(items)
    }

    override fun onNavigationRequest(navigationRequest: NavigationRequest) {
        when (navigationRequest) {
            is NavigationRequest.ListToDetail -> navigateToDetail(navigationRequest)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.items.observe(this, ::onItemsFetched)
    }

    override fun onStop() {
        super.onStop()
        postponeEnterTransition()
    }

    override fun onBindingCreated(binding: FragmentVideoListBinding) {
        binding.viewModel = viewModel
        binding.videoList.apply {
            adapter = videoListAdapter
            itemAnimator = null
            (layoutManager as? GridLayoutManager)?.spanSizeLookup = videoListAdapter.spanSizeLookup
            doOnLayout {
                startPostponedEnterTransition()
            }
        }
    }
}
