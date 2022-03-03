package dev.abhattacharyea.amadeuspoi.ui.list

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.abhattacharyea.amadeuspoi.PlacesListAdapter
import dev.abhattacharyea.amadeuspoi.PlacesManager
import dev.abhattacharyea.amadeuspoi.databinding.FragmentListBinding
import dev.abhattacharyea.amadeuspoi.ui.FilterDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class ListFragment : Fragment(), FilterDialogFragment.FilterDialogListener {

    private var _binding: FragmentListBinding? = null

    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    private var lastKnownLocation: Location? = null
    private lateinit var placesManager: PlacesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.button.setOnClickListener {
            FilterDialogFragment(this).show(requireActivity().supportFragmentManager, "filters")
        }

        recyclerView = binding.placesList
        val recyclerViewManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = recyclerViewManager
        recyclerView.visibility = View.GONE

        placesManager = PlacesManager(requireContext())

        getPlaces()
        return root
    }

    @SuppressLint("MissingPermission")
    private fun getPlaces(categories: List<String>? = null) {
        try {
            recyclerView.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            placesManager.getDeviceLocation().addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    lastKnownLocation = task.result
                    if (lastKnownLocation != null) {

                        CoroutineScope(Dispatchers.Main + SupervisorJob()).launch {
                            val places = PlacesManager(requireContext())
                                .fetchPlaces(
                                    lastKnownLocation!!.latitude,
                                    lastKnownLocation!!.longitude,
                                    2,
                                    categories
                                )
                            places?.let {
                                val placesListAdapter = PlacesListAdapter(it)
                                recyclerView.adapter = placesListAdapter
                                recyclerView.visibility = View.VISIBLE
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDialogPositiveClick(dialog: DialogFragment, categories: List<String>) {
        getPlaces(categories)
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {

    }
}