package com.example.hackathon_finder.viewModel

import androidx.compose.animation.core.snap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hackathon_finder.data.Hackathon
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class FavouriteViewModel: ViewModel(){
    // still have to add content.
    private val db = FirebaseFirestore.getInstance()
    private val favouriteRef = db.collection("favourites")

    private val _favouriteHackathons = MutableStateFlow<List<Hackathon>>(emptyList())
    val favouriteHackathons: StateFlow<List<Hackathon>> = _favouriteHackathons


    init {
        loadFavourites()
    }
    fun saveHackathon(hackathon: Hackathon){
        val docId = hackathon.name
            .replace("\\s+".toRegex(), "_")
            .replace(Regex("[^A-Za-z0-9_]"), "")
        favouriteRef.document(docId)
            .set(hackathon, SetOptions.merge())
            .addOnSuccessListener {
                println("data is saved successfully in database")
                loadFavourites()
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
                loadFavourites()
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

    fun loadFavourites(){
        favouriteRef.get()
            .addOnSuccessListener { snapshots ->
                val list = snapshots.documents.mapNotNull{it.toObject(Hackathon::class.java)}
                viewModelScope.launch {
                    _favouriteHackathons.value = list
                }
            }
            .addOnFailureListener { e ->
                println("Failed to load favourites: ${e.message}")
            }
    }


}