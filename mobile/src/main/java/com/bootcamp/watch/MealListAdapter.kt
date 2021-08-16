package com.bootcamp.watch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rubdev.shared.Meal

class MealListAdapter(
    private val meals: MutableList<Meal>,
    private val callback: Callback?
) : RecyclerView.Adapter<MealListAdapter.MealViewHolder>() {

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = meals[position]
        holder.title.text = meal.title
        holder.ingredients.text = meal.ingredients.joinToString(separator = ", ")
        holder.calories.text = meal.calories.toString()
        holder.star.visibility = if (meal.favorited) View.VISIBLE else View.INVISIBLE
        holder.itemView.setOnClickListener {
            callback?.mealClicked(meal)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun getItemCount() = meals.size

    fun updateMeal(meal: Meal) {
        for ((index, value) in meals.withIndex()) {
            if (value.title == meal.title) {
                meals[index] = meal
            }
        }

        notifyDataSetChanged()
    }

    class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView
        val ingredients: TextView
        val calories: TextView
        val star: ImageView

        init {
            title = view.findViewById(R.id.title)
            ingredients = view.findViewById(R.id.ingredients)
            calories = view.findViewById(R.id.calories)
            star = view.findViewById(R.id.star)
        }
    }

    interface Callback {
        fun mealClicked(meal: Meal)
    }
}