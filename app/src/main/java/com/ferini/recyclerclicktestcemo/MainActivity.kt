package com.ferini.recyclerclicktestcemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), Adapter.ItemClickListener,
    RecyclerView.OnItemTouchListener {
    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) = Unit

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        Log.d("Testing", "onTouch: $e")

        if (e.action != MotionEvent.ACTION_UP) return false

        printView(rv, 0)

        val under = rv.findChildViewUnder(e.x, e.y)
        Log.d("Testing", "under: $under")

        return false
    }

    private fun printView(view: View, prefix: Int) {
        Log.d("Testing", "${"  ".repeat(prefix)}$view")

        if (view !is ViewGroup) return

        for (i in 0 until view.childCount) {
            printView(view.getChildAt(i), prefix + 1)
        }
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) = Unit

    companion object {
        val idlingResource = CountingIdlingResource("idling-resource")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = Adapter(LayoutInflater.from(this), this)
        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        recycler.addOnItemTouchListener(this)

        idlingResource.increment()
        recycler.postDelayed({
            adapter.setItems(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10))

            recycler.postDelayed({
                adapter.addSpecial()
                recycler.scrollToPosition(0)
                idlingResource.decrement()
            }, 100)
        }, 100)
    }

    override fun regularClicked(num: Int) {
        Log.d("Testing", "regularClicked: $num")
        checking_text.text = "regular_action_$num"
    }

    override fun specialButtonClicked() {
        Log.d("Testing", "specialButtonClicked")
        checking_text.text = "special_button_action"
    }

    override fun specialClicked() {
        Log.d("Testing", "specialClicked")
        checking_text.text = "special_action"
    }
}

class Adapter(val inflater: LayoutInflater, val clickListener: ItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var hasSpecial = false

    interface ItemClickListener {
        fun regularClicked(num: Int)
        fun specialClicked()
        fun specialButtonClicked()
    }

    private val items = ArrayList<Int>()

    override fun getItemViewType(position: Int): Int {
        return if (hasSpecial && position == 0) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> RegularHolder(
                inflater.inflate(R.layout.item_regular, parent, false),
                clickListener
            )
            else -> SpecialHolder(
                inflater.inflate(R.layout.item_special, parent, false),
                clickListener
            )

        }
    }

    override fun getItemCount(): Int {
        return if (!hasSpecial) items.size else items.size + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            holder is RegularHolder -> holder.bind(if (hasSpecial) items[position - 1] else items[position])
            holder is SpecialHolder -> holder.bind()
        }
    }

    fun setItems(items: List<Int>) {
        this.items.addAll(items)
        notifyItemRangeInserted(0, items.size)
    }

    fun addSpecial() {
        hasSpecial = true
        notifyItemInserted(0)
    }

    class RegularHolder(itemView: View, val itemClickListener: ItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val text: TextView = itemView.findViewById(R.id.item_text)
        val innerLayout: View = itemView.findViewById(R.id.inner_layout_regular)

        fun bind(num: Int) {
            text.text = "Item #$num"
            innerLayout.setOnClickListener {
                printClickedViewInfo(it)
                itemClickListener.regularClicked(num)
            }
        }

    }

    class SpecialHolder(itemView: View, val itemClickListener: ItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val innerLayout: View = itemView.findViewById(R.id.inner_layout_special)
        val button: View = itemView.findViewById(R.id.special_button)

        fun bind() {
            innerLayout.setOnClickListener {
                printClickedViewInfo(it)
                itemClickListener.specialClicked()
            }
            button.setOnClickListener {
                printClickedViewInfo(it)
                itemClickListener.specialButtonClicked()
            }
        }

    }

    companion object {
        fun printClickedViewInfo(view: View) {
            val loc = IntArray(2)
            view.getLocationOnScreen(loc)
            Log.d(
                "Testing",
                "clicked on $view, absolute location " +
                        "${loc.toList()} - [${loc[0] + view.width}, ${loc[1] + view.height}]"
            )
        }
    }
}