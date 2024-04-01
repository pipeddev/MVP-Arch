package com.pipe.d.dev.mvparch.mainModule.model

import com.pipe.d.dev.mvparch.common.EventBus
import com.pipe.d.dev.mvparch.common.SportEvent
import com.pipe.d.dev.mvparch.common.getAdEventsInRealtime
import com.pipe.d.dev.mvparch.common.getResultEventsInRealtime
import com.pipe.d.dev.mvparch.common.someTime
import kotlinx.coroutines.delay

class MainRepository {
    suspend fun getEvents() {
        val events = getResultEventsInRealtime()
        events.forEach { event ->
            delay(someTime())
            publishEvent(event)
        }
    }

    suspend fun saveResult(result: SportEvent.ResultSuccess) {
        val response = if (result.isWarning)
            SportEvent.ResultError(30, "Error al guardar.")
        else SportEvent.SaveEvent
        publishEvent(response)
    }

    suspend fun registerAd() {
        val events = getAdEventsInRealtime()
        publishEvent(events.first())
    }

    suspend fun closeAd() {
        publishEvent(SportEvent.CloseAdEvent)
    }

    private suspend fun publishEvent(event: SportEvent) {
        EventBus.instance().publish(event)
    }
}