package edu.cit.cortes.stockmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.cit.cortes.stockmate.model.SaleResponse

class SaleAdapter(
    private var sales: List<SaleResponse>
) : RecyclerView.Adapter<SaleAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sale, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(sales[position])
    }

    override fun getItemCount(): Int = sales.size

    fun submitList(newSales: List<SaleResponse>) {
        sales = newSales
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameText = itemView.findViewById<TextView>(R.id.saleProductName)
        private val totalText = itemView.findViewById<TextView>(R.id.saleTotal)
        private val quantityText = itemView.findViewById<TextView>(R.id.saleQuantity)
        private val metaText = itemView.findViewById<TextView>(R.id.saleMeta)

        fun bind(item: SaleResponse) {
            nameText.text = item.productName
            totalText.text = "₱%.2f".format(item.totalAmount)
            quantityText.text = item.quantitySold.toString()
            val recordedBy = item.recordedByName ?: "—"
            val date = formatDate(item.createdAt)
            metaText.text = "Recorded by $recordedBy  •  $date"
        }

        // Backend sends ISO LocalDateTime (e.g. 2026-07-21T14:30:05); show date + HH:mm.
        private fun formatDate(raw: String?): String {
            if (raw.isNullOrBlank()) return "—"
            val trimmed = raw.substringBefore('.')
            val parts = trimmed.split('T')
            if (parts.size < 2) return trimmed
            val date = parts[0]
            val time = parts[1].take(5)
            return "$date $time"
        }
    }
}
