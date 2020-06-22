package com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.R;
import com.uva.projetoprogramacaodispositivosmoveis.forumacademicoti.activity.EditarPerfilActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerfilFragment extends Fragment {

    private ImageView imagePerfil;
    private Button buttonEditarPerfil;
    public GridView gridViewPerfil;

    public PerfilFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //Configurações dos Componentes
        gridViewPerfil = view.findViewById(R.id.gridViewPerfil);
        imagePerfil = view.findViewById(R.id.imagePerfil);
        buttonEditarPerfil = view.findViewById(R.id.buttonEditarPerfil);

        //Abrir edição de perfil
        buttonEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditarPerfilActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
