package com.example.pdm_2021_ii_p3_proyecto3

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pdm_2021_ii_p3_proyecto3.DataCollection.EmpleadoDataCollectionItem
import com.example.pdm_2021_ii_p3_proyecto3.Service.EmpleadoService
import com.example.pdm_2021_ii_p3_proyecto3.Service.RestEngine
import kotlinx.android.synthetic.main.activity_abogado.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.content_main.*
import org.apache.commons.codec.digest.DigestUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity() : AppCompatActivity() {
    var array = ArrayList<String>()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<RecyclerAdapter.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            // setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(findViewById(R.id.toolbar))
        collapsing_toollbar.title = "CENTAURO ABOGADOS"
        collapsing_toollbar.setContentScrimColor(Color.WHITE)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = RecyclerAdapter()
        recyclerView.adapter = adapter

        button.setOnClickListener {
            callServiceGetEmpleados()
        }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId)
        {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun iniciar(idEmpleado:Long){
        val intent = Intent(this,PrincipalActivity::class.java)
        intent.putExtra("idEmpleado",idEmpleado)
        startActivity(intent)
    }

    private fun estaVacio():Boolean{
        if(txtUsuario.text.toString().isEmpty()) {
            txtUsuario.error ="Debe rellenar el usuario del empleado"
            return true
        }
        if(txtContraseña.text.toString().isEmpty()) {
            txtContraseña.error ="Debe rellenar la contraseña del empleado"
            return true
        }
        return false
    }

    private fun noLoSuficienteLargo():Boolean{
        if(txtUsuario.text.toString().length < 8) {
            txtUsuario.error ="El nombre de usuario no puede ser menor a 8 dígitos"
            return true
        }
        if(txtContraseña.text.toString().length < 8) {
            txtContraseña.error ="La contraseña no puede ser menor a 8 dígitos"
            return true
        }
        return false
    }

    private fun callServiceGetEmpleados() {
        if(estaVacio()){
            return
        }
        if(noLoSuficienteLargo()){
            return
        }
        val personService: EmpleadoService = RestEngine.buildService().create(EmpleadoService::class.java)
        var result: Call<List<EmpleadoDataCollectionItem>> = personService.listEmpleados()
        val nombreUsuario = txtUsuario.text.toString()
        val contraseña = txtContraseña.text.toString()
        val claveEncriptada = DigestUtils.md5Hex(contraseña)

        result.enqueue(object : Callback<List<EmpleadoDataCollectionItem>> {
            override fun onFailure(call: Call<List<EmpleadoDataCollectionItem>>, t: Throwable) {
                Toast.makeText(this@MainActivity,"Error", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(
                call: Call<List<EmpleadoDataCollectionItem>>,
                response: Response<List<EmpleadoDataCollectionItem>>
            ) {
                for(i in 0..(response.body()!!.size-1)){
                    array.add(response.body()!!.get(i).nombreusuario + "|" + response.body()!!.get(i).claveusuario + "|" + response.body()!!.get(i).idempleado)
                }
                for(i in 0..(array.size-1)){
                    val lista = array.get(i).split("|")
                    val nombreUsuarioFinal = lista[0]
                    val contraseñaFinal = lista[1]
                    val idEmpleado = lista[2]
                    if(nombreUsuarioFinal.equals(nombreUsuario) && contraseñaFinal.equals(claveEncriptada)){
                        iniciar(idEmpleado.toLong())
                        return
                    }
                }
                Toast.makeText(this@MainActivity,"No se encuentra el usuario", Toast.LENGTH_LONG).show()

            }
        })
    }

}