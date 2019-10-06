package com.gmmp.easylearn.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gmmp.easylearn.R;
import com.gmmp.easylearn.model.Modulo;
import com.gmmp.easylearn.model.Video;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import iammert.com.expandablelib.ExpandableLayout;
import iammert.com.expandablelib.Section;

import static com.gmmp.easylearn.helper.UtilKt.getCursoGlobal;

public class ModuloActivity extends AppCompatActivity {

    private AppCompatButton btnNovoModulo;
    private AlertDialog alertDialog;
    private EditText txtNomeModulo;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulo);

        iniciar();
    }

    @SuppressLint("ResourceAsColor")
    private void iniciar() {

        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Mostrar o botão
        getSupportActionBar().setHomeButtonEnabled(true);      //Ativar o botão
        getSupportActionBar().setTitle(getCursoGlobal().getNome());

        // Banner

        //Btn novo modulo
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(this);
        builderDialog.setTitle("Novo módulo");

        FrameLayout container = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.fab_margin);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.fab_margin);

        txtNomeModulo = new EditText(this);
        txtNomeModulo.setHint("Nome do módulo");
        txtNomeModulo.setBackgroundResource(R.color.colorEditText);
        txtNomeModulo.setTextColor(R.color.colorDescricao);
        txtNomeModulo.setLayoutParams(params);

        container.addView(txtNomeModulo);

        builderDialog.setView(container);
        builderDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String nome = txtNomeModulo.getText().toString();
                if (nome.isEmpty())
                    txtNomeModulo.setError("Por favor, entre com o nome do módulo");
                else {
                    Modulo modulo = new Modulo(nome);
                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference();

                    databaseReference.
                            child("cursos").
                            child(getCursoGlobal().getNome()).
                            child("modulos").
                            child(nome).
                            setValue(modulo);
                }

            }
        });

        builderDialog.setNegativeButton("Cancelar", null);

        alertDialog = builderDialog.create();

        btnNovoModulo = findViewById(R.id.buttonNovoModulo);
        btnNovoModulo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });


        // Lista de modulos

        ExpandableLayout layout = findViewById(R.id.expandable);

        layout.setRenderer(new ExpandableLayout.Renderer<Modulo, Video>() {

            // Modulo
            @Override
            public void renderParent(View view, Modulo model, boolean isExpanded, int parentPosition) {
                ((TextView) view.findViewById(R.id.tv_parent_name)).setText("Seção " + (parentPosition + 1) + " - " + model.getNome());
                if (isExpanded) {
                    view.findViewById(R.id.arrow).setBackgroundResource(R.drawable.ic_seta_para_cima);
                } else {
                    view.findViewById(R.id.arrow).setBackgroundResource(R.drawable.ic_seta_para_baixo);
                }
            }

            // Video do modulo
            @Override
            public void renderChild(final View view, Video model, int parentPosition, int childPosition) {
                if (childPosition == 0) { // essa condição está errada, pois ela não vai mostrar o primeiro vídeo do array / digitado por Paulo
                    ((TextView) view.findViewById(R.id.tv_child_name)).setTypeface(null, Typeface.BOLD);
                    ((TextView) view.findViewById(R.id.tv_child_name)).setText("Adicionar vídeo");
                    ((TextView) view.findViewById(R.id.txt_video_duracao)).setText(" ");
                } else {
                    ((TextView) view.findViewById(R.id.tv_child_name)).setText(model.getNome());
                    ((TextView) view.findViewById(R.id.txt_video_duracao)).setText(model.getDuracao());
                    (view.findViewById(R.id.tv_child_name)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView txt = (TextView) v;
                            startActivity(new Intent(getApplicationContext(), AulaActivity.class));
                            Toast.makeText(ModuloActivity.this, "Video clicado: " + txt.getText(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        layout.addSection(getSection1());
        layout.addSection(getSection2());
    }


    private Section<Modulo, Video> getSection1() {
        Section<Modulo, Video> section = new Section<>();

        List<Video> aulas = new ArrayList<>();
        Modulo modulo = new Modulo("Vamos começar");

        for (int i = 0; i < 5; i++) {
            aulas.add(new Video("" + i, "Vídeo " + i, 0, 0, 0, "0", " ", i + ".0min"));
        }

        section.parent = modulo;
        section.children.addAll(aulas);
        return section;
    }


    private Section<Modulo, Video> getSection2() {
        Section<Modulo, Video> section = new Section<>();

        List<Video> aulas = new ArrayList<>();
        Modulo modulo = new Modulo("Instalação no Windows");

        for (int i = 0; i < 5; i++) {
            aulas.add(new Video("" + i, "Vídeo " + i, 0, 0, 0, "0", " ", i + ".0min"));
        }

        section.parent = modulo;
        section.children.addAll(aulas);
        return section;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Botão adicional na ToolBar
        switch (item.getItemId()) {
            case android.R.id.home:  //ID do seu botão (gerado automaticamente pelo android, usando como está, deve funcionar
                finish();  //Método para matar a activity e não deixa-lá indexada na pilhagem
                break;
            default:
                break;
        }
        return true;
    }


}