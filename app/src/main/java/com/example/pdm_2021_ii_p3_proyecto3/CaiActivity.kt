package com.example.pdm_2021_ii_p3_proyecto3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.CaiDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.RestApiError
import com.example.pdm_2021_ii_p3_proyecto3.Service.CaiService

import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_audiencia_detalle.*
import kotlinx.android.synthetic.main.activity_cai.*
import kotlinx.android.synthetic.main.activity_cai.view.*
import kotlinx.android.synthetic.main.activity_cliente.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CaiActivity : AppCompatActivity() {
    var array = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cai)
        callServiceGetCais()
        btnGuardarCAI.setOnClickListener { v-> callServicePostCai() }
        btnActualizarCAI.setOnClickListener { v -> actualizarCai(v) }
        btnBorrarCAI.setOnClickListener { v-> borrarCai(v) }
    }

    private fun callServiceGetCais() {
        val caiService: CaiService = RestEngine.buildService().create(CaiService::class.java)
        var result: Call<List<CaiDataCollectionItem>> = caiService.listCai()

        result.enqueue(object : Callback<List<CaiDataCollectionItem>> {
            override fun onFailure(call: Call<List<CaiDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@CaiActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<CaiDataCollectionItem>>,
                response: Response<List<CaiDataCollectionItem>>
            ) {
                array.add("Todos los cai")
                array.add("Id cai|Cai|Rango inicial|Rango final|Fecha limite")
                for(i in 0..(response.body()!!.size-1)){
                    array.add(response.body()!!.get(i).idcai.toString() + "|" + response.body()!!.get(i).cai + "|"
                            + response.body()!!.get(i).rangoinicial + "|" + response.body()!!.get(i).rangofinal + "|"
                            + response.body()!!.get(i).fechalimite )
                    val arrayAdapter: ArrayAdapter<*>
                    arrayAdapter = ArrayAdapter(this@CaiActivity,android.R.layout.simple_list_item_1,array)
                    lvwCai.adapter = arrayAdapter
                }

            }
        })
    }

    private fun callServicePostCai() {

        val caiInfo = CaiDataCollectionItem(
            idcai = 0,
            cai = txtCai.text.toString(),
            rangoinicial = txtRangoInicial.text.toString(),
            rangofinal = txtRangoFinal.text.toString(),
            fechalimite = txtFechaLimite.text.toString()

        )
        addCai(caiInfo) {
            if (it?.idcai != null) {
                callServiceGetCais()
                Toast.makeText(this,"OK", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this,"Error", Toast.LENGTH_LONG).show()
            }
        }
    }
    fun addCai(caiData: CaiDataCollectionItem, onResult: (CaiDataCollectionItem?) -> Unit){
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val retrofit = RestEngine.buildService().create(CaiService::class.java)
        var result: Call<CaiDataCollectionItem> = retrofit.addCai(caiData)
        result.enqueue(object : Callback<CaiDataCollectionItem> {
            override fun onFailure(call: Call<CaiDataCollectionItem>, t: Throwable) {
                onResult(null)
            }

            override fun onResponse(call: Call<CaiDataCollectionItem>,
                                    response: Response<CaiDataCollectionItem>
            ) {
                if (response.isSuccessful) {
                    val addedCai = response.body()!!
                    onResult(addedCai)
                }
                /*else if (response.code() == 401){
                    Toast.makeText(this@MainActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }*/
                else if (response.code() == 500){
                    //val gson = Gson()
                    //val type = object : TypeToken<RestApiError>() {}.type
                    //var errorResponse1: RestApiError? = gson.fromJson(response.errorBody()!!.charStream(), type)
                    val errorResponse = Gson().fromJson(response.errorBody()!!.string()!!, RestApiError::class.java)


                    Toast.makeText(this@CaiActivity,errorResponse.errorDetails,Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CaiActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        }
        )
    }

    private fun estaVacio():Boolean{
        /*if(txtIdCAI.text.toString().isEmpty()) {
            txtIdCAI.error ="Debe rellenar el id del cai"
            return true
        }else if(txtCAI.text.toString().isEmpty()){
            txtCAI.error = "Debe rellenar el cai"
            return true
        }*/
        if(txtRangoInicial.text.toString().isEmpty()) {
            txtRangoInicial.error ="Debe rellenar el rango inicial"
            return true
        }
        if(txtRangoFinal.text.toString().isEmpty()) {
            txtRangoFinal.error ="Debe rellenar el rango final"
            return true
        }
        if(txtFechaLimite.text.toString().isEmpty()) {
            txtFechaLimite.error ="Debe rellenar la fecha limite"
            return true
        }

        return false
    }
//
    private fun noLoSuficienteLargo():Boolean{
        if(txtIdCai.text.toString().length != 15) {
            txtIdCai.error ="El id cai no puede ser distinto a 13 dígitos, no olvide ingresar los guiones"
            return true
        }else if(txtCai.text.toString().length < 3){
            txtCai.error = "El cai no puede ser  menor a 3 caracteres"
            return true
        }
        if(txtRangoInicial.text.toString().length <3) {
            txtRangoInicial.error ="El rango inicial no puede ser menor a 3 caracteres "
            return true
        }
        if(txtRangoFinal.text.toString().length <3) {
            txtRangoFinal.error ="El rango final no puede ser menor a 3 caracteres "
            return true
        }
        if(txtFechaLimite.text.toString().length <4) {
            txtFechaLimite.error ="La fecha limite no puede ser menor a 4 digitos"
            return true
        }

        return false
    }
    fun actualizarCai(view: View) {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del cai")
        builder.setMessage("Por favor ingrese el ID del cai a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_cai, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServicePutCai(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun callServicePutCai(idCai:Long) {

        val caiInfo = CaiDataCollectionItem(
            idcai = 0,
            cai = txtCai.text.toString(),
            rangoinicial = txtRangoInicial.text.toString(),
            rangofinal = txtRangoFinal.text.toString(),
            fechalimite = txtFechaLimite.text.toString()

        )

        val retrofit = RestEngine.buildService().create(CaiService::class.java)
        var result: Call<CaiDataCollectionItem> = retrofit.updateCai(caiInfo)
        callServiceGetCais()
        result.enqueue(object : Callback<CaiDataCollectionItem> {
            override fun onFailure(call: Call<CaiDataCollectionItem>, t: Throwable) {

            }

            override fun onResponse(call: Call<CaiDataCollectionItem>,
                                    response: Response<CaiDataCollectionItem>) {
                if (response.isSuccessful) {
                    callServiceGetCais()
                    val updatedPerson = response.body()!!
                    Toast.makeText(this@CaiActivity,"OK"+response.body()!!.cai,Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@CaiActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CaiActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    fun borrarCai(view:View){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Ingrese el Id del cai")
        builder.setMessage("Por favor ingrese el ID del cai a buscar, en caso de querer verificar nuevamente presione el boton \"Cancelar\"")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_cai, null)
        val editText  = dialogLayout.findViewById<EditText>(R.id.editText)
        builder.setView(dialogLayout)
        builder.setPositiveButton("Enviar") { dialogInterface, i ->
            if(editText.text.toString() == ""){
                Toast.makeText(this, "No puede dejar el ID vacío", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }
            callServiceDeleteCai(editText.text.toString().toLong())
        }
        builder.setNegativeButton("Cancelar"){dialogInterface, i -> return@setNegativeButton}
        builder.show()
    }

    private fun callServiceDeleteCai(idCai: Long) {
        val caiService:CaiService = RestEngine.buildService().create(CaiService::class.java)
        var result: Call<ResponseBody> = caiService.deleteCai(idCai)

        result.enqueue(object :  Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@CaiActivity,"Error",Toast.LENGTH_LONG).show()
            }
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(this@CaiActivity,"DELETE",Toast.LENGTH_LONG).show()
                }
                else if (response.code() == 401){
                    Toast.makeText(this@CaiActivity,"Sesion expirada",Toast.LENGTH_LONG).show()
                }
                else{
                    Toast.makeText(this@CaiActivity,"Fallo al traer el item",Toast.LENGTH_LONG).show()
                }
            }
        })
    }

}