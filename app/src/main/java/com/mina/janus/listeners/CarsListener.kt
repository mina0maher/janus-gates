package com.mina.janus.listeners

import com.mina.janus.models.CarModelItem

interface CarsListener {
    fun onCarClicked(lastCheckedPosition:Int?,checkedCar:CarModelItem?)
}