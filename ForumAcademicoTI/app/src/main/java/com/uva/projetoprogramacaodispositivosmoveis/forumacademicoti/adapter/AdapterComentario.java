package com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.R;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model.Comentario;

import java.util.List;

public class AdapterComentario extends RecyclerView.Adapter<AdapterComentario.MyViewHolder> {

    private List<Comentario> listaComentarios;
    private Context context;

    public AdapterComentario(List<Comentario> listaComentarios, Context context) {
        this.listaComentarios = listaComentarios;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterComentario.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_comentario, parent, false);
        return new AdapterComentario.MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterComentario.MyViewHolder holder, int position) {
        Comentario comentario = listaComentarios.get(position);

        holder.nomeUsuario.setText(comentario.getNomeUusario());
        holder.comentario.setText(comentario.getComentario());

        //Recuperar foto do usuário
        Uri uriFotoUsuario = Uri.parse(comentario.getCaminhoFoto());
        Glide.with(context).load(uriFotoUsuario).into(holder.fotoUsuario);
    }

    @Override
    public int getItemCount() {
        return listaComentarios.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView fotoUsuario;
        TextView nomeUsuario, comentario;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fotoUsuario = itemView.findViewById(R.id.imageFotoUsuarioComentario);
            nomeUsuario = itemView.findViewById(R.id.textNomeUsuarioComentario);
            comentario = itemView.findViewById(R.id.textComentario);
        }
    }
}
