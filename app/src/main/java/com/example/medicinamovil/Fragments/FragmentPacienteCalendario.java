package com.example.medicinamovil.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.medicinamovil.MainActivity;
import com.example.medicinamovil.ObjetosNat.Paciente;
import com.example.medicinamovil.PrincipalPacienteActivity;
import com.example.medicinamovil.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPacienteCalendario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPacienteCalendario extends Fragment {
    private TextView fecha;
    private ListView listaMedicamentosDiarios;
    private String[] header={"Medicamentos", "Hora","Estado"};
    private HashMap<Integer, String[]> receta;



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentPacienteCalendario() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPacienteCalendario newInstance(String param1, String param2) {
        FragmentPacienteCalendario fragment = new FragmentPacienteCalendario();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);
        //Actualizacion de fecha
        fecha=(TextView) view.findViewById(R.id.tvFecha);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String fechaFormato = df.format(Calendar.getInstance().getTime());
        fecha.setText(fechaFormato);

        //LisView de medicamentos
        obtenerMedicamentos();
        listaMedicamentosDiarios=(ListView) view.findViewById(R.id.lvMedicamentosDiarios);
        listaMedicamentosDiarios.setAdapter(new AdaptadorMedicamentosPaciente(view.getContext(), receta));
        return view;
    }

    private void obtenerMedicamentos(){
        receta=((Paciente) MainActivity.getUser()).getReceta();
    }


}