/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.dvt.weather.adaptor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.dvt.weather.R
import com.dvt.weather.database.Forecast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class WeatherAdapter( private val items: MutableList<Forecast>) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {

//var items = LiveData<Forecast>()
 //private val mTask: LiveData<List<Forecast>> = ArrayList<Forecast>()



  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.weather_row, parent, false)
    return ViewHolder(view)
  }
  fun setListData(data: List<Forecast>) {
    this.items.addAll(data)
    notifyDataSetChanged()
  }
  override fun getItemCount() = items.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    holder.bind(items[position])
    holder.imageViewPlayer.setImageResource(items[position].weatherIcon)

  }
  public fun getConvertedDay(date: String): String {

    return SimpleDateFormat(
      "EEEE",
      Locale.ENGLISH
    ).format(SimpleDateFormat("YYYY-MM-DD HH:MM:ss").parse(date))
  }
  class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val imageViewPlayer: ImageView = itemView.findViewById(R.id.imageViewPlayer)
    val textViewPlayerName: TextView = itemView.findViewById(R.id.textViewPlace)
    val textViewPlayerCountry: TextView = itemView.findViewById(R.id.textViewTemperature)



      fun bind(data: Forecast){

        try {
         val forecastDay= SimpleDateFormat(
           "EEEE",
           Locale.ENGLISH
         ).format(SimpleDateFormat("yyyy-MM-dd ").parse(data.forecastDate))
          textViewPlayerName.text =  forecastDay
        } catch (e: ParseException) {
          e.printStackTrace()
        }


//Round to 1 dec
          //var temp_value:String = data.temperature_max
          //val temperatureValue:Double = temp_value.toDouble()
          val roundTemp = Math.round(data.temperature)
          textViewPlayerCountry.text = "$roundTemp °"//data.weatherIcon


      }
  }
}