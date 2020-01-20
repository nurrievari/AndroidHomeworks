package ru.itis.homework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragment = TaskListFragment(object : TaskInterface {
            override fun showDetails(taskId: Int?) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, TaskDetailsFragment.newInstance(taskId))
                    .addToBackStack(null)
                    .commit()
            }
        })
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

}
