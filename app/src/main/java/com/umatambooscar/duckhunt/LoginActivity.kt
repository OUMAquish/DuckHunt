package com.umatambooscar.duckhunt

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val db = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Cambiar tipo de fuente
        val customTypeface = resources.getFont(R.font.pixel)
        editTextNick.typeface = customTypeface
        buttonStart.typeface = customTypeface

        //Reproducción de sonido
        MediaPlayer.create(this, R.raw.title_screen).start()

        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder().build()
//        adViewLogin.adSize = AdSize.BANNER
//        adViewLogin.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        adViewLogin.loadAd(adRequest)

        //Eventos clic
        buttonStart.setOnClickListener {
            val nick = editTextNick.getText().toString()
            if (nick.isEmpty()) {
                editTextNick.setError("El nombre de usuario es obligatorio")
            } else if (nick.length < 3) {
                editTextNick.setError("Debe tener al menos 3 caracteres")
            } else {
                ProcesarUsuarioFirestore(nick)
            }

        }

        buttonHallDeFama.setOnClickListener {
            val i = Intent(this, HallDeFamaActivity::class.java)
            startActivity(i)
        }


    }

    fun ProcesarUsuarioFirestore(nick:String){
        val db = FirebaseFirestore.getInstance()
        lateinit var  idDocumentReference: String
        db.collection("users")
            .whereEqualTo("nick",nick)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val documents = it.getResult()
                    if (documents != null) {
                        if (documents.documents.size == 0) {
                            // Add a new document with a generated ID
                            db.collection("users")
                                .add(User(nick, 0))
                                .addOnSuccessListener { documentReference ->
                                    idDocumentReference = documentReference.id
                                    iniciarJuego(nick,idDocumentReference)
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Error al leer la base de datos",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                        } else {
                            idDocumentReference = documents.documents[0].id
                            iniciarJuego(nick,idDocumentReference)
                        }

                    }
                }
            }
    }

    fun iniciarJuego(nick: String, id: String){
        val i = Intent(this@LoginActivity, GameActivity::class.java)
        i.putExtra(EXTRA_NICK, nick)
        i.putExtra(EXTRA_ID, id)
        startActivity(i)
    }

}