package com.example.larble

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private val mList: List<ItemsLeaderboard>) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_leaderboard, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val itemsViewModel = mList[position]

        holder.imageView.setImageResource(itemsViewModel.image)

        holder.username.text = itemsViewModel.username
        holder.wins.text = itemsViewModel.wins
        holder.score.text = itemsViewModel.score

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageview)
        val username: TextView = itemView.findViewById(R.id.username)
        val wins: TextView = itemView.findViewById(R.id.wins)
        val score: TextView = itemView.findViewById(R.id.score)
    }
}