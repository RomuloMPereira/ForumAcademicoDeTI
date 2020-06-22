package com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.R;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.ConfiguracaoFirebase;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.UsuarioFirebase;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private ProgressBar progressBar;

    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        inicializarComponentes();

        progressBar.setVisibility(View.GONE);
        //Cadastrar usuário
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if(!textoNome.isEmpty()){
                    if(!textoEmail.isEmpty()){
                        if(!textoSenha.isEmpty()){
                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);
                            cadastrar(usuario);
                        } else {
                            Toast.makeText(CadastroActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CadastroActivity.this, "Preencha o email!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CadastroActivity.this, "Preencha o nome!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Método responsável por cadastrar usuário e fazer as validações do cadastro
    public void cadastrar(final Usuario usuario){
        progressBar.setVisibility(View.VISIBLE);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao
                .createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    try {
                                        progressBar.setVisibility(View.GONE);

                                        //Salvar dados no Firebase
                                        String idUsuario = task.getResult().getUser().getUid();
                                        usuario.setId(idUsuario);
                                        usuario.salvar();

                                        //Salvar dados no profile do Firebase
                                        UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                                        Toast.makeText(CadastroActivity.this, "Cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                                        //Redirecionar para Main Activity
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                        //Finalizar a activity atual
                                        finish();
                                    } catch(Exception e){
                                        e.printStackTrace();
                                    }
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    String erroExcecao = "";
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e){
                                        erroExcecao = "Digite uma senha mais forte!";
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        erroExcecao = "Digite um email válido!";
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        erroExcecao = "Esta conta já foi cadastrada!";
                                    } catch (Exception e) {
                                        erroExcecao = "Ao cadastrar o usuário: " + e.getMessage();
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(CadastroActivity.this, "Erro " + erroExcecao, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                );
    }

    public void inicializarComponentes(){
        campoNome = findViewById(R.id.editCadastroNome);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoCadastrar = findViewById(R.id.buttonCadastrar);
        progressBar = findViewById(R.id.progressCadastrar);

        campoNome.requestFocus();
    }
}
