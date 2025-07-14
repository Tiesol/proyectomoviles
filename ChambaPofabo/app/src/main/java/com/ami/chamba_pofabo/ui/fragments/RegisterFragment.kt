package com.ami.chamba_pofabo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ami.chamba_pofabo.R
import com.ami.chamba_pofabo.databinding.FragmentRegisterBinding
import com.ami.chamba_pofabo.viewmodel.RegisterViewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setupListeners()
        observeVM()
        return binding.root
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val name      = binding.txtNombre.editText?.text.toString().trim()
            val lastName  = binding.txtApellido.editText?.text.toString().trim()
            val email     = binding.txtEmailResgister.editText?.text.toString().trim()
            val password  = binding.txtPasswordRegister.editText?.text.toString()

            if (name.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
                toast("Todos los campos son obligatorios"); return@setOnClickListener
            }
            if (!password.matches(Regex("^(?=.*[A-Z])(?=.*\\d).+$"))) {
                toast("La contraseña debe tener al menos una mayúscula y un número"); return@setOnClickListener
            }
            binding.btnRegister.isEnabled = false
            viewModel.register(name, lastName, email, password)
        }
    }

    private fun observeVM() {
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            binding.btnRegister.isEnabled = true
            result.onSuccess {
                toast("Registro exitoso, ingresa con tu contraseña")
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            result.onFailure {
                toast(it.message ?: "Error al registrar")
            }
        }
    }

    private fun toast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}
