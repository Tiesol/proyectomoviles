package com.ami.chamba_pofabo.ui.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.ami.chamba_pofabo.databinding.FragmentCategoryListBinding
import com.ami.chamba_pofabo.model.Category
import com.ami.chamba_pofabo.ui.adapter.CategoryAdapter
import com.ami.chamba_pofabo.viewmodel.CategoryListViewModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class CategoryListFragment : Fragment(), CategoryAdapter.CategoryClickListener {

    private val TAG = "CategoryListFragment"
    private var _binding: FragmentCategoryListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryListViewModel by viewModels()

    private lateinit var adapter: CategoryAdapter
    private var fullList: ArrayList<Category> = arrayListOf()
    private val selectedCategories = mutableListOf<Category>()

    private var selectedImageUri: Uri? = null
    private var token: String = ""
    private var workerId: Int = -1
    private var imageUploaded = false

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            Log.d(TAG, "Imagen seleccionada: $it")
            Glide.with(requireContext())
                .load(selectedImageUri)
                .centerCrop()
                .into(binding.imgChambeador)
            imageUploaded = false
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openImagePicker()
        } else {
            Log.e(TAG, "Permiso denegado para acceder a imágenes")
            Toast.makeText(
                requireContext(),
                "Permiso denegado. No se puede seleccionar una imagen.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated iniciado")

        val sharedPreferences = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("token", "") ?: ""

        if (token.isEmpty()) {
            Log.e(TAG, "Token no encontrado en SharedPreferences")
            Toast.makeText(requireContext(), "No se encontró token de sesión", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Token obtenido: ${token.take(10)}...")

        setupUI()
        setupRecyclerView()
        setupObservers()
        viewModel.getMyProfile(token)
        loadCategories()
    }
    private fun setupUI() {
        binding.imgChambeador.setOnClickListener {
            checkPermissionAndPickImage()
        }

        binding.btnCheck.setOnClickListener {
            if (workerId == -1) {
                Toast.makeText(requireContext(), "No se ha cargado el perfil correctamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedCategories.isEmpty()) {
                Toast.makeText(requireContext(), "No hay categorías seleccionadas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            selectedImageUri?.let {
                viewModel.uploadProfilePicture(token, it, requireContext())
            }

            viewModel.updateWorkerCategories(token, workerId, selectedCategories)

            Toast.makeText(requireContext(), "Enviando información...", Toast.LENGTH_SHORT).show()
        }

        binding.btnAddCategoria.setOnClickListener {
            showCreateCategoryDialog()
        }
    }

    private fun showCreateCategoryDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Crear nueva categoría")

        val input = android.widget.EditText(requireContext())
        input.hint = "Nombre de la categoría"
        input.inputType = android.text.InputType.TYPE_CLASS_TEXT

        builder.setView(input)

        builder.setPositiveButton("Crear") { _, _ ->
            val categoryName = input.text.toString().trim()
            if (categoryName.isNotEmpty()) {
                viewModel.createCategory(token, categoryName)
            } else {
                Toast.makeText(requireContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun checkPermissionAndPickImage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            openImagePicker()
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    openImagePicker()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    Toast.makeText(
                        requireContext(),
                        "Se necesita permiso para acceder a tus imágenes",
                        Toast.LENGTH_SHORT
                    ).show()
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }


    private fun openImagePicker() {
        pickImage.launch("image/*")
    }

    private fun setupRecyclerView() {
        adapter = CategoryAdapter()
        adapter.setOnCategoryClickListener(this)
        binding.recyclerCategorias.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerCategorias.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.categoryList.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { categories ->
                    fullList = ArrayList(categories)
                    adapter.setData(fullList)
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), error.message ?: "Error al cargar categorías", Toast.LENGTH_SHORT).show()
                }
            )
        }
        viewModel.createCategoryResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { newCategory ->
                    Toast.makeText(requireContext(), "Categoría '${newCategory.name}' creada exitosamente", Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Error al crear categoría: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
        viewModel.createCategoryResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { newCategory ->
                    Toast.makeText(requireContext(), "Categoría '${newCategory.name}' creada exitosamente", Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Error al crear categoría: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        viewModel.profileData.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { meResponse ->
                    workerId = meResponse.worker.id

                    if (!meResponse.worker.picture_url.isNullOrEmpty() && meResponse.worker.picture_url != "null") {
                        Glide.with(requireContext())
                            .load(meResponse.worker.picture_url)
                            .signature(ObjectKey(System.currentTimeMillis()))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(binding.imgChambeador)
                    }
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Error al cargar perfil: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        viewModel.uploadImageResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { message ->
                    Toast.makeText(requireContext(), "Imagen subida: $message", Toast.LENGTH_SHORT).show()
                    imageUploaded = true

                    lifecycleScope.launch {
                        delay(500)
                        viewModel.getMyProfile(token)
                    }
                },
                onFailure = { error ->
                    imageUploaded = false
                    Toast.makeText(requireContext(), "Error al subir imagen: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }

        viewModel.updateCategoriesResult.observe(viewLifecycleOwner) { result ->
            result.fold(
                onSuccess = { message ->
                    Toast.makeText(requireContext(), "Categorías actualizadas: $message", Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Toast.makeText(requireContext(), "Error al actualizar categorías: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    private fun loadCategories() {
        if (token.isEmpty()) {
            Toast.makeText(requireContext(), "No se encontró el token de sesión", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.loadCategories(token)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCategoryClick(category: Category, isChecked: Boolean) {
        adapter.updateCheckState(category.id, isChecked)
        if (isChecked) {
            if (!selectedCategories.contains(category)) {
                selectedCategories.add(category)
            }
        } else {
            selectedCategories.remove(category)
        }
    }
}
