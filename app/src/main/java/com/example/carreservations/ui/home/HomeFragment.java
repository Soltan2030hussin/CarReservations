package com.example.carreservations.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.carreservations.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // ربط واجهة XML
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // تعيين النص مباشرة
        TextView textView = binding.textHome;
        textView.setText("This is the Home screen of Car Reservations!");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
