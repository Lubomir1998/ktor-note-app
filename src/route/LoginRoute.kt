package com.example.route

import com.example.data.checkIfUserPasswordIsCorrect
import com.example.data.requests.AccountRequest
import com.example.data.responses.SimpleResponse
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

fun Route.loginRoute(){
    route("/login"){
        post {
            withContext(Dispatchers.IO){
                val request = try {
                    call.receive<AccountRequest>()
                }
                catch (e: ContentTransformationException) {
                    call.respond(HttpStatusCode.BadRequest)
                    return@withContext
                }
                val isPasswordCorrect = checkIfUserPasswordIsCorrect(request.email, request.password)
                if(isPasswordCorrect){
                    call.respond(HttpStatusCode.OK, SimpleResponse(true, "You have successfully logged in"))
                }
                else{
                    call.respond(HttpStatusCode.OK, SimpleResponse(false, "Email or password is incorrect"))
                }
            }
        }
    }
}