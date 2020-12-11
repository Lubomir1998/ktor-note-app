package com.example.route

import com.example.data.checkIfUserExists
import com.example.data.collections.User
import com.example.data.registerUser
import com.example.data.requests.AccountRequest
import com.example.data.responses.SimpleResponse
import com.example.secure.getHashWithSalt
import io.ktor.application.call
import io.ktor.features.ContentTransformationException
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.registerRoute(){
    route("/register"){
        post {
            withContext(Dispatchers.IO) {
                val request = try {
                    call.receive<AccountRequest>()
                }
                catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@withContext
                }
                val userExists = checkIfUserExists(request.email)
                if (!userExists) {
                    if (registerUser(User(request.email, getHashWithSalt(request.password)))) {
                        call.respond(HttpStatusCode.OK, SimpleResponse(true, "Successfully created account"))
                    }
                    else {
                        call.respond(HttpStatusCode.OK, SimpleResponse(false, "An unknown error occurred"))
                    }
                }
            }
        }

    }
}