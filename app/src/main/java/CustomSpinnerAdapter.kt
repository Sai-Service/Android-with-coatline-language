import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckedTextView
import com.example.apinew.R

class CustomSpinnerAdapter(
    private val context: Context,
    private val items: List<String>
) : ArrayAdapter<String>(context, R.layout.spinner_item_with_checkbox, items) {

    private val selectedItems = mutableSetOf<Int>()  // Stores selected positions

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createView(position, convertView, parent)
    }

    private fun createView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.spinner_item_with_checkbox, parent, false)
        val checkBox = view.findViewById<CheckedTextView>(R.id.checkBoxItem)

        checkBox.text = items[position]

        // Ensure "Select Wash Stages" is not selectable
        if (position == 0) {
            checkBox.isEnabled = false
        } else {
            checkBox.isEnabled = true
            checkBox.isChecked = selectedItems.contains(position)
        }

        view.setOnClickListener {
            if (position != 0) {  // Prevent clicking on "Select Wash Stages"
                if (selectedItems.contains(position)) {
                    selectedItems.remove(position)
                } else {
                    selectedItems.add(position)
                }
                notifyDataSetChanged()
            }
        }

        return view
    }

    fun getSelectedItems(): List<String> {
        return selectedItems.map { items[it] }  // Return selected item names
    }
}
