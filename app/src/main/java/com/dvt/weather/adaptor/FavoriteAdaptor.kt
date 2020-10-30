package com.dvt.weather.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weather.R
import com.dvt.weather.database.FavoriteModel
import com.dvt.weather.database.Forecast
import com.dvt.weather.database.RecyclerViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class FavoriteAdaptor(private val items: MutableList<FavoriteModel>) : RecyclerView.Adapter<FavoriteAdaptor.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.favorite_row, parent, false)
        return ViewHolder(view)
    }

    fun setListData(data: List<FavoriteModel>) {
        this.items.addAll(data)
        notifyDataSetChanged()
    }
    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(items[position])

    }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textViewTemp: TextView = itemView.findViewById(R.id.textViewTemperature)
        val textViewPlace: TextView = itemView.findViewById(R.id.textViewPlace)


        fun bind(data: FavoriteModel) {

            textViewPlace.text =("${data.place}")
            //Round to 1 dec
            val roundTemp = Math.round(data.temperature)
            textViewTemp.text = "$roundTemp °"//data.weatherIcon



        }
    }
}