package com.example

import com.example.route.registerRoute
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
            registerRoute()
    }
    install(ContentNegotiation){
        gson {
            setPrettyPrinting()
        }
    }



}
