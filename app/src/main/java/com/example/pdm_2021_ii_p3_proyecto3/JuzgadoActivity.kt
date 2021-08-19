package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.JuzgadoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.ServicioDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.Service.JuzgadoService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.example.pdm_2021_ii_p3_proyecto3.Service.ServicioService
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_juzgado.*
import kotlinx.android.synthetic.main.activity_servicio.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class JuzgadoActivity : AppCompatActivity() {
    var array = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_juzgado)
        btnGuardarJuzgado.setOnClickListener { v-> callServicePostJuzgado() }
        btnActualizarJuzgado.setOnClickListener {v -> actualizarJuzgado(v)  }
        btnEliminarJuzgado.setOnClickListener { v-> borrarJuzgado(v) }
        callServiceGetJuzgado()
    }
    private fun callServiceGetJuzgado() {
        val personService: JuzgadoService = RestEngine.buildService().create(JuzgadoService::class.java)
        var result: Call<List<JuzgadoDataCollectionItem>> = personService.listJuzgado()

        result.enqueue(object : Callback<List<JuzgadoDataCollectionItem>> {
            override fun onFailure(call: Call<List<JuzgadoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@JuzgadoActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<JuzgadoDataCollectionItem>>,
                response: Response<List<JuzgadoDataCollectionItem>>
            ) {
                array.add("Todos los Juzgados")
                array.add("ID|Juzgado|Dirección")
                for(i in 0..(response.body()!!.size-1)){
                    array.add(response.body()!!.get(i).idjuzgado.toString() + "|" + response.body()!!.get(i).nombrejuzgado + "|" + response.body()!!.get(i).direccionjuzgado)

                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@JuzgadoActivity,android.R.layout.simple_list_item_1,array)
                    lvwJuzgado.adapter = arrayAdapter
                }

            }
        })
    }


    fun actualizarJuzgado(view: View) {
        /*     if(estaVacio()){
                 return
             }
             if(noLoSuficienteLargo()){
                 return
             }*/
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del servicio")
        builder.setMessage("Por favor ingrese el ID del juzgado a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_juzgado, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutJuzgado(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()

    }

    private fun callServicePutJuzgado(idJuzgado:Long) {

        val juzgadoInfo = JuzgadoDataCollectionItem(
            idjuzgado = idJuzgado,
            nombrejuzgado = txtNombreJuzgado.text.toString(),
            direccionjuzgado = txtDireccionJuzgado.text.toString()
        )

        val retrofit = RestEngine.buildService().create(JuzgadoService::class.java)
        var result: Call<JuzgadoDataCollectionItem> = retrofit.updateJuzgado(juzgadoInfo)
        result.enqueue(object : Callback<JuzgadoDataCollectionItem> {
            override fun onFailure(call: Call<JuzgadoDataCollectionItem>, t: Throwable) {
                Toast.makeText(this@JuzgadoActivity,"Error", Toast.LENGTH_LONG).show()
            }
            override fun onResponse(call: Call<JuzgadoDataCollectionItem>,
                                    response: Response<JuzgadoDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@JuzgadoActivity,"OK"+response.body()!!.nombrejuzgado,
                        Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@JuzgadoActivity,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@JuzgadoActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun callServicePostJuzgado() {

        val  juzgadoInfo = JuzgadoDataCollectionItem(
            idjuzgado = 0, // Este se llena solo para no dejar vacio. Es incremental
            nombrejuzgado = txtNombreJuzgado.text.toString(),
            direccionjuzgado = txtDireccionJuzgado.text.toString(),
            )
        addJuzgado(juzgadoInfo) {
            if (it?.idjuzgado != null) {
                Toast.makeText(this@JuzgadoActivity,"OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@JuzgadoActivity,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }




    fun addJuzgado(juzgadoData: JuzgadoDataCollectionItem, onResult: (JuzgadoDataCollectionItem?) -> Unit){
        val retrofit = RestEngine.buildService().create(JuzgadoService::class.java)
        var result: Call<JuzgadoDataCollectionItem> = retrofit.addJuzgado(juzgadoData)
        result.enqueue(object : Callback<JuzgadoDataCollectionItem> {
            override fun onFailure(call: Call<JuzgadoDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<JuzgadoDataCollectionItem>,
                                    response: Response<JuzgadoDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedPerson = response.body()!!
                    onResult(addedPerson)
                }
                /*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*/
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(this@JuzgadoActivity,errorResponse.errorDetails, Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@JuzgadoActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }

        }
        )

    }
    fun borrarJuzgado(view: View){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del Juzgado")
        builder.setMessage("Por favor ingrese el ID del Juzgado a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_juzgado, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeleteJuzgado(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()

    }

    private fun callServiceDeleteJuzgado(idJuzgado: Long) {
        val juzgadoService: JuzgadoService = RestEngine.buildService().create(JuzgadoService::class.java)
        var result: Call<ResponseBody> = juzgadoService.deleteJuzgado(idJuzgado)

        result.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@JuzgadoActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@JuzgadoActivity,"DELETE", Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@JuzgadoActivity,"Sesion expirada", Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@JuzgadoActivity,"Fallo al traer el item", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun estaVacio():Boolean{
        if(txtNombreJuzgado.text.toString().isEmpty()) {
            txtNombreJuzgado.error ="Debe rellenar el nombre del juzgado "
            return true
        }else if(txtDireccionJuzgado.text.toString().isEmpty()){
            txtDireccionJuzgado.error = "Debe rellenar la dirección"
            return true
        }


        return false
    }

    private fun noLoSuficienteLargo():Boolean{
        if(txtNombreJuzgado.text.toString().length < 4) {
            txtNombreJuzgado.error ="El nombre no puede ser menor a 4 caracteres"
            return true
        }else if(txtDireccionJuzgado.text.toString().length < 6){
            txtDireccionJuzgado.error = "La dirrección no puede ser menor a 6 caracteres"
            return true
        }

        return false
    }
}