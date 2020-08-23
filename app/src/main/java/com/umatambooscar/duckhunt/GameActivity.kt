package com.umatambooscar.duckhunt

import android.app.AlertDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*

class GameActivity : AppCompatActivity() {
    var contador = 0
    var anchoPantalla = 0
    var altoPantalla = 0
    lateinit var aleatorio: Random
    var gameOver = false
    lateinit var idDocumentNick: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        textViewNick.setText(intent.getStringExtra(EXTRA_NICK))
        idDocumentNick = intent.getStringExtra(EXTRA_ID).toString()

        inicializarPantalla()

        inicializarCuentaRegresiva()

        imageViewDuck.setOnClickListener {
            if (gameOver) return@setOnClickListener
            contador++
            MediaPlayer.create(this, R.raw.gunshot).start()
            textViewCounter.setText(contador.toString())
            imageViewDuck.setImageResource(R.drawable.duck_clicked)

            //https://developer.android.com/reference/android/os/Handler

            Handler().postDelayed(Runnable {
                imageViewDuck.setImageResource(R.drawable.duck)
                moverPato()
            }, 50)

        }

    }

    private fun inicializarPantalla() {
        // 1. Obtenemos el tamaño de la pantalla del dispositivo
        val display = this.resources.displayMetrics
        anchoPantalla = display.widthPixels
        altoPantalla = display.heightPixels

        // 2. Inicializamos el objeto para generar números aleatorios
        aleatorio = Random()
    }

    private fun moverPato() {
        val min = 0
        val maximoX: Int = anchoPantalla - imageViewDuck.getWidth()
        val maximoY: Int = altoPantalla - imageViewDuck.getHeight()

        // Generamos 2 números aleatorios, para la coordenadas x , y
        val randomX: Int = aleatorio.nextInt(maximoX - min + 1 + min)
        val randomY: Int = aleatorio.nextInt(maximoY - min + 1 + min)

        // Utilizamos los números aleatorios para mover el pato a esa nueva posición
        imageViewDuck.setX(randomX.toFloat())
        imageViewDuck.setY(randomY.toFloat())
    }

    private fun inicializarCuentaRegresiva() {
        //https://developer.android.com/reference/kotlin/android/os/CountDownTimer
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val segundosRestantes = millisUntilFinished / 1000
                textViewTimer.setText("${segundosRestantes}s")
            }

            override fun onFinish() {
                textViewTimer.setText("0s")
                gameOver = true
                mostrarDialogoGameOver()
                guardarResultadosEnFirestore()
            }
        }.start()
    }

    private fun guardarResultadosEnFirestore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(idDocumentNick)
            .update("ducks",contador)
    }

    private fun mostrarDialogoGameOver() {
        //https://developer.android.com/guide/topics/ui/dialogs
        val builder = AlertDialog.Builder(this)
        builder
            .setMessage("Has conseguido cazar $contador patos")
            .setTitle("Game over")
            .setPositiveButton("Reinicio",
                DialogInterface.OnClickListener { dialog, id ->
                    contador = 0
                    gameOver = false
                    textViewCounter.setText(contador.toString())
                    moverPato()
                    inicializarCuentaRegresiva()
                })
            .setNegativeButton("Salir",
                DialogInterface.OnClickListener { dialog, id ->
                    dialog.dismiss()
                    finish()
                })
        builder.create().show()
    }

}