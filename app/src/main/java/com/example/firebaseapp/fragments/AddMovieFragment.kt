package com.example.firebaseapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.firebaseapp.databinding.FragmentAddMovieBinding
import com.example.firebaseapp.utils.MovieData
import com.google.android.material.textfield.TextInputEditText


class AddMovieFragment : DialogFragment() {

    private lateinit var binding: FragmentAddMovieBinding
    private lateinit var listener: AddMovieClickListener
    private var movieData: MovieData? = null

    fun setListener(listener: AddMovieFragment.AddMovieClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "AddMovieFragment"

        @JvmStatic
        fun newInstance(movieId: String, title: String, director: String, genre: String) = AddMovieFragment().apply {
            arguments = Bundle().apply {
                putString("movieId", movieId)
                putString("title", title)
                putString("director", director)
                putString("genre", genre)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            movieData = MovieData(
                arguments?.getString("movieId").toString(),
                arguments?.getString("title").toString(),
                arguments?.getString("director").toString(),
                arguments?.getString("genre").toString()
            )
            binding.titleET.setText(movieData?.title)
            binding.directorET.setText(movieData?.director)
            binding.genreET.setText(movieData?.genre)
        }
        registerEvents()
    }

    private fun registerEvents() {
        binding.movieAddBtn.setOnClickListener {
            val title = binding.titleET.text.toString().trim()
            val director = binding.directorET.text.toString().trim()
            val genre = binding.genreET.text.toString().trim()

            if (title.isNotEmpty() && director.isNotEmpty()) {
                if (movieData == null) {
                    listener.onSaveMovie(title, director, genre, binding.titleET, binding.directorET, binding.genreET)
                } else {
                    movieData?.title = title
                    movieData?.director = director
                    movieData?.genre = genre
                    listener.onUpdateMovie(movieData!!, binding.titleET, binding.directorET, binding.genreET)
                }
            } else {
                Toast.makeText(context, "Title and Director fields should not be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.movieClose.setOnClickListener {
            dismiss()
        }
    }

    interface AddMovieClickListener {
        fun onSaveMovie(title: String, director: String, genre: String, titleET: TextInputEditText,
                       directorET: TextInputEditText, genreET: TextInputEditText)

        fun onUpdateMovie(movieData: MovieData, titleET: TextInputEditText,
                          directorET: TextInputEditText, genreET: TextInputEditText)
    }
}