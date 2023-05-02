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
import com.example.firebaseapp.R
import com.example.firebaseapp.databinding.FragmentBookBinding
import com.example.firebaseapp.utils.BookAdapter
import com.example.firebaseapp.utils.BookData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class BookFragment : Fragment(), AddBookFragment.AddBookClickListener,
    BookAdapter.BookAdapterClickInterface {

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var navController: NavController
    private lateinit var binding: FragmentBookBinding
    private lateinit var popUpBook: AddBookFragment
    private lateinit var adapter: BookAdapter
    private lateinit var mList: MutableList<BookData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBookBinding.inflate(inflater, container, false)
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
            .reference.child("Books").child(auth.currentUser?.uid.toString())

        binding.bookRV.setHasFixedSize(true)
        binding.bookRV.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = BookAdapter(mList)
        adapter.setListener(this)
        binding.bookRV.adapter = adapter
    }

    private fun registerEvents() {
        binding.addBookBtn.setOnClickListener {
            popUpBook = AddBookFragment()
            popUpBook.setListener(this)
            popUpBook.show(
                childFragmentManager, "AddBookFragment"
            )
        }
    }

    private fun getDataFromFirebase() {
        databaseRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (bookSnapshot in snapshot.children) {
                    val book = bookSnapshot.getValue(BookData::class.java)
                    if (book != null) {
                        book.bookId = bookSnapshot.key.toString()
                        mList.add(book)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })
    }

    override fun onSaveTask(
        title: String,
        author: String,
        genre: String,
        titleET: TextInputEditText,
        authorET: TextInputEditText,
        genreET: TextInputEditText
    ) {
        val categoryKey = databaseRef.push().key
        val categoryNode = HashMap<String, Any>()
        categoryNode["title"] = title
        categoryNode["author"] = author
        categoryNode["genre"] = genre
        val categoryRef = databaseRef.child(categoryKey!!)

        categoryRef.setValue(categoryNode).addOnCompleteListener {
            if (it.isSuccessful) {
                titleET.text = null
                authorET.text = null
                genreET.text = null
            } else {
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
        popUpBook.dismiss()
    }

    override fun onDeleteBookBtnClick(bookData: BookData) {
        databaseRef.child(bookData.bookId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(context, "Book deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onEditBookBtnClick(bookData: BookData) {
        TODO("Not yet implemented")
    }
}