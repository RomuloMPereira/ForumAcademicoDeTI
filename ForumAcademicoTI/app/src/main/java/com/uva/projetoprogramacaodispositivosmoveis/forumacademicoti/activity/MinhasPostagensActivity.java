package com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.R;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.adapter.AdapterPostagem;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.ConfiguracaoFirebase;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.RecyclerItemClickListener;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.UsuarioFirebase;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model.Postagem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MinhasPostagensActivity extends AppCompatActivity {

    private RecyclerView recyclerMinhasPostagens;
    private List<Postagem> postagens = new ArrayList<>();
    private AdapterPostagem adapterPostagens;

    private DatabaseReference postagemUsuarioRef;
    private AlertDialog alertDialog;
    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_postagens);

        //Configurar Firebase
        postagemUsuarioRef = ConfiguracaoFirebase.getFirebase()
                .child("minhas_postagens")
                .child(UsuarioFirebase.getIdentificadorUsuario());
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        //Inicializar componentes
        inicializarComponentes();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("MINHAS POSTAGENS");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //Configurar RecyclerView
        recyclerMinhasPostagens.setLayoutManager(new LinearLayoutManager(this));
        recyclerMinhasPostagens.setHasFixedSize(true);

        adapterPostagens = new AdapterPostagem(postagens, this);
        recyclerMinhasPostagens.setAdapter(adapterPostagens);

        //Recuperar postagens para o usuário
        recuperarPostagens();

        //Adicionar evento de clique no RecyclerView
        recyclerMinhasPostagens.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerMinhasPostagens,
                        new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Postagem postagemSelecionada = postagens.get(position);
                        Intent intent = new Intent(MinhasPostagensActivity.this, VisualizacaoPostagemActivity.class);
                        //tem q implementar Serializable na Postagem
                        intent.putExtra("postagemSelecionada", postagemSelecionada);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Postagem postagemSelecionada = postagens.get(position);
                        confirmarRemocao(postagemSelecionada);
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PublicarPostagemActivity.class));
            }
        });
    }

    private void confirmarRemocao(final Postagem postagemSelecionada){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmação de remoção");
        builder.setMessage("Você selecionou a opção para remover a postagem. Você quer mesmo removê-la?");
        builder.setCancelable(false);
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                postagemSelecionada.remover();
                removerImagensStorage(postagemSelecionada);
                Toast.makeText(getApplicationContext(), "Removendo sua postagem", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Voltando para suas postagens", Toast.LENGTH_SHORT).show();
            }
        });

        builder.show();
    }

    public void removerImagensStorage(final Postagem postagemSelecionada){
        List<String> imagens = postagemSelecionada.getImagens();
        if(imagens.size() != 0){
            for(int i=0; i < imagens.size(); i++){
                String urlImagem = imagens.get(i);
                StorageReference imagem = storage
                        .child("imagens")
                        .child("postagens")
                        .child(postagemSelecionada.getIdPostagem())
                        .child("imagem" + i);
                imagem.delete();
            }
        }
    }

    private void recuperarPostagens(){
        //AlertDialog de Progresso
        alertDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Carregando suas postagens")
                .setCancelable(false)
                .build();
        alertDialog.show();

        postagemUsuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postagens.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    postagens.add(ds.getValue(Postagem.class));
                }

                //Exibição reversa das postagens
                //Collections.reverse(postagens);

                adapterPostagens.notifyDataSetChanged();

                //Encerrando AlertDialog
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void inicializarComponentes(){
        recyclerMinhasPostagens = findViewById(R.id.recyclerMinhasPostagens);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

}
