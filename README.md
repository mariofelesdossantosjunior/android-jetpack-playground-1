
[![Build Status](https://travis-ci.com/jeppeman/android-jetpack-playground.svg?branch=master)](https://travis-ci.com/jeppeman/android-jetpack-playground)

# android-jetpack-playground

A small video player pet project with the purpose of exploring cutting edge Android development (AndroidX, architecture components), and combining the new tools with what has previously been my bread and butter; the main areas of exploration are:
* MotionLayout
* MVVM with LiveData and Android ViewModels
* Navigation architecture component (with safeargs)
* Jetpack testing, mainly isolated fragment unit tests that run both on device and the JVM with the same source code
* AndroidX package structure
    
These tools have been woven into my previous go-to project setup, which has consisted of the following (among other things):
* Clean architecture
* MVVM with databinding
* Dagger2
* RxJava2
* Retrofit
    
Following are some of the takeaways from each area of exploration and the project as a whole.

MotionLayout
---
I've been waiting for a tool like this for Android for quite some time, although it still has some
ways to go before it is mature, it is extremely promising and already very powerful. Being able to create
complex animations that are fully declarative is great. However, due to the editor not being released yet, 
it is a bit cumbersome to work with at the moment, declaring keyframes manually for complex
animations can be quite time consuming. <br/>
Below are a few silly animations from the project that showcases MotionLayout, click to watch with better quality on YouTube.

<a target="_blank" href="https://www.youtube.com/watch?v=wGbnyM_hJSQ"><img src="https://raw.githubusercontent.com/jeppeman/android-jetpack-playground/master/gifs/loader.gif" width="224" height="400" /></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a target="_blank" href="https://www.youtube.com/watch?v=mcBy2lza8zM"><img src="https://raw.githubusercontent.com/jeppeman/android-jetpack-playground/master/gifs/fullscreen.gif" width="224" height="400" /></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a target="_blank" href="https://www.youtube.com/watch?v=UZVqhDmo8M4"><img src="https://raw.githubusercontent.com/jeppeman/android-jetpack-playground/master/gifs/panel.gif" width="224" height="400" /></a>

MVVM with LiveData and Android ViewModels
---
LiveData and ViewModels are great tools for dealing with some of the common nuisances of Android development, it also worked pretty well (but wasn't painless) to integrate them with databinding, RxJava and Dagger2. <br />
The way LiveData is set up in this project is that it works as a complement to RxJava - RxJava is used in the data and domain layer, then LiveData only lives in the ViewModels and exposes data from the use cases to the views; pretty straight forward stuff. LiveData also integrate seamlessly with databinding which was very nice. The need for databinding felt much smaller with kotlin android extensions and LiveData, but I still like the feature of being able to bind clicks and other events directly on the ViewModels.<br/>
Getting ViewModels to work with Dagger2 was more challenging and made me want to pull my hair out a few times, but I ended up with a setup I'm quite happy with. I was facing three problems:
1. The `AndroidInjection` feature of Dagger2 does not support injection of `androidx.fragment.app.Fragment`
2. ViewModels do not share lifecycle with the fragment (or activity), hence simple constructor injection with fragment scope does not work, they need to be provided from a `ViewModel.Factory`.
3. To enable better isolation in unit testing of fragments, I wanted them to just have an `@Inject` dependency on the ViewModels, this means that ViewModels can not be created from the fragments.

The first problem was solved by adding support for the injection myself, basically just copy pasting the source from dagger and adding adapting for `androidx.fragment.app.Fragment`, after which injection can be achieved by calling `AndroidXInjection.inject(this)` from a fragment - source can be found <a href="https://github.com/jeppeman/android-jetpack-playground/tree/master/presentation/src/main/java/com/jeppeman/jetpackplayground/di/androidx">here</a>.<br/>
To tackle the second problem, I created a custom `ViewModel.Factory` which is provided with a multibound ViewModel map, it looks like this:
```kotlin
@PerFragment
class ViewModelFactory @Inject constructor(
        private val viewModelProviders: MutableMap<Class<out ViewModel>, Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return viewModelProviders[modelClass]?.get() as T
    }
}
```
By virtue of provider injection no ViewModel instances are created until they are actually needed. <br/>
The third problem was solved by simply providing ViewModels from dagger modules that have been provided with the ViewModelFactory and a fragment. Here is how one looks:
```kotlin
@Module
class VideoListModule {
    @Provides
    @IntoMap
    @ViewModelKey(VideoListViewModelImpl::class)
    fun provideVideoListViewModelIntoMap(videoListViewModelImpl: VideoListViewModelImpl): ViewModel {
        return videoListViewModelImpl
    }

    @Provides
    fun provideVideoListViewModel(
            videoListFragment: VideoListFragment,
            viewModelFactory: ViewModelFactory): VideoListViewModel {
        return ViewModelProviders.of(videoListFragment, viewModelFactory)[VideoListViewModelImpl::class.java]
    }
}
```
With this setup I was able to achieve unit testing of fragments fully independent of the ViewModels.

Navigation Architecture Component
---
This is also a very promising feature, navigation has been a messy problem on Android historically. Some pain points are definitely addressed by the navigation component, I do have one issue with it though; typically you want to carry out your navigation logic from the ViewModel, but since the `NavController`s (used to navigate) are tied to their contexts, ViewModels should not carry references to them to not risk memory leaks. <br/>
The way I chose to deal with this was to have the ViewModels being able to send a navigation request that the fragments can subscribe to and take appropriate action. I'm not completely happy with this approach but it works fairly well, I'm able to alleviate navigation logic from the fragments at least. I would be happy to receive feedback or suggestions on how to deal with this. <br/>
I'm really fond of the safe args feature though, who doesn't like type-safety eh?

Isolated fragment testing for both instrumentation and JVM with the same source
---
After having heard of the write-once-run-everywhere ambitions from the Google IO testing presentations I was very excited. Although Nitrogen is not released yet, I really wanted to take Robolectric 4.0 out for a spin. My ambition was to have fragment unit tests in a shared test folder that would run both instrumented and with Robolectric; since I have some fairly complex UI with animations and orientation changes in the project I thought this would be a tall order, but it was actually achievable in the end with some tinkering. I needed to create a custom shadow for `MotionLayout` (<a href="https://github.com/jeppeman/android-jetpack-playground/blob/master/presentation/src/test/java/com/jeppeman/jetpackplayground/shadows/ShadowMotionLayout.kt">here</a>) in order to make it work with Robolectric, but apart from that it was mostly smooth sailing. Getting the instrumented tests to run on Travis was pretty annoying though, but that's a different story. <br />
Isolating fragment tests has also been quite messy historically, but with the new `FragmentScenario` it has become a cakewalk basically. Here is an example of a fragment unit test from the project:
```kotlin
@Test
fun clickFastForward_ShouldDelegateToViewModel() {
    launch {
        `when`(mockPlayingState.initial).thenReturn(true)
        `when`(viewModel.state).thenReturn(mutableLiveDataOf(mockPlayingState))
    }

    onView(withId(R.id.fastForward)).check(matches(isVisibleToUser())).perform(click())

    verify(viewModel).onFastForwardClick()
}
```
`launch` is a helper method that calls the new `FragmentScenario.launchInContainer()` under the hood. The source can be found <a href="https://github.com/jeppeman/android-jetpack-playground/blob/master/presentation/src/sharedTest/java/com/jeppeman/jetpackplayground/ui/base/BaseFragmentTest.kt">here</a> and <a href="https://github.com/jeppeman/android-jetpack-playground/blob/master/presentation/src/sharedTest/java/com/jeppeman/jetpackplayground/ui/videodetail/VideoDetailFragmentTest.kt">here</a>.

AndroidX Package Structure
---
Moving away from the previous monolithic packages is a great initiative, I havn't run in to any bugs related to the new packages yet either. I suppose the only downside of it is that it's harder to keep track of new releases than it was before, since you inevitably end up with more dependencies; surely a price worth paying though.

Conclusion
---
I'm quite content with the outcome, there were a few bumps in the road, but all in all I'm really loving all the new tools that Google provide; they really do make life easier. Good times to be an Android dev for sure. <br /><br />
Contributions or any sort of feedback on the project are most welcome!

Future
---
I will continually update the project as new tools, or updates to existing ones, get released. One thing I have not touched upon yet here is persistence with Room, which I'm planning to add in the near future.