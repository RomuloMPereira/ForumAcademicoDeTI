package com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.R;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.adapter.AdapterPostagemPublica;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.ConfiguracaoFirebase;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.RecyclerItemClickListener;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model.Postagem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private Button buttonFeedCategoria;
    private RecyclerView recyclerPostagensPublicas;

    private List<Postagem> listaPostagens = new ArrayList<>();
    private AdapterPostagemPublica adapterPostagensPublicas;
    private DatabaseReference postagensPublicasRef;
    private AlertDialog alertDialog;
    private String filtroCategoria = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializar componentes
        inicializarComponentes();

        //Configurar Firebase
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //Configurar RecyclerView
        recyclerPostagensPublicas.setLayoutManager(new LinearLayoutManager(this));
        recyclerPostagensPublicas.setHasFixedSize(true);

        adapterPostagensPublicas = new AdapterPostagemPublica(listaPostagens, this);
        recyclerPostagensPublicas.setAdapter(adapterPostagensPublicas);

        //Recuperar postagens para o feed principal
        recuperarPostagensPublicas();

        //Esse evento foi adiocionado no AdapterPostagemPublica COMO TESTE
        //Aplicar evento de clique na postagem
        recyclerPostagensPublicas.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerPostagensPublicas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Postagem postagemSelecionada = listaPostagens.get(position);
                                Intent intent = new Intent(MainActivity.this, VisualizacaoPostagemActivity.class);
                                //tem q implementar Serializable na Postagem
                                intent.putExtra("postagemSelecionada", postagemSelecionada);
                                startActivity(intent);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }));


        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("FÓRUM ACADÊMICO DE TI");
        setSupportActionBar(toolbar);

        /*
        //Habilitar a navegação BottomNavigation
        configurarBottomNavigation();
        //Habilitar feed como tela principal do app
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();

         */

    }

    public void filtrarPorCategoria(View view){
        AlertDialog.Builder dialogCategoria = new AlertDialog.Builder(this);
        dialogCategoria.setTitle("Selecione a categoria desejada");

        //Configurar View para Spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner_categoria, null);

        //Configurar Spinner
        final Spinner spinnerFiltroCategoria = viewSpinner.findViewById(R.id.spinnerFiltroCategoria);
        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroCategoria.setAdapter(adapter);

        dialogCategoria.setView(viewSpinner);

        dialogCategoria.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filtroCategoria = spinnerFiltroCategoria.getSelectedItem().toString();
                recuperarPostagensPublicasPorCategoria();
            }
        });

        dialogCategoria.setNegativeButton("Voltar para o Feed de postagens", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recuperarPostagensPublicas();
            }
        });

        AlertDialog dialog = dialogCategoria.create();
        dialog.show();
    }

    public void recuperarPostagensPublicasPorCategoria(){
        //AlertDialog de Progresso
        alertDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Carregando postagens para a categoria " + filtroCategoria)
                .setCancelable(false)
                .build();
        alertDialog.show();

        //Configura nó por Estado
        postagensPublicasRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens")
                .child(filtroCategoria);

        postagensPublicasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPostagens.clear();
                for(DataSnapshot itemPostagem: dataSnapshot.getChildren()){
                    Postagem postagem = itemPostagem.getValue(Postagem.class);
                    listaPostagens.add(postagem);
                }

                //Exibição reversa das postagens
                //Collections.reverse(listaPostagens);

                adapterPostagensPublicas.notifyDataSetChanged();

                //Encerrando AlertDialog
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void recuperarPostagensPublicas(){
        //AlertDialog de Progresso
        alertDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Carregando postagens")
                .setCancelable(false)
                .build();
        alertDialog.show();

        //Configura nó de todas as postagens
        postagensPublicasRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens");

        listaPostagens.clear();
        postagensPublicasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot categoria : dataSnapshot.getChildren()){
                    for(DataSnapshot itemPostagem: categoria.getChildren()){
                        Postagem postagem = itemPostagem.getValue(Postagem.class);
                        listaPostagens.add(postagem);
                    }
                }

                //Exibição reversa das postagens
                //Collections.reverse(listaPostagens);

                adapterPostagensPublicas.notifyDataSetChanged();

                //Encerrando AlertDialog
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void inicializarComponentes(){
        recyclerPostagensPublicas = findViewById(R.id.recyclerPostagensPublicas);
        buttonFeedCategoria = findViewById(R.id.buttonFeedCategoria);
    }

    /*
    private void configurarBottomNavigation(){
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigation);

        //Habilitar Navegação (Fragments) - tratar eventos de click no BottomNavigation
        habilitarNavegacao(bottomNavigationViewEx);
    }

    private void habilitarNavegacao(BottomNavigationViewEx viewEx){
        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch(menuItem.getItemId()){
                    case R.id.ic_home:
                        fragmentTransaction.replace(R.id.viewPager, new FeedFragment()).commit();
                        return true;
                    case R.id.ic_pesquisa:
                        fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                        return true;
                    case R.id.ic_postagem:
                        fragmentTransaction.replace(R.id.viewPager, new PostagemFragment()).commit();
                        return true;
                    case R.id.ic_perfil:
                        fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                        return true;
                }
                return false;
            }
        });
    }

     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.menu_postagens:
                startActivity(new Intent(getApplicationContext(), MinhasPostagensActivity.class));
                break;
            case R.id.menu_perfil:
                startActivity(new Intent(getApplicationContext(), EditarPerfilActivity.class));
                break;
            case R.id.menu_sair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

}
