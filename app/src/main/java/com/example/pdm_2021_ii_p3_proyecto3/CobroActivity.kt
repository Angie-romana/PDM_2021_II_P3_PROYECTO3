package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ClienteDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.PrecioHistoricoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ServicioDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.Service.ClienteService
import com.example.pdm_2021_ii_p3_proyecto3.Service.PrecioHistoricoService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.example.pdm_2021_ii_p3_proyecto3.Service.ServicioService
import kotlinx.android.synthetic.main.activity_cobro.*
import kotlinx.android.synthetic.main.activity_expediente.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class CobroActivity : AppCompatActivity() {
    var array = ArrayList<String>()
    var idCliente = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cobro)
        llenarSpinner()
        llenarListView()
    }

    private fun llenarSpinner(){
        val arrayAdapter: ArrayAdapter<*>
        idCliente.add("Seleccione el cliente")

        val casosService: ClienteService = RestEngine.buildService().create(ClienteService::class.java)
        var result: Call<List<ClienteDataCollectionItem>> = casosService.listClientes()

        result.enqueue(object : Callback<List<ClienteDataCollectionItem>> {
            override fun onFailure(call: Call<List<ClienteDataCollectionItem>>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<List<ClienteDataCollectionItem>>,
                response: Response<List<ClienteDataCollectionItem>>
            ) {
                for(i in 0..(response.body()!!.size-1)){
                    idCliente.add(response.body()!!.get(i).idcliente.toString() + ". " + response.body()!!.get(i).nombrecliente)
                }

            }
        })
        arrayAdapter = ArrayAdapter(this,android.R.layout.simple_list_item_1,idCliente)
        spiCliente.adapter = arrayAdapter
    }

    private fun llenarListView(){
        val servicioService: ServicioService = RestEngine.buildService().create(
            ServicioService::class.java)
        var result: Call<List<ServicioDataCollectionItem>> = servicioService.listServicio()
        var arrayAdapter: ArrayAdapter<*>
        result.enqueue(object : Callback<List<ServicioDataCollectionItem>> {
            override fun onFailure(call: Call<List<ServicioDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@CobroActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<ServicioDataCollectionItem>>,
                response: Response<List<ServicioDataCollectionItem>>
            ) {
                arrayAdapter = ArrayAdapter(this@CobroActivity,android.R.layout.simple_list_item_1,array)
                lstServicios.adapter = arrayAdapter
                array.clear()
                for(i in 0..(response.body()!!.size-1)){
                    val precioHistoricoService: PrecioHistoricoService = RestEngine.buildService().create(
                        PrecioHistoricoService::class.java)
                    val idServicio = response.body()!!.get(i).idservicio.toString().toLong()
                    var result2: Call<PrecioHistoricoDataCollectionItem> = precioHistoricoService.getPrecioHistoricoByIdServicio(idServicio)
                    result2.enqueue(object : Callback<PrecioHistoricoDataCollectionItem>
                    {
                        override fun onResponse(
                            call: Call<PrecioHistoricoDataCollectionItem>,
                            response2: Response<PrecioHistoricoDataCollectionItem>
                        ) {
                            array.add(response.body()!!.get(i).idservicio.toString() + "." + response.body()!!.get(i).nombreservicio + "|" + response2.body()!!.precio)
                            arrayAdapter = ArrayAdapter(this@CobroActivity,android.R.layout.simple_list_item_1,array)
                            lstServicios.adapter = arrayAdapter
                        }
                        override fun onFailure(
                            call: Call<PrecioHistoricoDataCollectionItem>,
                            t: Throwable
                        ) {
                            TODO("Not yet implemented")
                        }

                    })

                }
        }
    })

    }

}