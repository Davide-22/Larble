package com.example.larble

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
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
        if(itemsViewModel.profile_picture!= null){
            val decodeImage: ByteArray = Base64.decode(itemsViewModel.profile_picture, Base64.DEFAULT)
            val bitmap: Bitmap = BitmapFactory.decodeByteArray(decodeImage, 0, decodeImage.size)
            holder.photo.setImageBitmap(bitmap)
        }

        holder.username.text = itemsViewModel.username
        holder.wins.text = itemsViewModel.wins
        holder.score.text = itemsViewModel.score

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val photo: ImageView = itemView.findViewById(R.id.photo)
        val username: TextView = itemView.findViewById(R.id.username)
        val wins: TextView = itemView.findViewById(R.id.wins)
        val score: TextView = itemView.findViewById(R.id.score)
    }
}