package com.example.whyyou

class HistoryMapPoint(Name: String?, date: String?, longitude: Double, latitude: Double) {
    var name: String? = null
    var date: String? = null
    var latitude = 0.0
    var longitude = 0.0

    init {
        this.name = Name
        this.date = date
        this.latitude = latitude
        this.longitude = longitude
    }
}