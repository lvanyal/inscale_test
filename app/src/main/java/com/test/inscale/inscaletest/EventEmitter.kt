package com.test.inscale.inscaletest

import android.os.Build
import android.support.annotation.RequiresApi
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.reactivestreams.Subscriber
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import java.util.stream.IntStream
import io.reactivex.processors.PublishProcessor


//Random integers with random intervals observable container, EventEmitter is a React Native class.
//Here EvenEmitter is just a container for 3 data observables.
class EventEmitter {
    private val temperatureData = getObservableWithRandomValuesAndRandomIntervals()
    private val airPresureData = getObservableWithRandomValuesAndRandomIntervals()
    private val humidityData = getObservableWithRandomValuesAndRandomIntervals()

    private val dataMap = mapOf("temperature" to temperatureData, "air" to airPresureData,
            "humidity" to humidityData)

//    fun get(eventName: String) = dataMap[eventName]

    operator fun get(eventName: String) = dataMap[eventName]

    private fun getObservableWithRandomValuesAndRandomIntervals(): Flowable<Int>? {
        return getRandomValueObservable().concatMap {
            return@concatMap Flowable.just(it).delay(getRandomDelay(), TimeUnit.MILLISECONDS)
        }!!
    }

    var random = Random()

    private fun getRandomValueObservable(): Flowable<Int> {
        return Flowable.range(1, 10000000)
    }

    private fun getRandomDelay() = Random().nextInt(100).toLong() + 100


}