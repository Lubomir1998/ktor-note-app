package com.example.route

import com.example.data.checkIfUserExists
import com.example.data.collections.User
import com.example.data.registerUser
import com.example.data.requests.AccountRequest
import com.example.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.features.ContentTransformationException
import io.ktor.http.*
import io.ktor.http.HttpStatusCode
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
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
                    if (registerUser(User(request.email, request.password))) {
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