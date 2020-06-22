package com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.R;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.activity.VisualizacaoPostagemActivity;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model.Postagem;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model.Usuario;

import java.util.List;

public class AdapterPostagemPublica extends RecyclerView.Adapter<AdapterPostagemPublica.MyViewHolder> {

    private List<Postagem> postagens;
    private Context context;

    public AdapterPostagemPublica(List<Postagem> postagens, Context context) {
        this.postagens = postagens;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterPostagemPublica.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_postagem_publica, parent, false);
        return new AdapterPostagemPublica.MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPostagemPublica.MyViewHolder holder, int position) {
        Postagem postagem = postagens.get(position);

        holder.nomeUsuario.setText(postagem.getNomeUsuario());
        holder.titulo.setText(postagem.getTitulo());
        holder.categoria.setText(postagem.getCategoria());
        holder.descricao.setText(postagem.getDescricao());

        //Recuperar foto do usuário
        Uri uriFotoUsuario = Uri.parse(postagem.getFotoUsuario());
        Glide.with(context).load(uriFotoUsuario).into(holder.fotoUsuario);

        /*
        //Adicionar evento de clique no layoutVisualizarPostagem (esse evento foi implementado na MainActivity)
        holder.layoutVisualizarPostagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, VisualizacaoPostagemActivity.class);
                intent.putExtra("idPostagem", postagem.getIdPostagem());
                intent.putExtra("tituloPostagem", postagem.getTitulo());
                intent.putExtra("categoriaPostagem", postagem.getCategoria());
                intent.putExtra("descricaoPostagem", postagem.getDescricao());
                intent.putExtra("postagemSelecionada", (Parcelable) postagem);
                context.startActivity(intent);
            }
        });
         */

        /*
        //Recuperar a foto do usuário via a biblioteca Picasso
        String urlFoto = postagem.getFotoUsuario();
        Picasso.get().load(urlFoto).into(holder.fotoUsuario);
         */
    }

    @Override
    public int getItemCount() {
        return postagens.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descricao, categoria, nomeUsuario;
        ImageView fotoUsuario;
        LinearLayout layoutVisualizarPostagem;

        public MyViewHolder(View itemView){
            super(itemView);

            nomeUsuario = itemView.findViewById(R.id.textPostPublicoNome);
            fotoUsuario = itemView.findViewById(R.id.imagePostPublico);
            titulo = itemView.findViewById(R.id.textPostPublicoTitulo);
            categoria = itemView.findViewById(R.id.textPostPublicoCategoria);
            descricao = itemView.findViewById(R.id.textPostPublicoDescricao);
            layoutVisualizarPostagem = itemView.findViewById(R.id.layoutVisualizarPostagem);
        }
    }
}

