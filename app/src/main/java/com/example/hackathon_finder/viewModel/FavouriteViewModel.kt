package com.example.hackathon_finder.viewModel

import androidx.lifecycle.ViewModel
import com.example.hackathon_finder.data.Hackathon
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions


class FavouriteViewModel: ViewModel(){
    // still have to add content.
    private val db = FirebaseFirestore.getInstance()
    private val favouriteRef = db.collection("favourites")
    fun saveHackathon(hackathon: Hackathon){
        val docId = hackathon.name
            .replace("\\s+".toRegex(), "_")
            .replace(Regex("[^A-Za-z0-9_]"), "")
        favouriteRef.document(docId)
            .set(hackathon, SetOptions.merge())
            .addOnSuccessListener {
                println("data is saved successfully in database")
            }
            .addOnFailureListener { e ->
                println("error : $(e.message)")
            }
    }

    fun deleteHackathon(hackathon: Hackathon){
        val docId = hackathon.name
            .replace("\\s+".toRegex(), "_")
            .replace(Regex("[^A-Za-z0-9_]"), "")
        favouriteRef.document(docId)
            .delete()
            .addOnSuccessListener {
                println("Details of hackathon is successfully deleted")
            }
            .addOnFailureListener { e->
                println("error: $(e.message)")
            }
    }

    // function to check hackathon is already save or not
    fun isHackathonExist(
        hackathon: Hackathon,
        onResult:(Boolean) -> Unit
    ){
        val docId = hackathon.name
            .replace("\\s+".toRegex(), "_")
            .replace(Regex("[^A-Za-z0-9_]"), "")
        favouriteRef.document(docId)
            .get()
            .addOnSuccessListener { details->
                onResult(details.exists())
            }
            .addOnFailureListener {
                onResult(false)
            }
    }

}