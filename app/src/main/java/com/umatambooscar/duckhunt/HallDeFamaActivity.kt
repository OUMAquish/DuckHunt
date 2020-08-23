package com.umatambooscar.duckhunt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_hall_de_fama.*

class HallDeFamaActivity : AppCompatActivity() {
    var listaUsuarios = arrayListOf<User>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hall_de_fama)
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .orderBy("ducks", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                for (documento in documents) {
                    val userRanking = documento.toObject(User::class.java)
                    listaUsuarios.add(userRanking)
                }

                val inflater = this.layoutInflater
                val rowHeaderView = inflater.inflate(R.layout.ranking_list_header, null, false)
                listViewRanking.addHeaderView(rowHeaderView)

                var rankingUsuarioAdaptador = RankingAdapter(this,listaUsuarios)
                listViewRanking.adapter = rankingUsuarioAdaptador
            }
    }
}
