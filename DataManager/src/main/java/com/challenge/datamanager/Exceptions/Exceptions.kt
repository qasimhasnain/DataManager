package com.challenge.datamanager.Exceptions

import java.io.IOException


class NoInternet : IOException() {

    override val message: String?
        get() {
            return "No connectivity exception"
        }

}
