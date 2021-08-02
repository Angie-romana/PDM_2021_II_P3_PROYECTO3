package com.example.pdm_2021_ii_p3_proyecto3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_principal.*

class PrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)

        btnAbogado.setOnClickListener {  val intent = Intent(this,AbogadoActivity::class.java)
            startActivity(intent) }
        btnCliente.setOnClickListener {  val intent = Intent(this,ClienteActivity::class.java)
            startActivity(intent) }
        btnAudiencia.setOnClickListener {  val intent = Intent(this,AudienciaDetalleActivity::class.java)
            startActivity(intent) }
        btnCaso.setOnClickListener {  val intent = Intent(this,CasoActivity::class.java)
            startActivity(intent) }
        btnCasoEmpleado.setOnClickListener {  val intent = Intent(this,CasoEmpleadoActivity::class.java)
            startActivity(intent) }
        btnIndicio.setOnClickListener {  val intent = Intent(this,indicioActivity::class.java)
            startActivity(intent) }
        btnCobro.setOnClickListener {  val intent = Intent(this,CobroActivity::class.java)
            startActivity(intent) }
        btnJzgado.setOnClickListener {  val intent = Intent(this,JuzgadoActivity::class.java)
            startActivity(intent) }
        btnPrecioHistorico.setOnClickListener {  val intent = Intent(this,PrecioHistoricoActivity::class.java)
            startActivity(intent) }
        btnServicio.setOnClickListener {  val intent = Intent(this,ServicioActivity::class.java)
            startActivity(intent) }
        btnSucursal.setOnClickListener {  val intent = Intent(this,SucursalActivity::class.java)
            startActivity(intent) }
        btnExpediente.setOnClickListener {  val intent = Intent(this,ExpedienteActivity::class.java)
            startActivity(intent) }
    }
}