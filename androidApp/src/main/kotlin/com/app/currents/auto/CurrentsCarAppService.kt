package com.app.currents.auto

import androidx.car.app.CarAppService
import androidx.car.app.Session
import androidx.car.app.validation.HostValidator

class CurrentsCarAppService : CarAppService() {


    override fun onCreateSession(): Session = CurrentsSession()

    override fun createHostValidator(): HostValidator =
        HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
}