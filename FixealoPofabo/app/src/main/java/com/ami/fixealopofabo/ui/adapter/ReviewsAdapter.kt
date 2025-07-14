package com.ami.fixealopofabo.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ami.fixealopofabo.R
import com.ami.fixealopofabo.model.Review

class ReviewsAdapter(private var reviews: List<Review>) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val tvUser: TextView = v.findViewById(R.id.tvUser)
        private val tvRating: TextView = v.findViewById(R.id.tvRating)
        private val tvComment: TextView = v.findViewById(R.id.tvComment)

        fun bind(review: Review) {
            tvUser.text = "${review.user.name} ${review.user.lastName ?: ""}"
            tvRating.text = "⭐ ${review.rating.toInt()}/5"
            tvComment.text = review.comment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(reviews[position])
    }

    override fun getItemCount() = reviews.size

    fun updateReviews(newReviews: List<Review>) {
        reviews = newReviews
        notifyDataSetChanged()
    }
}