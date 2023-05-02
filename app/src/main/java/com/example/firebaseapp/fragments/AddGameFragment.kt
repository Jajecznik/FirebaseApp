package com.example.firebaseapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.firebaseapp.databinding.FragmentAddGameBinding
import com.example.firebaseapp.utils.GameData
import com.google.android.material.textfield.TextInputEditText


class AddGameFragment : DialogFragment() {

    private lateinit var binding: FragmentAddGameBinding
    private lateinit var listener: AddGameClickListener
    private var gameData: GameData? = null

    fun setListener(listener: AddGameClickListener) {
        this.listener = listener
    }

    companion object {
        const val TAG = "AddGameFragment"

        @JvmStatic
        fun newInstance(gameId: String, title: String, developer: String, genre: String) = AddGameFragment().apply {
            arguments = Bundle().apply {
                putString("gameId", gameId)
                putString("title", title)
                putString("developer", developer)
                putString("genre", genre)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            gameData = GameData(
                arguments?.getString("gameId").toString(),
                arguments?.getString("title").toString(),
                arguments?.getString("developer").toString(),
                arguments?.getString("genre").toString()
            )
            binding.titleET.setText(gameData?.title)
            binding.developerET.setText(gameData?.developer)
            binding.genreET.setText(gameData?.genre)
        }
        registerEvents()
    }

    private fun registerEvents() {
        binding.gameAddBtn.setOnClickListener {
            val title = binding.titleET.text.toString().trim()
            val developer = binding.developerET.text.toString().trim()
            val genre = binding.genreET.text.toString().trim()

            if (title.isNotEmpty() && developer.isNotEmpty()) {
                if (gameData == null) {
                    listener.onSaveGame(title, developer, genre, binding.titleET, binding.developerET, binding.genreET)
                } else {
                    gameData?.title = title
                    gameData?.developer = developer
                    gameData?.genre = genre
                    listener.onUpdateGame(gameData!!, binding.titleET, binding.developerET, binding.genreET)
                }
            } else {
                Toast.makeText(context, "Title and Developer fields should not be empty!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.gameClose.setOnClickListener {
            dismiss()
        }
    }

    interface AddGameClickListener {
        fun onSaveGame(title: String, developer: String, genre: String, titleET: TextInputEditText,
                       developerET: TextInputEditText, genreET: TextInputEditText)

        fun onUpdateGame(gameData: GameData, titleET: TextInputEditText,
                         developerET: TextInputEditText, genreET: TextInputEditText)
    }
}