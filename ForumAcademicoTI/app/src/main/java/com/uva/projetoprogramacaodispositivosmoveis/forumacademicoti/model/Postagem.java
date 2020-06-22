package com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.ConfiguracaoFirebase;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.UsuarioFirebase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Postagem implements Serializable{
    private String idPostagem;
    private String categoria;
    private String titulo;
    private String descricao;
    private List<String> imagens;

    private String idUsuario;
    private String nomeUsuario;
    private String fotoUsuario;

    public Postagem() {
        DatabaseReference postagemRef = ConfiguracaoFirebase.getFirebase().child("minhas_postagens");
        setIdPostagem(postagemRef.push().getKey());
    }

    public void salvar(){
        //String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        Usuario usuarioLogado = UsuarioFirebase.getDadosUsuarioLogado();

        DatabaseReference postagemRef = ConfiguracaoFirebase.getFirebase().child("minhas_postagens");
        postagemRef
                //.child(idUsuario)
                .child(usuarioLogado.getId())
                .child(getIdPostagem())
                .setValue(this);

        salvarPostagemPublica();
    }

    public void salvarPostagemPublica(){
        DatabaseReference postagemRef = ConfiguracaoFirebase.getFirebase().child("postagens");
        postagemRef
                .child(getCategoria())
                .child(getIdPostagem())
                .setValue(this);
    }

    public void remover(){

        String idUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference postagemRef = ConfiguracaoFirebase.getFirebase()
                .child("minhas_postagens")
                .child(idUsuario)
                .child(getIdPostagem());

        postagemRef.removeValue();

        removerPostagemPublica();
    }

    public void removerPostagemPublica(){
        DatabaseReference postagemRef = ConfiguracaoFirebase.getFirebase()
                .child("postagens")
                .child(getCategoria())
                .child(getIdPostagem());

        postagemRef.removeValue();
    }

    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getImagens() {
        return imagens;
    }

    public void setImagens(List<String> imagens) {
        this.imagens = imagens;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getFotoUsuario() {
        return fotoUsuario;
    }

    public void setFotoUsuario(String fotoUsuario) {
        this.fotoUsuario = fotoUsuario;
    }
}
