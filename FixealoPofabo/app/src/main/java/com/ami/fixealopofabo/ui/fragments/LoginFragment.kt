package com.ami.fixealopofabo.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

import com.ami.fixealopofabo.viewmodel.LoginViewModel
import androidx.core.content.edit
import com.ami.FixealoPofabo.R
import com.ami.FixealoPofabo.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        setupEventListeners()
        observeViewModel()
        return binding.root
    }

    private fun setupEventListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.txtLogin.editText?.text.toString().trim()
            val password = binding.txtPassword.editText?.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Debe completar todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(context, "Ingrese un email válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        binding.tvFragmentRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { (token, userId) ->
                requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
                    .edit {
                        putString("token", token)
                        putInt("userId", userId)
                    }

                Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_loginFragment_to_categoryListFragment)
            }

            result.onFailure {
                Toast.makeText(context, it.message ?: "Error desconocido", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
