package com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.R;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.activity.PublicarPostagemActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostagemFragment extends Fragment {

    private Button buttonAdicionarPostagem;

    public PostagemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postagem, container, false);

        //Inicializar componentes
        buttonAdicionarPostagem = view.findViewById(R.id.buttonAdicionarPostagem);


        //Adiciona evento de clique na imagem
        buttonAdicionarPostagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PublicarPostagemActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }




}
