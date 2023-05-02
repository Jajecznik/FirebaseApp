package com.example.firebaseapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseapp.databinding.FragmentGameBinding
import com.example.firebaseapp.utils.GameAdapter
import com.example.firebaseapp.utils.GameData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class GameFragment : Fragment(), AddGameFragment.AddGameClickListener,
    GameAdapter.GameAdapterClickInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentGameBinding
    private lateinit var adapter: GameAdapter
    private lateinit var mList: MutableList<GameData>
    private var popUpGame: AddGameFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init(view)
        getDataFromFirebase()
        registerEvents()
    }

    private fun init(view: View) {
        navController = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance("https://fir-app-d80ed-default-rtdb.europe-west1.firebasedatabase.app")
            .reference.child("Games").child(auth.currentUser?.uid.toString())

        binding.gameRV.setHasFixedSize(true)
        binding.gameRV.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = GameAdapter(mList)
        adapter.setListener(this)
        binding.gameRV.adapter = adapter
    }

    private fun registerEvents() {
        binding.addGameBtn.setOnClickListener {
            if (popUpGame != null) {
                childFragmentManager.beginTransaction().remove(popUpGame!!).commit()
            }
            popUpGame = AddGameFragment()
            popUpGame!!.setListener(this)
            popUpGame!!.show(
                childFragmentManager, AddGameFragment.TAG
            )
        }
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (gameSnapshot in snapshot.children) {
                    val game = gameSnapshot.getValue(GameData::class.java)
                    if (game != null) {
                        game.gameId = gameSnapshot.key.toString()
                        mList.add(game)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSaveGame(
        title: String,
        developer: String,
        genre: String,
        titleET: TextInputEditText,
        developerET: TextInputEditText,
        genreET: TextInputEditText
    ) {
        val categoryKey = databaseRef.push().key
        val categoryNode = HashMap<String, Any>()
        categoryNode["title"] = title
        categoryNode["developer"] = developer
        categoryNode["genre"] = genre
        val categoryRef = databaseRef.child(categoryKey!!)

        categoryRef.setValue(categoryNode).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Game saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        titleET.text = null
        developerET.text = null
        genreET.text = null
        popUpGame!!.dismiss()
    }

    override fun onUpdateGame(
        gameData: GameData,
        titleET: TextInputEditText,
        developerET: TextInputEditText,
        genreET: TextInputEditText
    ) {
        val gameRef = databaseRef.child(gameData.gameId)
        val updates = HashMap<String, Any>()
        updates["title"] = gameData.title
        updates["developer"] = gameData.developer
        updates["genre"] = gameData.genre

        gameRef.updateChildren(updates).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Game updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }
            titleET.text = null
            developerET.text = null
            genreET.text = null
            popUpGame!!.dismiss()
        }
    }

    override fun onDeleteGameBtnClick(gameData: GameData) {
        databaseRef.child(gameData.gameId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Game deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditGameBtnClick(gameData: GameData) {
        if (popUpGame != null) {
            childFragmentManager.beginTransaction().remove(popUpGame!!).commit()
        }
        popUpGame = AddGameFragment.newInstance(gameData.gameId, gameData.title, gameData.developer, gameData.genre)
        popUpGame!!.setListener(this)
        popUpGame!!.show(childFragmentManager, AddGameFragment.TAG)
    }
}