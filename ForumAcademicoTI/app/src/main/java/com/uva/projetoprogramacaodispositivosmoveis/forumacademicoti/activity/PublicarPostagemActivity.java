package com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.R;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.ConfiguracaoFirebase;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.Permissao;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.UsuarioFirebase;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model.Postagem;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model.Usuario;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PublicarPostagemActivity extends AppCompatActivity implements View.OnClickListener{

    private Spinner spinnerCategoria;
    private EditText editTituloPostagem, editDescricaoPostagem;
    private ImageView imagePostagem1, imagePostagem2, imagePostagem3;
    private Button buttonPublicarPostagem;
    private AlertDialog alertDialog;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private List<String> listaImagensRecuperadas = new ArrayList<>();
    private List<String> listaUrlImagensFirebase = new ArrayList<>();

    private Postagem postagem;
    //private String idUsuarioLogado;
    private Usuario usuarioLogado;
    private StorageReference storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_postagem);

        //Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("CRIAR POSTAGEM");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        //Configurações do Firebase
        //idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();
        usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();
        storage = ConfiguracaoFirebase.getFirebaseStorage();

        //Validar permissões
        Permissao.validarPermissoes(permissoes, this, 1);

        //Inicializar componentes
        inicializarComponentes();

        //Carregar dados no spinner
        carregarDadosSpinner();

        //Publicar Postagem
        buttonPublicarPostagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDadosPostagem(v);
                salvarPostagem();
            }
        });

    }

    public void salvarPostagem(){
        //Alert Dialog de Progresso
        alertDialog = new SpotsDialog.Builder().setContext(this)
                .setMessage("Publicando Postagem")
                .setCancelable(false)
                .build();
        alertDialog.show();

        //Salvar imagens no Storage
        if(listaImagensRecuperadas.size() != 0){
            for(int i=0; i < listaImagensRecuperadas.size(); i++){
                String urlImagem = listaImagensRecuperadas.get(i);
                int tamanhoLista = listaImagensRecuperadas.size();
                salvarImagensStorage(urlImagem, tamanhoLista, i);
            }
        } else { //Se não tiver imagens, não chamará o método salvarImagensStorage
            postagem.salvar();
            //Encerrando AlertDialog
            alertDialog.dismiss();
            //Finalizando atividade
            finish();
        }
    }

    private void salvarImagensStorage(String urlString, final int totalImagens, int contador){
        //Criar nó de imagens no Storage
        StorageReference imagemPostagem = storage
                .child("imagens")
                .child("postagens")
                .child(postagem.getIdPostagem())
                .child("imagem" + contador);

        //Fazer upload do arquivo
        UploadTask uploadTask = imagemPostagem.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while(!uri.isComplete());
                Uri url = uri.getResult();

                String urlConvertida = url.toString();

                listaUrlImagensFirebase.add(urlConvertida);

                if(totalImagens == listaUrlImagensFirebase.size()){
                    postagem.setImagens(listaUrlImagensFirebase);

                    //Salvar a postagem já com as imagens
                    postagem.salvar();
                    //Encerrando AlertDialog
                    alertDialog.dismiss();
                    //Finalizando atividade
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(PublicarPostagemActivity.this, "Falha ao fazer upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Postagem configurarPostagem(){
        String categoria = spinnerCategoria.getSelectedItem().toString();
        String titulo = editTituloPostagem.getText().toString();
        String descricao = editDescricaoPostagem.getText().toString();

        Postagem postagem = new Postagem();
        postagem.setCategoria(categoria);
        postagem.setTitulo(titulo);
        postagem.setDescricao(descricao);
        postagem.setIdUsuario(usuarioLogado.getId());
        postagem.setNomeUsuario(usuarioLogado.getNome());
        postagem.setFotoUsuario(usuarioLogado.getCaminhoFoto());

        return postagem;
    }

    public void validarDadosPostagem(View view){
        postagem = configurarPostagem();

        if(!postagem.getCategoria().isEmpty()){
            if(!postagem.getTitulo().isEmpty()){
                if(!postagem.getDescricao().isEmpty()){

                } else {
                    Toast.makeText(this, "Preencha a descrição da postagem", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Preencha o título da postagem", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Selecione uma categoria", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.imagePostagem1:
                escolherImagem(1);
                break;
            case R.id.imagePostagem2:
                escolherImagem(2);
                break;
            case R.id.imagePostagem3:
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    //Capturar a imagem
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            //Recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //Configurar imagem no ImageView para salvá-la dentro de um array
            if(requestCode == 1){
                imagePostagem1.setImageURI(imagemSelecionada);
            } else if(requestCode == 2){
                imagePostagem2.setImageURI(imagemSelecionada);
            } else if(requestCode == 3){
                imagePostagem3.setImageURI(imagemSelecionada);
            }
            listaImagensRecuperadas.add(caminhoImagem);
        }
    }

    private void inicializarComponentes(){
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        editTituloPostagem = findViewById(R.id.editTituloPostagem);
        editDescricaoPostagem = findViewById(R.id.editDescricaoPostagem);
        imagePostagem1 = findViewById(R.id.imagePostagem1);
        imagePostagem2 = findViewById(R.id.imagePostagem2);
        imagePostagem3 = findViewById(R.id.imagePostagem3);

        imagePostagem1.setOnClickListener(this);
        imagePostagem2.setOnClickListener(this);
        imagePostagem3.setOnClickListener(this);

        buttonPublicarPostagem = findViewById(R.id.buttonPublicarPostagem);
    }

    private void carregarDadosSpinner(){
        String[] categorias = getResources().getStringArray(R.array.categorias);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){
            if(permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões negadas");
        builder.setMessage("Para criar uma postagem é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
