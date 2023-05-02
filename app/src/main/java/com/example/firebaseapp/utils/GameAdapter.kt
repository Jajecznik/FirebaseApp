package com.example.firebaseapp.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseapp.databinding.GameItemBinding

class GameAdapter(private val list: MutableList<GameData>):
    RecyclerView.Adapter<GameAdapter.GameItemViewHolder>() {

    private var listener: GameAdapterClickInterface? = null

    fun setListener(listener: GameAdapterClickInterface) {
        this.listener = listener
    }

    inner class GameItemViewHolder(val binding: GameItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameAdapter.GameItemViewHolder {
        val binding = GameItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: GameAdapter.GameItemViewHolder, position: Int) {
        with(holder) {
            with(list[position]) {
                binding.gameTitle.text = this.title
                binding.gameDeveloper.text = this.developer
                binding.gameGenre.text = this.genre

                binding.deleteGame.setOnClickListener {
                    listener?.onDeleteGameBtnClick(this)
                }

                binding.editGame.setOnClickListener {
                    listener?.onEditGameBtnClick(this)
                }
            }
        }
    }

    interface GameAdapterClickInterface {
        fun onDeleteGameBtnClick(gameData: GameData)
        fun onEditGameBtnClick(gameData: GameData)
    }
}