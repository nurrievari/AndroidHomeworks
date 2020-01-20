package ru.itis.homework

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.coroutines.*

class TaskDetailsFragment : Fragment() {

    private lateinit var taskDao: TaskDao
    private var task: Task? = null
    private var taskLoader: Deferred<Task>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) taskLoader = GlobalScope.async(Dispatchers.IO) {
            taskDao.getById(arguments!!.getInt(ARG_TASK_ID))
        }
        setHasOptionsMenu(true)
        activity?.title = getString(R.string.title_task_details)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        taskDao = AppDatabase(context).taskDao()
        taskLoader?.start()
    }

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = inflater.inflate(R.layout.fragment_task_details, container, false)?.apply {
        task = taskLoader?.getCompleted()
        task?.apply {
            et_task_details_title.setText(title)
            et_task_details_date.setText(date.toString())
            et_task_details_desc.setText(description)
            cb_task_details_is_done.isChecked = isDone
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_task_details, menu)
        menu.findItem(R.id.action_done).setOnMenuItemClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                view?.apply {
                    val title = et_task_details_title.text.toString()
                    val date = Date(et_task_details_date.text.toString())
                    val desc = et_task_details_desc.text.toString()
                    val isDone = cb_task_details_is_done.isChecked
                    if (task == null) {
                        task = Task(0, title, desc, date, isDone)
                        taskDao.insert(task!!)
                    } else {
                        task!!.apply {
                            this.title = title
                            description = desc
                            this.date = date
                            this.isDone = isDone
                        }
                        taskDao.update(task!!)
                    }
                }
            }
            activity?.onBackPressed()

            true
        }
    }

    companion object {
        private const val ARG_TASK_ID = "taskId"

        fun newInstance(taskId: Int?) = TaskDetailsFragment().apply {
            if (taskId != null) arguments = Bundle().apply {
                putInt(ARG_TASK_ID, taskId)
            }
        }
    }

}
