package com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.R;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.adapter.AdapterComentario;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.ConfiguracaoFirebase;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.UsuarioFirebase;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model.Comentario;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model.Postagem;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class VisualizacaoPostagemActivity extends AppCompatActivity {

    private TextView textPostDetalheTitulo, textPostDetalheCategoria, textPostDetalheDescricao, editPostDetalheComentario
            ;
    private CarouselView carouselView;
    private RecyclerView recyclerPostagemComentarios;
    private Button buttonPostDetalheComentar;

    private Postagem postagemSelecionada;
    private Usuario usuario;
    private AdapterComentario adapterComentario;
    private List<Comentario> listaComentarios = new ArrayList<>();
    private AlertDialog alertDialog;

    private DatabaseReference comentariosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizacao_postagem);

        //Inicializar Componentes
        inicializarComponentes();

        //Configurações para o Usuário Logado que irá comentar
        usuario = UsuarioFirebase.getDadosUsuarioLogado();

        //Configurar RecyclerView (comentários)
        adapterComentario = new AdapterComentario(listaComentarios, getApplicationContext());
        recyclerPostagemComentarios.setHasFixedSize(true);
        recyclerPostagemComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerPostagemComentarios.setAdapter(adapterComentario);

        //Recuperar postagem selecionada para visualização com as imagens da postagem
        postagemSelecionada = (Postagem) getIntent().getSerializableExtra("postagemSelecionada");
        if(postagemSelecionada != null){
            textPostDetalheTitulo.setText(postagemSelecionada.getTitulo());
            textPostDetalheCategoria.setText(postagemSelecionada.getCategoria());
            textPostDetalheDescricao.setText(postagemSelecionada.getDescricao());

            //Recuperar as imagens para colocar no CarouselView
            if(postagemSelecionada.getImagens() != null){
                ImageListener imageListener = new ImageListener() {
                    @Override
                    public void setImageForPosition(int position, ImageView imageView) {
                        String urlString = postagemSelecionada.getImagens().get(position);
                        Uri urlImagem = Uri.parse(urlString);
                        Glide.with(VisualizacaoPostagemActivity.this).load(urlImagem).into(imageView);
                    }
                };
                carouselView.setPageCount(postagemSelecionada.getImagens().size());
                carouselView.setImageListener(imageListener);
            }

        }

        //Recuperar comentários
        recuperarComentarios();

        /*
        //Recuperar dados da postagem do AdapterPostagemPublica, se o evento de clique estivesse nessa classe
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            idPostagem = bundle.getString("idPostagem");
            tituloPostagem = bundle.getString("tituloPostagem");
            categoriaPostagem = bundle.getString("categoriaPostagem");
            descricaoPostagem = bundle.getString("descricaoPostagem");
        }
         */

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("POSTAGEM E COMENTÁRIOS");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);
    }


    private void recuperarComentarios(){
        //AlertDialog de Progresso
        alertDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Carregando os comentários")
                .setCancelable(false)
                .build();
        alertDialog.show();

        comentariosRef = ConfiguracaoFirebase.getFirebase()
                .child("comentarios")
                .child(postagemSelecionada.getIdPostagem());

        listaComentarios.clear();
        comentariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    listaComentarios.add(ds.getValue(Comentario.class));
                }

                adapterComentario.notifyDataSetChanged();

                //Encerrando AlertDialog
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void salvarComentario(View view){
        String textoComentario = editPostDetalheComentario.getText().toString();

        if(textoComentario != null && !textoComentario.equals("")){

            Comentario comentario = new Comentario();
            comentario.setIdPostagem(postagemSelecionada.getIdPostagem());
            comentario.setIdUsuario(usuario.getId());
            comentario.setNomeUusario(usuario.getNome());
            comentario.setCaminhoFoto(usuario.getCaminhoFoto());
            comentario.setComentario(textoComentario);

            if(comentario.salvar()){
                Toast.makeText(this, "Comentário salvo com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Erro ao salvar o comentário", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Insira um comentário", Toast.LENGTH_SHORT).show();
        }

        //Limpar comentário digitado
        editPostDetalheComentario.setText("");
    }

    public void inicializarComponentes(){
        textPostDetalheTitulo = findViewById(R.id.textPostDetalheTitulo);
        textPostDetalheCategoria = findViewById(R.id.textPostDetalheCategoria);
        textPostDetalheDescricao = findViewById(R.id.textPostDetalheDescricao);
        carouselView = findViewById(R.id.carouselView);
        recyclerPostagemComentarios = findViewById(R.id.recyclerPostagemComentarios);
        editPostDetalheComentario = findViewById(R.id.editPostDetalheComentario);
        buttonPostDetalheComentar = findViewById(R.id.buttonPostDetalheComentar);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
