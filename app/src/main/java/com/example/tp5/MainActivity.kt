package com.example.tp5

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.os.BatteryManager
import android.os.Environment
import android.util.Log
import android.widget.ImageView

import com.backendless.Backendless
import com.backendless.exceptions.BackendlessFault
import com.backendless.files.BackendlessFile
import com.backendless.persistence.DataQueryBuilder
import kotlinx.android.synthetic.main.style_dune_ligne.*
import org.jetbrains.anko.doAsync

import org.jetbrains.anko.startActivityForResult
import org.jetbrains.anko.toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import android.net.ConnectivityManager as ConnectivityManager1
import com.backendless.async.callback.AsyncCallback as AsyncCallback1


data class PersonneBackendLess(var objectId: String? = null,
                               var Bimage: String = "",
                               var Bnom: String = "",
                               var Bemail:String="",
                               var Btel:String="",
                               var Bfixe:String="")


class MainActivity : AppCompatActivity() {
    val mReceiverOK=BatterieOK()
    val mReceiverLOW=BatterieLow()


    companion object{
        val APP_ID="98E5A86D-7391-F424-FF4F-17FAF6820200"
        val API_KEY="A1DA3880-52E0-4B98-B98F-71E7B5920367"
    }

    var personnes = mutableListOf<Personne>()

    var IMAGE:Bitmap?=null
    var chemin:Uri?=Uri.parse("/data/data/com.example.apptp3/app_images/" +
            "0e792aa8-17a0-4cf1-aec9-c15e76c09857.jpg")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /**
         * voir le traitement a effectuer selon le niveau de la batterie ok ou low
         *
         * **/
        this.registerReceiver(mReceiverLOW,  IntentFilter(Intent.ACTION_BATTERY_LOW))
        this.registerReceiver(mReceiverOK,  IntentFilter(Intent.ACTION_BATTERY_OKAY))

        Backendless.initApp(this, APP_ID, API_KEY)


        //on recupére une image aléatoire en http
        IMAGE=recupImage()

        //on recup les contacts dejas présent sur backendless
        val rec=RecuperationContacts()


        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager1
        when(connMgr.activeNetworkInfo?.type){
            ConnectivityManager1.TYPE_WIFI, ConnectivityManager1.TYPE_MOBILE ->
                rec.execute("stp")
            null -> { toast("Pas de réseau") }
        }




        buildRecyclerView()

        //le bouton pour permettre la saisie d'un contact
        btn_ajouter.setOnClickListener {

            startActivityForResult<AjoutPersonne>(1)


        }
    }




    inner class RecuperationImage() : AsyncTask<String, Void, Bitmap?>() {
        override fun doInBackground(vararg urls: String): Bitmap? {
            val urlOfImage = urls[0]
            return try {
                val inputStream = URL(urlOfImage).openStream()
                BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) { // Catch the download exception
                e.printStackTrace()
                null
            }
        }
        override fun onPostExecute(result: Bitmap?) {
            if(result!=null){
                // Display the downloaded image into image view
                toast("telechargement avec succé")

                IMAGE=result
                //image.setImageBitmap(IMAGE)
            }else{
                toast("Erreur lors du telechargement")
            }
        }
    }



    /****
     *
     * on retourn une image et on garde son chemin
     *
     * **/
    fun recupImage():Bitmap?{
        RecuperationImage()
            .execute("https://source.unsplash.com/random/800x600").get()
        val img=IMAGE

        //toast("${chemin}")

        chemin=saveImageToInternalStorage(img)
        //image.setImageURI(Uri.parse("file://mnt/sdcard/photoContacts/images.jpg"))
        return img

    }

    inner class RecuperationContacts(): AsyncTask<String, Int, MutableList<MutableMap<Any?, Any?>>>() {

        lateinit var mcontext :Context


        override fun doInBackground(vararg params: String?): MutableList<MutableMap<Any?, Any?>>? {


            //si on souhaite avoir le nombre de contacts qui sont  stocké dans le cloud
            //val count =Backendless.Data.of( "PersonneBackendLess" ).getObjectCount()

            val lesContacts = Backendless.Data.of("PersonneBackendLess")
                .find(DataQueryBuilder.create().setPageSize(25).setOffset(0))

            return lesContacts



        }


        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
        }


        override fun onPostExecute(result: MutableList<MutableMap<Any?, Any?>>) {
            super.onPostExecute(result)
            for (msg in result){

                //RecuperationImage()



                val p10=Personne(
                "${msg["bNom"]}",
                "${msg["bEmai"]}",
                "${msg["bTel"]}",
                "${msg["bFixe"]}",
                    Uri.parse("${msg["bimage"]}")
                )
                personnes.add(p10)
                //image.setImageBitmap(${msg["bImage"]})
                personnes.sortWith(compareBy({it.nom}))
                buildRecyclerView()
                mon_recycler.adapter?.notifyItemInserted(0)

            }

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            1 -> {
                // Résultat de startActivityForResult<ModifierActivity>
                if(resultCode == Activity.RESULT_OK){
                    val valider = data?.getBooleanExtra(AjoutPersonne.EXTRA_VALIDER, false) ?: false
                    if(valider){
                        // L'utilisateur a utilisé le bouton "valider"
                        // On récupère la valeur dans l'extra (avec une valeur par défaut de "")
                        val nouvValeurnom = data?.getStringExtra(AjoutPersonne.EXTRA_NOM) ?: ""
                        val nouvValeuremail = data?.getStringExtra(AjoutPersonne.EXTRA_EMAIL) ?: ""
                        val nouvValeurtel = data?.getStringExtra(AjoutPersonne.EXTRA_TEL) ?: ""
                        val nouvValeurfixe = data?.getStringExtra(AjoutPersonne.EXTRA_FAXE) ?: ""

                        val photo=recupImage()




                        var p8=Personne(nouvValeurnom
                            ,nouvValeuremail,nouvValeurtel,
                            nouvValeurfixe,chemin)


                        //un objet PersonneBackendLess que l on va stocker dans notre cloud
                        val per = PersonneBackendLess(null,chemin.toString(), nouvValeurnom, nouvValeuremail,
                            nouvValeurtel,nouvValeurfixe)




                        doAsync {
                            Backendless.Persistence
                                .of(PersonneBackendLess::class.java).save(per)

                        }


                        toast("Données bien ajouté au cloud")
                        personnes.add(0,p8)
                        //cette ligne permet de trier la liste des contactes par ordre alphabetique
                        personnes.sortWith(compareBy({it.nom}))
                        buildRecyclerView()
                        mon_recycler.adapter?.notifyItemInserted(0)

                    }else{
                        //ID--
                    }
                }else if(resultCode == Activity.RESULT_CANCELED){
                    // L'utilisateur a utilisé le bouton retour <- de son téléphone
                    // on ne fait rien de spécial non plus
                }
            }
        }
    }


    fun buildRecyclerView() {
        mon_recycler.setHasFixedSize(true)
        //mon_recycler.setAdapter(mAdapter)
        mon_recycler.layoutManager = LinearLayoutManager(this)

        mon_recycler.adapter = PersonneAdapter(personnes.toTypedArray())
        {
            var  nom="${it.nom}"
            var  tel="${it.tel}"
            var  mail="${it.email}"
            var  faxe="${it.fixe}"
            val intent3 = Intent(this, AfficheDetailActivity::class.java)
            intent3.putExtra("NOM",nom)
            intent3.putExtra("TEL",tel)
            intent3.putExtra("MAIL",mail)
            intent3.putExtra("FAXE",faxe)
            startActivity(intent3)


        }


    }



    /****
     *
     * Sauvgarder une image dans le stockage intern de l'application
     * et garder le chemin pour le stocker dans le cloud
     * et on recupére pour chaque contact le photo qui lui est associé dans le cloud
     *
     *
     * ***/

    private fun saveImageToInternalStorage(img:Bitmap?):Uri{

        val wrapper = ContextWrapper(applicationContext)


        var file = wrapper.getDir("images", Context.MODE_PRIVATE)


        // Create a file to save the image
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            // Get the file output stream
            val stream: OutputStream = FileOutputStream(file)

            // Compress bitmap
            img?.compress(Bitmap.CompressFormat.JPEG, 100, stream)

            // Flush the stream
            stream.flush()

            // Close stream
            stream.close()
        } catch (e: IOException){ // Catch the exception
            e.printStackTrace()
        }

        // Return the saved image uri
        return Uri.parse(file.canonicalPath)
    }

/****
 * lorsque on quite l'application
 * on se désabonne au Receiver
 *
 *
 * **/

    override fun onDestroy() {
        unregisterReceiver(mReceiverOK);
        unregisterReceiver(mReceiverLOW);
        super.onDestroy()
    }

}