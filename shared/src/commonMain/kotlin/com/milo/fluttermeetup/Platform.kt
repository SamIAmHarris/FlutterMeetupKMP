package com.milo.fluttermeetup

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform