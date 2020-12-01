package com.example.data

import com.example.data.collections.Note
import com.example.data.collections.User
import org.litote.kmongo.contains
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.eq
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.set
import org.litote.kmongo.setValue

private val client = KMongo.createClient().coroutine
private val database = client.getDatabase("NotesDatabase")
private val users = database.getCollection<User>()
private val notes = database.getCollection<Note>()

suspend fun registerUser(user: User): Boolean {
    return users.insertOne(user).wasAcknowledged()
}

suspend fun checkIfUserExists(email: String): Boolean{
    return users.findOne(User::email eq email) != null
}

suspend fun checkIfUserPasswordIsCorrect(email: String, password: String): Boolean{
    val actualPassword = users.findOne(User::email eq email)?.password ?: return false
    return actualPassword == password
}

suspend fun getNotesForUser(email: String): List<Note> {
    return notes.find(Note::owners contains email).toList()
}

suspend fun saveNote(note: Note): Boolean {
    val noteExists = notes.findOneById(note.id) != null
    return if(noteExists) {
        notes.updateOneById(note.id, note).wasAcknowledged()
    }
    else {
        notes.insertOne(note).wasAcknowledged()
    }
}

suspend fun isOwnerOfNote(id: String, owner: String): Boolean {
    val note = notes.findOneById(id) ?: return false
    return owner in note.owners
}

suspend fun addOwnerToNote(id: String, owner: String): Boolean {
    val owners = notes.findOneById(id)?.owners ?: return false
    return notes.updateOneById(id, setValue(Note::owners, owners + owner)).wasAcknowledged()
}

suspend fun deleteNoteForUser(email: String, id: String): Boolean {
    val note = notes.findOne(Note::id eq id, Note::owners contains email)

    note?.let {
        if(it.owners.size > 1){
            // the note has multiple owners so we delete it only for the particular owner
            val newOwners = it.owners - email
            val updatedNote = notes.updateOne(Note::id eq it.id, setValue(Note::owners, newOwners))
            return updatedNote.wasAcknowledged()
        }
        return notes.deleteOneById(it.id).wasAcknowledged()
    } ?: return false

}













