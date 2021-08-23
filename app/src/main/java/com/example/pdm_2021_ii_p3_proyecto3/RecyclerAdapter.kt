package com.example.pdm_2021_ii_p3_proyecto3

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class RecyclerAdapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    private val titles = arrayOf(
        "Abogados", "Audiencia", "CAI", "Casos", "Caso Empleado", "Clientes",
        "Cobros", "Expedientes", "Indicio", "Juzgado", "Precio Historico", "Servicios", "Sucursales"
    )

    private val details = arrayOf(
        "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "Item 6", "Item 7",
        "Item 8", "Item 9", "Item 10", "Item 11", "Item 12", "Item 13"
    )

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
        R.drawable.sucursales
    )

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.card_layout, viewGroup, false)
        return ViewHolder(v)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemImage: ImageView
        var itemTitle: TextView
        var itemDetail: TextView

        init {
            itemImage = itemView.findViewById(R.id.item_image)
            itemTitle = itemView.findViewById(R.id.item_title)
            itemDetail = itemView.findViewById(R.id.item_detail)

            itemView.setOnClickListener { v: View? ->
                var position: Int = getAdapterPosition()
                println(position.toString())

                itemView.setOnClickListener { v: View? ->
                    var position: Int = getAdapterPosition()
                    println(position.toString())
                    when (position) {
                        0 -> irAbogado(itemView)
                        1 -> irAudiencia(itemView)
                        2 -> irCai(itemView)
                        3 -> irCaso(itemView)
                        4 -> irCasoEmpleado(itemView)
                        5 -> irCliente(itemView)
                        6 -> irCobro(itemView)
                        7 -> irExpediente(itemView)
                        8 -> irIndicio(itemView)
                        9 -> irJuzgado(itemView)
                        10 -> irPrecioHistorico(itemView)
                        11 -> irServicio(itemView)
                        12 -> irSucursal(itemView)
                        else -> regresarMain(itemView)

                    }
                }
            }
        }
    }
        private fun regresarMain(itemView: View) {
            val intent = Intent(itemView.context, MainActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irAbogado(itemView: View) {
            val intent = Intent(itemView.context, AbogadoActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irAudiencia(itemView: View) {
            val intent = Intent(itemView.context, AudienciaDetalleActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irCai(itemView: View) {
            val intent = Intent(itemView.context, CaiActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irCaso(itemView: View) {
            val intent = Intent(itemView.context, CasoActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irCasoEmpleado(itemView: View) {
            val intent = Intent(itemView.context, CasoEmpleadoActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irCliente(itemView: View) {
            val intent = Intent(itemView.context, ClienteActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irCobro(itemView: View) {
            val intent = Intent(itemView.context, CobroActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irExpediente(itemView: View) {
            val intent = Intent(itemView.context, ExpedienteActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irIndicio(itemView: View) {
            val intent = Intent(itemView.context, IndicioActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irJuzgado(itemView: View) {
            val intent = Intent(itemView.context, JuzgadoActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irPrecioHistorico(itemView: View) {
            val intent = Intent(itemView.context, PrecioHistoricoActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irServicio(itemView: View) {
            val intent = Intent(itemView.context, ServicioActivity::class.java)
            itemView.context.startActivity(intent)
        }

        private fun irSucursal(itemView: View) {
            val intent = Intent(itemView.context, SucursalActivity::class.java)
            itemView.context.startActivity(intent)
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


