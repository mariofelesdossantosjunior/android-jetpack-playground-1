package com.jeppeman.jetpackplayground.ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import com.jeppeman.jetpackplayground.di.androidx.AndroidXInjection
import com.jeppeman.jetpackplayground.navigation.NavigationRequest
import com.jeppeman.jetpackplayground.navigation.NavigationRequestListener

abstract class BaseFragment<TBinding : ViewDataBinding, TViewModel : IViewModel>
    : Fragment(), NavigationRequestListener {

    @get:LayoutRes
    protected abstract val layoutRes: Int
    abstract val viewModel: TViewModel

    protected abstract fun onBindingCreated(binding: TBinding)

    protected fun navigate(destination: NavDirections, extras: Navigator.Extras) {
        view?.findNavController()?.navigate(destination, extras)
    }

    override fun onNavigationRequest(navigationRequest: NavigationRequest) {

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidXInjection.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.registerNavigationRequestListener(this)
        lifecycle.addObserver(viewModel)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return DataBindingUtil.inflate<TBinding>(
                inflater,
                layoutRes,
                container,
                false
        ).let { binding ->
            binding.setLifecycleOwner(this)
            onBindingCreated(binding)
            return@let binding.root
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.unregisterNavigationRequestListener(this)
    }
}