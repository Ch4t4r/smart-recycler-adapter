package smartrecycleradapter.feature

import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_filter_item.searchView
import kotlinx.android.synthetic.main.activity_filter_item.toolbarProgressBar
import kotlinx.android.synthetic.main.activity_simple_item.recyclerView
import smartadapter.SmartRecyclerAdapter
import smartadapter.diffutil.DiffUtilExtension
import smartadapter.filter.FilterExtension
import smartadapter.get
import smartadapter.viewevent.listener.OnClickEventListener
import smartrecycleradapter.R
import smartrecycleradapter.feature.simpleitem.SimpleItemViewHolder
import smartrecycleradapter.utils.showToast
import smartrecycleradapter.viewholder.SmallHeaderViewHolder
import kotlin.random.Random

/*
 * Created by Manne Öhlund on 2019-08-11.
 * Copyright (c) All rights reserved.
 */

class SimpleFilterActivity : BaseSampleActivity() {

    override val contentView: Int = R.layout.activity_filter_item
    lateinit var smartAdapter: SmartRecyclerAdapter

    private val predicate = object : DiffUtilExtension.DiffPredicate<Any> {
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.title = "Simple Filter"

        val items = (0..100000).map { Random.nextInt(100, 10000) }.toMutableList()

        smartAdapter = SmartRecyclerAdapter.items(items)
            .map(String::class, SmallHeaderViewHolder::class)
            .map(Int::class, SimpleFilterItemViewHolder::class)
            .add(OnClickEventListener {
                showToast("Nr ${it.adapter.getItem(it.position)} @ pos ${it.position + 1}")
            })
            .add(
                FilterExtension(
                    filterPredicate = { item, constraint ->
                        when (item) {
                            is Int -> item.toString().contains(constraint)
                            else -> true
                        }
                    }
                ) {
                    toolbarProgressBar.visibility = if (it) View.VISIBLE else View.GONE
                }
            )
            .into(recyclerView)

        // Set search view filter
        searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
    }

    fun filter(query: String?) {
        val filterExtension: FilterExtension = smartAdapter.get()

        filterExtension.filter(lifecycleScope, query, autoSetNewItems = true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.refresh, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.refresh -> refresh()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refresh() {
        val items = (0..10000).map { Random.nextInt(100, 10000) }.toMutableList()
        smartAdapter.setItems(items)
    }
}

class SimpleFilterItemViewHolder(view: ViewGroup) : SimpleItemViewHolder(view) {

    init {
        (itemView as TextView).typeface = Typeface.MONOSPACE
    }

    override fun bind(item: Int) {
        (itemView as TextView).text = "$item"
    }
}
