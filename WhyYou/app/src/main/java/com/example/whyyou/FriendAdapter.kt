package com.example.whyyou

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.friend_listview.view.*

class FriendAdapter(private val context: Context) : RecyclerView.Adapter<FriendAdapter.ViewHolder>(){

    private var datas = mutableListOf<FriendData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.friend_listview, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val friendName = itemView.friendName

        fun bind(item: FriendData) {
            friendName.text = item.name

            itemView.setOnClickListener {
                val intent = Intent(context,FriendAppAdd::class.java)
                intent.putExtra("friend_name", item.name)
                context.startActivity(intent)
            }
        }

    }

    fun replaceList(newList: MutableList<FriendData>) {
        datas = newList.toMutableList()
        notifyDataSetChanged()
    }
}