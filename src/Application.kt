package com.example

import com.example.data.checkIfUserPasswordIsCorrect
import com.example.route.loginRoute
import com.example.route.noteRoutes
import com.example.route.registerRoute
import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
        }
    }
    install(Authentication){
        configAuth()
    }
    install(Routing) {
        registerRoute()
        loginRoute()
        noteRoutes()
    }


}




private fun Authentication.Configuration.configAuth() {
    basic {
        realm = "Note Server"
        validate { credentials ->
            val email = credentials.name
            val password = credentials.password

            if(checkIfUserPasswordIsCorrect(email, password)) {
                UserIdPrincipal(email)
            } else {
                null
            }
        }
    }
}

