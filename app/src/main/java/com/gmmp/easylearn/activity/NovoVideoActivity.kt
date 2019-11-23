package com.gmmp.easylearn.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gmmp.easylearn.R
import com.gmmp.easylearn.dialog.ViewDialog
import com.gmmp.easylearn.helper.cursoGlobal
import com.gmmp.easylearn.helper.moduloGlobal
import com.gmmp.easylearn.helper.modulosReferencia
import com.gmmp.easylearn.helper.videosReferencia
import com.gmmp.easylearn.model.Video
import com.google.firebase.storage.FirebaseStorage
import com.greentoad.turtlebody.mediapicker.MediaPicker
import com.greentoad.turtlebody.mediapicker.core.MediaPickerConfig
import kotlinx.android.synthetic.main.activity_novo_video.*
import org.jetbrains.anko.toast
import java.util.*

class NovoVideoActivity : AppCompatActivity() {

    private var URL_VIDEO = ""
    private val idVideo = UUID.randomUUID().toString()
    private var qtdAulas = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_novo_video)

        supportActionBar?.hide()
        toolbarVideo.inflateMenu(R.menu.salvar)
        toolbarVideo.setOnMenuItemClickListener {
            if (editNomeVideo.text.toString().isEmpty()) {
                toast("É necessário que você envie um vídeo")
            } else {

                val nomeVideo = editNomeVideo.text.toString()
                val descricaoVideo = editDescricaoVideo.text.toString()

                if (URL_VIDEO.isNotEmpty()) {

                    val video = Video(idVideo,
                            nomeVideo,
                            "",
                            descricaoVideo,
                            URL_VIDEO)

                    videosReferencia(cursoGlobal.id, moduloGlobal.id).child(idVideo).setValue(video).toString()

                    qtdAulas = moduloGlobal.qtdAulas
                    qtdAulas++
                    modulosReferencia(cursoGlobal.id).child(moduloGlobal.id).child("qtdAulas").setValue(qtdAulas)
                    toast("'$nomeVideo' foi adicionado com sucesso!")


                } else {
                    toast("Não foi possível publicar a Aula")
                }

                finish()
            }
            false
        }

        imgUploadVideo.setOnClickListener {

            val pickerConfig = MediaPickerConfig()
                    .setUriPermanentAccess(false)
                    .setAllowMultiSelection(false)
                    .setShowConfirmationDialog(true)
                    .setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

            MediaPicker.with(this, MediaPicker.MediaTypes.VIDEO)
                    .setConfig(pickerConfig)
                    .setFileMissingListener(object : MediaPicker.MediaPickerImpl.OnMediaListener{
                        override fun onMissingFileWarning() {
                            toast("O arquivo não pode ser encontrado!")
                        }
                    })
                    .onResult()
                    .subscribe({

                        val file = it[0]

                        val videoRef = FirebaseStorage.getInstance().reference
                                .child(cursoGlobal.id)
                                .child(moduloGlobal.id)
                                .child(idVideo)

                        val uploadTask = videoRef.putFile(file)

                        val upVideo = ViewDialog(this)
                        upVideo.showDialog("Aguarde", "Realizando upload do vídeo")

                        uploadTask.addOnFailureListener {
                            toast("Não foi possível realizar upload do vídeo")

                            upVideo.hideDialog()
                        }.addOnSuccessListener {
                            videoRef.downloadUrl.addOnCompleteListener {
                                URL_VIDEO = it.result.toString()
                                toast("Upload concluido: $it")

                                upVideo.hideDialog()
                            }
                        }

                    },{
                        toast("Erro: $it")
                    })

        }

        btnIncluirMaterial.setOnClickListener {
            // TODO INCLUIR MATERIAL
        }

    }
}
