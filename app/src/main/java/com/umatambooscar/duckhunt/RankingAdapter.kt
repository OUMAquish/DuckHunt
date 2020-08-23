package com.umatambooscar.duckhunt

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class RankingAdapter (private val context: Activity, private val usuarios: ArrayList<User>)   : BaseAdapter() {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.ranking_list, null, true)

        val textViewPosition = rowView.findViewById<TextView>(R.id.textViewPosition)
        val textViewDucks = rowView.findViewById<TextView>(R.id.textViewDucks)
        val textViewNick = rowView.findViewById<TextView>(R.id.textViewNick)
        val posicion = position + 1
        textViewPosition.text = posicion.toString()
        textViewDucks.text = "${usuarios[position].ducks}"
        textViewNick.text = "${usuarios[position].nick}"
        return rowView
    }

    override fun getItem(position: Int): Any? {
        return usuarios.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return usuarios.size
    }
}
