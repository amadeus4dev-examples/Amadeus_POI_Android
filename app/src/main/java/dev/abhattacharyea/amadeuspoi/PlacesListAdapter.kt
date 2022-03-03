package dev.abhattacharyea.amadeuspoi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amadeus.android.domain.resources.Location


class PlacesListAdapter(private val places: List<Location>): RecyclerView.Adapter<PlacesListAdapter.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.places_name)
    }
    companion object {
        private val TAG = PlacesListAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.places_list_item, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.placeName.text = places[position].name
    }

    override fun getItemCount() = places.size
}