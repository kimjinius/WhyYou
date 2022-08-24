package com.example.whyyou

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.app_listview.view.*

class AppAdapter(private val context: Context) : RecyclerView.Adapter<AppAdapter.ViewHolder>(){

    private var datas = mutableListOf<AppData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_listview, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val appTitle = itemView.appTitle
        private val appDate = itemView.appDate

        fun bind(item: AppData) {
            appTitle.text = item.title
            appDate.text = item.date

            itemView.setOnClickListener {
                val intent = Intent(context,AppDetail::class.java)
                intent.putExtra("friend_name", item.friendName)
                intent.putExtra("app_date", item.date)
                intent.putExtra("app_time", item.time)
                intent.putExtra("app_title", item.title)
                intent.putExtra("app_location", item.location)

                context.startActivity(intent)
            }

        }
    }

    fun replaceList(newList: MutableList<AppData>) {
        datas = newList.toMutableList()
        notifyDataSetChanged()
    }
}