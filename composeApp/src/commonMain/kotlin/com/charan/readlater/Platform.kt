package com.charan.readlater

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform