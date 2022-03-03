package dev.abhattacharyea.amadeuspoi.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import dev.abhattacharyea.amadeuspoi.R
import java.lang.IllegalStateException

class FilterDialogFragment(fragment: Fragment): DialogFragment() {

    private val listener: FilterDialogListener = fragment as FilterDialogListener
    private val selectedItems = ArrayList<String>()

    interface FilterDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, categories: List<String>)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val choices = resources.getStringArray(R.array.categories_array)

            builder.setTitle(R.string.filter_message)
                .setMultiChoiceItems(R.array.categories_array,
                    null,
                    DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(choices[which])
                        } else if (selectedItems.contains(choices[which])) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(choices[which])
                        }
                    })
                .setPositiveButton(R.string.apply,
                    DialogInterface.OnClickListener {dialog, id ->
                        listener.onDialogPositiveClick(this, selectedItems)
                    })
                .setNegativeButton(R.string.Cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        listener.onDialogNegativeClick(this)
                    })

            builder.create()
        } ?: throw  IllegalStateException("Got a null activity")
    }

    companion object {
        private val TAG = FilterDialogFragment::class.java.simpleName
    }
}