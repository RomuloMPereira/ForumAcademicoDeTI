package com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.R;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.model.Postagem;

import java.util.List;

public class AdapterPostagem extends RecyclerView.Adapter<AdapterPostagem.MyViewHolder> {

    private List<Postagem> postagens;
    private Context context;

    public AdapterPostagem(List<Postagem> postagens, Context context) {
        this.postagens = postagens;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_postagem, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Postagem postagem = postagens.get(position);
        holder.titulo.setText(postagem.getTitulo());
        holder.categoria.setText(postagem.getCategoria());
        holder.descricao.setText(postagem.getDescricao());
    }

    @Override
    public int getItemCount() {
        return postagens.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView titulo;
        TextView descricao;
        TextView categoria;

        public MyViewHolder(View itemView){
            super(itemView);

            titulo = itemView.findViewById(R.id.textMinhaPostagemTitulo);
            categoria = itemView.findViewById(R.id.textMinhaPostagemCategoria);
            descricao = itemView.findViewById(R.id.textMinhaPostagemDescricao);
        }
    }
}
