package com.example.firebaseapp.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseapp.databinding.MovieItemBinding

class MovieAdapter(private val list: MutableList<MovieData>):
    RecyclerView.Adapter<MovieAdapter.MovieItemViewHolder>() {

    private var listener: MovieAdapterClickInterface? = null

    fun setListener(listener: MovieAdapterClickInterface) {
        this.listener = listener
    }

    inner class MovieItemViewHolder(val binding: MovieItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapter.MovieItemViewHolder {
        val binding = MovieItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MovieAdapter.MovieItemViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.movieTitle.text = this.title
                binding.movieDirector.text = this.director
                binding.movieGenre.text = this.genre

                binding.deleteMovie.setOnClickListener {
                    listener?.onDeleteMovieBtnClick(this)
                }

                binding.editMovie.setOnClickListener {
                    listener?.onEditMovieBtnClick(this)
                }
            }
        }
    }

    interface MovieAdapterClickInterface {
        fun onDeleteMovieBtnClick(movieData: MovieData)
        fun onEditMovieBtnClick(movieData: MovieData)
    }
}