package com.example.firebaseapp.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseapp.databinding.BookItemBinding

class BookAdapter(private val list: MutableList<BookData>):
    RecyclerView.Adapter<BookAdapter.BookItemViewHolder>() {

    private var listener: BookAdapterClickInterface? = null

    fun setListener(listener: BookAdapterClickInterface) {
        this.listener = listener
    }

    inner class BookItemViewHolder(val binding: BookItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookItemViewHolder {
        val binding = BookItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BookItemViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.bookTitle.text = this.title
                binding.bookAuthor.text = this.author
                binding.bookGenre.text = this.genre

                binding.deleteBook.setOnClickListener {
                    listener?.onDeleteBookBtnClick(this)
                }

                binding.editBook.setOnClickListener {
                    listener?.onEditBookBtnClick(this)
                }
            }
        }
    }

    interface BookAdapterClickInterface {
        fun onDeleteBookBtnClick(bookData: BookData)
        fun onEditBookBtnClick(bookData: BookData)
    }
}