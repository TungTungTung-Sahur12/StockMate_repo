package edu.cit.cortes.stockmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(private val products: List<ProductItem>) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emojiText = itemView.findViewById<TextView>(R.id.productEmoji)
        private val nameText = itemView.findViewById<TextView>(R.id.productName)

        fun bind(item: ProductItem) {
            emojiText.text = item.emoji
            nameText.text = item.name
        }
    }
}

data class ProductItem(val emoji: String, val name: String)
