package com.sbaygildin.pushwords.wordlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sbaygildin.pushwords.data.model.WordTranslation


class WordlistAdapter(
    private val onEditClick: (WordTranslation) -> Unit,
    private val onDeleteClick: (WordTranslation) -> Unit
) : RecyclerView.Adapter<WordlistAdapter.WordViewHolder>() {

    private var wordList: List<WordTranslation> = listOf()

    fun setWords(words: List<WordTranslation>) {
        this.wordList = words
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word_translation, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val currentWord = wordList[position]
        holder.bind(currentWord)
    }

    override fun getItemCount() = wordList.size

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val originalWordTextView: TextView = itemView.findViewById(R.id.originalWordTextView)
        private val translatedWordTextView: TextView = itemView.findViewById(R.id.translatedWordTextView)
        private val editButton: ImageButton = itemView.findViewById(R.id.editButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(wordTranslation: WordTranslation) {
            originalWordTextView.text = wordTranslation.originalWord
            translatedWordTextView.text = wordTranslation.translatedWord


            Log.d("navProblem", "Binding word: ${wordTranslation.originalWord}")

            editButton.setOnClickListener {
                Log.d("navProblem", "Edit clicked for: ${wordTranslation.originalWord}")
                onEditClick(wordTranslation)
            }

            deleteButton.setOnClickListener {
                Log.d("WordlistAdapter", "Delete clicked for: ${wordTranslation.originalWord}")
                onDeleteClick(wordTranslation)
            }


        }
    }
}

