import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.apinew.R

class GridItemAdapter(context: Context, private val data: List<Pair<String, String>>) :
    ArrayAdapter<Pair<String, String>>(context, 0, data) {

    private val ITEM_TYPE_DATA = 0
    private val ITEM_TYPE_DIVIDER = 1

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        return if (getItemViewType(position) == ITEM_TYPE_DIVIDER) {
            inflater.inflate(R.layout.grid_divider_item, parent, false)
        } else {
            val view = convertView ?: inflater.inflate(R.layout.grid_item, parent, false)

            val keyTextView = view.findViewById<TextView>(R.id.keyTextView)
            val valueTextView = view.findViewById<TextView>(R.id.valueTextView)

            val item = getItem(position)
            keyTextView.text = "${item?.first}:"
            valueTextView.text = item?.second

            view
        }
    }

    override fun getViewTypeCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 12 == 11) ITEM_TYPE_DIVIDER else ITEM_TYPE_DATA
    }

    override fun getCount(): Int {
        return data.size + data.size / 11
    }

    override fun getItem(position: Int): Pair<String, String>? {
        return if (position % 12 == 11) null else data[position - position / 12]
    }
}
