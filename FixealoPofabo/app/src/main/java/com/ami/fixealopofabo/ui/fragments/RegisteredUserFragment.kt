package com.ami.fixealopofabo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ami.FixealoPofabo.R
import com.ami.FixealoPofabo.databinding.FragmentRegisteredUserBinding


class RegisteredUserFragment : Fragment() {

    private var _binding: FragmentRegisteredUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisteredUserBinding.inflate(inflater, container, false)
        setupListeners()
        return binding.root
    }

    private fun setupListeners() {
        binding.btnReturnLoginFragment.setOnClickListener {
            findNavController().navigate(R.id.action_fragment_registered_user_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
