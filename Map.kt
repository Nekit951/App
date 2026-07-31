package com.example.maps

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class Map  : Application() {
    override fun onCreate() {
        super.onCreate()

        // Ключ и локаль устанавливаются ТУТ и строго ОДИН РАЗ за всю жизнь приложения!
        MapKitFactory.setApiKey("a6f298f8-a284-494e-bec0-ea34447203f6")
        MapKitFactory.setLocale("ru_RU")
        MapKitFactory.initialize(this)
    }
}