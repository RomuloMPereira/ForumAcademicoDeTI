package com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model;

import com.google.firebase.database.DatabaseReference;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.helper.ConfiguracaoFirebase;

public class Comentario {
    private String idComentario;
    private String idPostagem;
    private String idUsuario;
    private String caminhoFoto;
    private String nomeUusario;
    private String comentario;

    public Comentario() {
    }

    public boolean salvar(){
        DatabaseReference comentariosRef = ConfiguracaoFirebase.getFirebase()
                .child("comentarios")
                .child(getIdPostagem());

        String chaveComentario = comentariosRef.push().getKey();
        setIdComentario(chaveComentario);

        comentariosRef.child(getIdComentario()).setValue(this);

        return true;
    }

    public String getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(String idComentario) {
        this.idComentario = idComentario;
    }

    public String getIdPostagem() {
        return idPostagem;
    }

    public void setIdPostagem(String idPostagem) {
        this.idPostagem = idPostagem;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    public String getNomeUusario() {
        return nomeUusario;
    }

    public void setNomeUusario(String nomeUusario) {
        this.nomeUusario = nomeUusario;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
