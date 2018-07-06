package com.test.inscale.inscaletest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Timed
import io.reactivex.subscribers.DisposableSubscriber
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val dataObserver: DisposableSubscriber<Timed<DisplayObject>> = object : DisposableSubscriber<Timed<DisplayObject>>() {
        override fun onStart() {
            super.onStart()
            request(1)
        }

        override fun onError(e: Throwable) {
            //nothing to do here
        }

        override fun onNext(result: Timed<DisplayObject>) {
            val timeFromLastEmit = result.time()
            if (timeFromLastEmit > 100L) {
                showDisplayObject(result.value())
            }
            request(1)
        }

        override fun onComplete() {
            //nothing to do here
        }


    }

    private lateinit var dataObservable: Flowable<Timed<DisplayObject>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val eventEmitter = EventEmitter()

        val temperatureObservable = eventEmitter["temperature"]?.map { DataWithTimestamp(it, System.currentTimeMillis()) }
        val airObservable = eventEmitter["air"]?.map { DataWithTimestamp(it, System.currentTimeMillis()) }
        val humidityObservable = eventEmitter["humidity"]?.map { DataWithTimestamp(it, System.currentTimeMillis()) }

        dataObservable = Flowable.combineLatest<DataWithTimestamp, DataWithTimestamp, DataWithTimestamp, DisplayObject>(temperatureObservable, airObservable, humidityObservable,
                Function3 { temperature, air, humidity -> createDisplayObject(temperature, air, humidity) })
                .timeInterval()
    }

    override fun onResume() {
        super.onResume()
        dataObservable.subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(dataObserver)
    }

    override fun onPause() {
        super.onPause()
        dataObserver.dispose()
    }

    private data class DisplayObject(val temperature: String, val air: String, val humidity: String)
    private data class DataWithTimestamp(val data: Int, val timestamp: Long)

    private fun createDisplayObject(temperature: DataWithTimestamp, air: DataWithTimestamp, humidity: DataWithTimestamp): DisplayObject {
        val temperatureText = createDisplayValueForData(temperature)
        val airText = createDisplayValueForData(air)
        val humidityText = createDisplayValueForData(humidity)

        return DisplayObject(temperatureText, airText, humidityText)
    }

    private fun createDisplayValueForData(data: DataWithTimestamp): String {
        val currentTime = System.currentTimeMillis()
        return if (currentTime - data.timestamp >= 1000) "N/A" else data.data.toString()
    }

    private fun showDisplayObject(displayObject: DisplayObject) {
        tv_temp.text = displayObject.temperature
        tv_pressure.text = displayObject.air
        tv_humidity.text = displayObject.humidity
    }
}
