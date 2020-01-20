package ru.itis.homework

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView


class TaskListAdapter(
    private val onClickListener: (Task) -> Unit,
    private val onRemoveListener: (Task) -> Unit
) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return TaskViewHolder(inflater.inflate(
            R.layout.item_task_list_fragment, parent, false
        ))
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(task: Task) {
            itemView.apply {
                iv_item_state.setImageResource(
                    if (task.isDone) R.drawable.ic_done_primary else R.drawable.ic_planned
                )
                tv_item_title.text = task.title
                tv_item_date.text = task.date.toString()
                setOnClickListener { onClickListener(task) }
                ib_item_delete.setOnClickListener { onRemoveListener(task) }
            }
        }

    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Task, newItem: Task) = oldItem == newItem
    }
}
