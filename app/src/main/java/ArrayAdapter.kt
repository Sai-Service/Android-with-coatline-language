import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class HintSpinnerAdapter(context: Context, resource: Int, objects: List<String>) :
    ArrayAdapter<String>(context, resource, objects) {

    override fun isEnabled(position: Int): Boolean {
        return position != 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = super.getDropDownView(position, convertView, parent)

        val textView = view as TextView
        if (position == 0) {
            textView.setTextColor(context.resources.getColor(android.R.color.darker_gray))
        } else {
            textView.setTextColor(context.resources.getColor(android.R.color.black))
        }

        return view
    }
}



