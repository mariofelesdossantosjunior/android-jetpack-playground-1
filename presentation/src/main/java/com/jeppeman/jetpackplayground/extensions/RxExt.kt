package com.jeppeman.jetpackplayground.extensions

import io.reactivex.subjects.BehaviorSubject
import kotlin.reflect.KProperty

operator fun <T : Any> BehaviorSubject<T>.getValue(thisRef: Any, property: KProperty<*>): T =
        checkNotNull(value) { "Subject has no value" }

operator fun <T : Any> BehaviorSubject<T>.setValue(thisRef: Any, property: KProperty<*>, value: T): Unit = onNext(value)