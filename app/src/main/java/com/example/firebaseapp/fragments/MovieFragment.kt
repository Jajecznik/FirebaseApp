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
import com.example.firebaseapp.databinding.FragmentMovieBinding
import com.example.firebaseapp.utils.MovieAdapter
import com.example.firebaseapp.utils.MovieData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MovieFragment : Fragment(), AddMovieFragment.AddMovieClickListener,
    MovieAdapter.MovieAdapterClickInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentMovieBinding
    private lateinit var adapter: MovieAdapter
    private lateinit var mList: MutableList<MovieData>
    private var popUpMovie: AddMovieFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieBinding.inflate(inflater, container, false)
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
            .reference.child("Movies").child(auth.currentUser?.uid.toString())

        binding.movieRV.setHasFixedSize(true)
        binding.movieRV.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = MovieAdapter(mList)
        adapter.setListener(this)
        binding.movieRV.adapter = adapter
    }

    private fun registerEvents() {
        binding.addMovieBtn.setOnClickListener {
            if (popUpMovie != null) {
                childFragmentManager.beginTransaction().remove(popUpMovie!!).commit()
            }
            popUpMovie = AddMovieFragment()
            popUpMovie!!.setListener(this)
            popUpMovie!!.show(
                childFragmentManager, AddMovieFragment.TAG
            )
        }
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (movieSnapshot in snapshot.children) {
                    val movie = movieSnapshot.getValue(MovieData::class.java)
                    if (movie != null) {
                        movie.movieId = movieSnapshot.key.toString()
                        mList.add(movie)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onSaveMovie(
        title: String,
        director: String,
        genre: String,
        titleET: TextInputEditText,
        directorET: TextInputEditText,
        genreET: TextInputEditText
    ) {
        val categoryKey = databaseRef.push().key
        val categoryNode = HashMap<String, Any>()
        categoryNode["title"] = title
        categoryNode["director"] = director
        categoryNode["genre"] = genre
        val categoryRef = databaseRef.child(categoryKey!!)

        categoryRef.setValue(categoryNode).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Movie saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        titleET.text = null
        directorET.text = null
        genreET.text = null
        popUpMovie!!.dismiss()
    }

    override fun onUpdateMovie(
        movieData: MovieData,
        titleET: TextInputEditText,
        directorET: TextInputEditText,
        genreET: TextInputEditText
    ) {
        val movieRef = databaseRef.child(movieData.movieId)
        val updates = HashMap<String, Any>()
        updates["title"] = movieData.title
        updates["director"] = movieData.director
        updates["genre"] = movieData.genre

        movieRef.updateChildren(updates).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Movie updated", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }
            titleET.text = null
            directorET.text = null
            genreET.text = null
            popUpMovie!!.dismiss()
        }
    }

    override fun onDeleteMovieBtnClick(movieData: MovieData) {
        databaseRef.child(movieData.movieId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Movie deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditMovieBtnClick(movieData: MovieData) {
        if (popUpMovie != null) {
            childFragmentManager.beginTransaction().remove(popUpMovie!!).commit()
        }
        popUpMovie = AddMovieFragment.newInstance(movieData.movieId, movieData.title, movieData.director, movieData.genre)
        popUpMovie!!.setListener(this)
        popUpMovie!!.show(childFragmentManager, AddMovieFragment.TAG)
    }
}