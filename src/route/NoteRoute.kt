package com.example.route

import com.example.data.collections.Note
import com.example.data.deleteNoteForUser
import com.example.data.getNotesForUser
import com.example.data.requests.DeleteNoteRequest
import com.example.data.saveNote
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.principal
import io.ktor.http.HttpStatusCode
import io.ktor.request.ContentTransformationException
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun Route.noteRoutes(){
    route("/getNotes"){
        authenticate {
            get {
                withContext(Dispatchers.IO){
                    val email = call.principal<UserIdPrincipal>()!!.name

                    val notes = getNotesForUser(email)

                    call.respond(HttpStatusCode.OK, notes)
                }
            }
        }
    }
    route("/deleteNote"){
        authenticate {
            post {
                withContext(Dispatchers.IO){
                    val email = call.principal<UserIdPrincipal>()!!.name
                    val request = try {
                        call.receive<DeleteNoteRequest>()
                    } catch (e: ContentTransformationException) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@withContext
                    }
                    if (deleteNoteForUser(email, request.id)) {
                        call.respond(HttpStatusCode.OK)
                    }
                    else {
                        call.respond(HttpStatusCode.Conflict)
                    }
                }
            }
        }
    }
    route("/saveNote"){
        authenticate {
            post {
                withContext(Dispatchers.IO) {
                    val note = try {
                        call.receive<Note>()
                    } catch (e: ContentTransformationException) {
                        call.respond(HttpStatusCode.BadRequest)
                        return@withContext
                    }
                    if(saveNote(note)) {
                        call.respond(HttpStatusCode.OK)
                    }
                    else {
                        call.respond(HttpStatusCode.Conflict)
                    }
                }
            }
        }
    }
}