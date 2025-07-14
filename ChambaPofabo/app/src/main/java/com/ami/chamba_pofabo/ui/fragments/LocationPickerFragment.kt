package com.ami.chamba_pofabo.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ami.chamba_pofabo.R
import com.ami.chamba_pofabo.databinding.FragmentLocationPickerBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class LocationPickerFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentLocationPickerBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null
    private var appointmentId: Int = -1
    private var fixedMarker: Marker? = null

    private var receivedLatitude: String? = null
    private var receivedLongitude: String? = null
    private var fixedLocation: LatLng? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.entries.all { it.value }) {
            enableMyLocation()
        } else {
            Toast.makeText(requireContext(), "Se requieren permisos de ubicación", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appointmentId = arguments?.getInt("appointmentId", -1) ?: -1
        receivedLatitude = arguments?.getString("latitude")
        receivedLongitude = arguments?.getString("longitude")

        Log.d("LocationPicker", "=== DATOS RECIBIDOS ===")
        Log.d("LocationPicker", "appointmentId: $appointmentId")
        Log.d("LocationPicker", "receivedLatitude: '$receivedLatitude'")
        Log.d("LocationPicker", "receivedLongitude: '$receivedLongitude'")
        Log.d("LocationPicker", "Latitude isEmpty: ${receivedLatitude.isNullOrEmpty()}")
        Log.d("LocationPicker", "Longitude isEmpty: ${receivedLongitude.isNullOrEmpty()}")

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        Log.d("LocationPicker", "=== MAPA LISTO ===")
        Log.d("LocationPicker", "receivedLatitude: '$receivedLatitude'")
        Log.d("LocationPicker", "receivedLongitude: '$receivedLongitude'")

        checkLocationPermission()
    }

    private fun updateLocationInfo(location: LatLng) {
        binding.tvLocationCoordinates.text = "Lat: ${String.format("%.6f", location.latitude)}, Lng: ${String.format("%.6f", location.longitude)}"
    }

    private fun checkLocationPermission() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocation()
        } else {
            requestPermissionLauncher.launch(permissions)
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (::map.isInitialized) {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true

            if (!receivedLatitude.isNullOrEmpty() && !receivedLongitude.isNullOrEmpty() &&
                receivedLatitude != "0.0" && receivedLongitude != "0.0") {
                Log.d("LocationPicker", "=== PROCESANDO COORDENADAS RECIBIDAS ===")
                try {
                    val lat = receivedLatitude!!.toDouble()
                    val lng = receivedLongitude!!.toDouble()
                    Log.d("LocationPicker", "Coordenadas parseadas: lat=$lat, lng=$lng")

                    fixedLocation = LatLng(lat, lng)

                    fixedMarker = map.addMarker(
                        MarkerOptions()
                            .position(fixedLocation!!)
                            .title("Ubicación de la cita")
                            .draggable(false)
                    )

                    binding.ivLocationMarker?.visibility = View.GONE

                    Log.d("LocationPicker", "Marcador creado en: ${fixedLocation!!.latitude}, ${fixedLocation!!.longitude}")
                    Log.d("LocationPicker", "Picker del centro ocultado")

                    moveCamera(fixedLocation!!)
                    updateLocationInfo(fixedLocation!!)

                    Toast.makeText(requireContext(), "Ubicación fija de la cita", Toast.LENGTH_SHORT).show()
                } catch (e: NumberFormatException) {
                    Log.e("LocationPicker", "Error parseando coordenadas: ${e.message}")
                    Toast.makeText(requireContext(), "Coordenadas inválidas: ${e.message}", Toast.LENGTH_SHORT).show()
                    setupNormalMode()
                }
            } else {
                Log.d("LocationPicker", "=== NO HAY COORDENADAS VÁLIDAS - MODO NORMAL ===")
                setupNormalMode()
            }
        }
    }

    private fun setupNormalMode() {
        map.setOnCameraIdleListener {
            val center = map.cameraPosition.target
            updateLocationInfo(center)
        }

        binding.ivLocationMarker?.visibility = View.VISIBLE

        if (fixedLocation == null) {
            getCurrentLocationAndMove()
        }
    }

    private fun getCurrentLocationAndMove() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val defaultLocation = LatLng(-17.783616, -63.182084)
            moveCamera(defaultLocation)
            Toast.makeText(requireContext(), "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                currentLocation = LatLng(it.latitude, it.longitude)
                moveCamera(currentLocation!!)
            } ?: run {
                val defaultLocation = LatLng(-17.783616, -63.182084)
                moveCamera(defaultLocation)
                Toast.makeText(requireContext(), "No se pudo obtener la ubicación actual", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun moveCamera(location: LatLng) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
        updateLocationInfo(location)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}