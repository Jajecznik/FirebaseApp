package com.example.firebaseapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.firebaseapp.databinding.FragmentAddBookBinding
import com.example.firebaseapp.utils.BookData
import com.google.android.material.textfield.TextInputEditText


class AddBookFragment : DialogFragment() {

    private lateinit var binding: FragmentAddBookBinding
    private lateinit var listener: AddBookClickListener
    private var bookData: BookData? = null

    fun setListener(listener: AddBookClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "AddBookFragment"

        @JvmStatic
        fun newInstance(bookId: String, title: String, author: String, genre: String) = AddBookFragment().apply {
            arguments = Bundle().apply {
                putString("bookId", bookId)
                putString("title", title)
                putString("author", author)
                putString("genre", genre)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            bookData = BookData(
                arguments?.getString("bookId").toString(),
                arguments?.getString("title").toString(),
                arguments?.getString("author").toString(),
                arguments?.getString("genre").toString()
            )
            binding.titleET.setText(bookData?.title)
            binding.authorET.setText(bookData?.author)
            binding.genreET.setText(bookData?.genre)
        }
        registerEvents()
    }

    private fun registerEvents() {
        binding.bookAddBtn.setOnClickListener {
            val title = binding.titleET.text.toString().trim()
            val author = binding.authorET.text.toString().trim()
            val genre = binding.genreET.text.toString().trim()

            if (title.isNotEmpty() && author.isNotEmpty()) {
                if (bookData == null) {
                    listener.onSaveBook(title, author, genre, binding.titleET, binding.authorET, binding.genreET)
                } else {
                    bookData?.title = title
                    bookData?.author = author
                    bookData?.genre = genre
                    listener.onUpdateBook(bookData!!, binding.titleET, binding.authorET, binding.genreET)
                }
            } else {
                Toast.makeText(context, "Title and Author fields should not be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.bookClose.setOnClickListener {
            dismiss()
        }
    }

    interface AddBookClickListener {
        fun onSaveBook(title: String, author: String, genre: String, titleET: TextInputEditText,
                       authorET: TextInputEditText, genreET: TextInputEditText)

        fun onUpdateBook(bookData: BookData, titleET: TextInputEditText,
                         authorET: TextInputEditText, genreET: TextInputEditText)
    }
}