package edu.cit.cortes.stockmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import edu.cit.cortes.stockmate.model.ProductResponse

class ProductAdapter(
    private var products: List<ProductResponse>,
    private val onDeleteClick: (ProductResponse) -> Unit,
    private val showDelete: Boolean
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position], onDeleteClick, showDelete)
    }

    override fun getItemCount(): Int = products.size

    fun submitList(newProducts: List<ProductResponse>) {
        products = newProducts
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val placeholderText = itemView.findViewById<TextView>(R.id.productPlaceholder)
        private val nameText = itemView.findViewById<TextView>(R.id.productName)
        private val priceText = itemView.findViewById<TextView>(R.id.productPrice)
        private val quantityText = itemView.findViewById<TextView>(R.id.productQuantity)
        private val lowStockBadge = itemView.findViewById<TextView>(R.id.lowStockBadge)
        private val deleteButton = itemView.findViewById<MaterialButton>(R.id.deleteProductButton)

        fun bind(item: ProductResponse, onDeleteClick: (ProductResponse) -> Unit, showDelete: Boolean) {
            placeholderText.text = item.category.replace("_", " ").uppercase()
            nameText.text = item.name
            priceText.text = "Price: ₱%.2f".format(item.price)
            quantityText.text = "Qty: ${item.quantity}"

            lowStockBadge.visibility = if (InventoryUiUtils.isLowStock(item.quantity, item.lowStockThreshold)) {
                View.VISIBLE
            } else {
                View.GONE
            }

            deleteButton.visibility = if (showDelete) View.VISIBLE else View.GONE
            deleteButton.setOnClickListener { onDeleteClick(item) }
        }
    }
}
