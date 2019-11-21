package com.example.tp5


import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.personne_ajout.*
import kotlinx.android.synthetic.main.style_dune_ligne.*
import org.jetbrains.anko.toast
import java.net.URL
import android.widget.LinearLayout as LinearLayout1


class AjoutPersonne : AppCompatActivity() {
    val mReceiverLOW=BatterieLow()

    companion object {
        const val EXTRA_VALIDER = "AjouterPersonne.valider"
        const val EXTRA_NOM = "AjouterPersonne.nom"
        const val EXTRA_EMAIL = "AjouterPersonne.email"
        const val EXTRA_TEL = "AjouterPersonne.tel"
        const val EXTRA_FAXE = "AjouterPersonne.fixe"
        const val EXTRA_BATERIE = "AjouterPersonne.Labaterie"
    }

    var IMAGE:Bitmap?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.personne_ajout)

        this.registerReceiver(mReceiverLOW,  IntentFilter(Intent.ACTION_BATTERY_CHANGED))


        //le bouton ajouter on recup les informations saisies et on les passe en paaramétre a l'intent
        bt_ajouter.setOnClickListener {
            if ((ed_nom.text.toString()=="")&&(ed_tel.text.toString()=="")){
                Toast.makeText(this, "vous devez saisir au moins un nom et un numero de telephone", Toast.LENGTH_LONG).show()

            }
            else{


                var laBaterieEstFaible=mReceiverLOW.valeur

                val intent = Intent()
                // On a valider (=> true)
                intent.putExtra(EXTRA_VALIDER, true)
                // On donne la valeur tapée en retour
                intent.putExtra(EXTRA_NOM,ed_nom.text.toString() )
                intent.putExtra(EXTRA_EMAIL,ed_email.text.toString() )
                intent.putExtra(EXTRA_TEL,ed_tel.text.toString() )
                intent.putExtra(EXTRA_FAXE,ed_fixe.text.toString() )

                intent.putExtra(EXTRA_BATERIE,laBaterieEstFaible)


                setResult(RESULT_OK, intent)
                finish()
            }

        }

        //mettre les champs a vide
        bt_annuler.setOnClickListener {

            ed_nom.setText("")
            ed_email.setText("")
            ed_tel.setText("")
            ed_fixe.setText("")
        }


    }

    /****
     * lorsque on quite l'application
     * on se désabonne au Receiver
     * on se désabonne
     *
     *
     * **/


    override fun onDestroy() {
        // unregisterReceiver(mReceiverOK);
        unregisterReceiver(mReceiverLOW);
        super.onDestroy()
    }

}
