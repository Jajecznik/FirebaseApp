package com.example.firebaseapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.firebaseapp.R
import com.example.firebaseapp.databinding.FragmentAddBookBinding
import com.google.android.material.textfield.TextInputEditText


class AddBookFragment : DialogFragment() {

    private lateinit var binding: FragmentAddBookBinding
    private lateinit var listener: AddBookClickListener

    fun setListener(listener: AddBookClickListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        registerEvents()
    }

    private fun registerEvents() {
        binding.bookAddBtn.setOnClickListener {
            val title = binding.titleET.text.toString().trim()
            val author = binding.authorET.text.toString().trim()
            val genre = binding.genreET.text.toString().trim()

            if (title.isNotEmpty() && author.isNotEmpty()) {
                listener.onSaveTask(title, author, genre, binding.titleET, binding.authorET, binding.genreET)
            } else {
                Toast.makeText(context, "Title and Author fields should not be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bookClose.setOnClickListener {
            dismiss()
        }
    }

    interface AddBookClickListener {
        fun onSaveTask(title: String, author: String, genre: String,
                       titleET: TextInputEditText, authorET: TextInputEditText, genreET: TextInputEditText)
    }
}