package com.example.pdm_2021_ii_p3_proyecto3

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private val titles = arrayOf("Abogados","Audiencia","CAI","Casos","Caso Empleado","Clientes",
                                 "Cobros","Expedientes","Indicio","Juzgado","Precio Historico","Servicios","Sucursales")

    private val details = arrayOf("Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6","Item 7",
                                    "Item 8", "Item 9","Item 10","Item 11","Item 12","Item 13" )

    private val images = intArrayOf(
        R.drawable.lawyer_24px,
        R.drawable.audiencia,
        R.drawable.cai,
        R.drawable.casos,
        R.drawable.caso_emp,
        R.drawable.clientes,
        R.drawable.cobros,
        R.drawable.expediente,
        R.drawable.indicio,
        R.drawable.juzgado,
        R.drawable.precio,
        R.drawable.servicios,
        R.drawable.sucursales)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_layout,viewGroup, false)
        return ViewHolder(v)
    }
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView
        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detail)

            itemView.setOnClickListener { v: View ->
                var position: Int = getAdapterPosition()
                Snackbar.make(v,"Click en el item $position",
                    Snackbar.LENGTH_LONG).setAction("Action",null).show()
            }
        }
    }
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemTitle.text = titles[position]
        viewHolder.itemDetail.text = details[position]
        viewHolder.itemImage.setImageResource(images[position])
    }

    override fun getItemCount(): Int {
        return titles.size
    }

}

