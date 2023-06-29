package com.example.baseproject
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textview.MaterialTextView
import kotlin.math.abs

class MainUserMenuActivity : AppCompatActivity() {
    private lateinit var userNameTextView: TextView
    private lateinit var userTypeTextView: TextView
    private lateinit var currentDayTextView: TextView
    private lateinit var hourlyScheduleListView: ListView

    private val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
    private var currentDayIndex = 0

    var userData: String? = null
    var allData: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_user_menu)

        userNameTextView = findViewById(R.id.userNameTextView)
        userTypeTextView = findViewById(R.id.userTypeTextView)
        currentDayTextView = findViewById(R.id.currentDayTextView)
        hourlyScheduleListView = findViewById(R.id.hourlyScheduleListView)

        userData = intent.getStringExtra("userData")
        allData = intent.getStringArrayListExtra("AllData")!!

        userNameTextView.text = getDataFromString(userData.toString())["id"]
        userTypeTextView.text = "Ученик"

        currentDayTextView.text = daysOfWeek[currentDayIndex]

        val scheduleAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            getScheduleForDay(currentDayIndex)
        )
        hourlyScheduleListView.adapter = scheduleAdapter

        hourlyScheduleListView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedItem = parent.getItemAtPosition(position) as String
                showTaskInputDialog(selectedItem)
            }

        val previousDayTextView: TextView = findViewById(R.id.previousDayTextView)
        previousDayTextView.setOnClickListener {
            showPreviousDaySchedule()
        }

        val nextDayTextView: TextView = findViewById(R.id.nextDayTextView)
        nextDayTextView.setOnClickListener {
            showNextDaySchedule()
        }
        update()
    }

    private fun getScheduleForDay(dayIndex: Int): MutableList<String> {
        // TODO: Implement your logic to retrieve the schedule for the given day
        // In this example, we simply return a hardcoded schedule for demonstration purposes
        return mutableListOf(
            "8:00 - Свободно",
            "9:00 - Свободно",
            "10:00 - Свободно",
            "11:00 - Свободно",
            "12:00 - Свободно",
            "13:00 - Свободно",
            "14:00 - Свободно",
            "15:00 - Свободно",
            "16:00 - Свободно",
            "17:00 - Свободно",
            "18:00 - Свободно",
            "19:00 - Свободно",
            "20:00 - Свободно",
            "21:00 - Свободно"
        )
    }

    private fun showPreviousDaySchedule() {
        if (currentDayIndex > 0) {
            currentDayIndex--
            currentDayTextView.text = daysOfWeek[currentDayIndex]
            val scheduleAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                getScheduleForDay(currentDayIndex)
            )
            hourlyScheduleListView.adapter = scheduleAdapter
        } else if (currentDayIndex == 0) {
            currentDayIndex = daysOfWeek.size - 1
            currentDayTextView.text = daysOfWeek[currentDayIndex]
            val scheduleAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                getScheduleForDay(currentDayIndex)
            )
            hourlyScheduleListView.adapter = scheduleAdapter

        }
        update()
    }

    private fun showNextDaySchedule() {
        if (currentDayIndex < daysOfWeek.size - 1) {
            currentDayIndex++
            currentDayTextView.text = daysOfWeek[currentDayIndex]
            val scheduleAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                getScheduleForDay(currentDayIndex)
            )
            hourlyScheduleListView.adapter = scheduleAdapter
        } else if (currentDayIndex == daysOfWeek.size - 1) {
            currentDayIndex = 0
            currentDayTextView.text = daysOfWeek[currentDayIndex]
            val scheduleAdapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                getScheduleForDay(currentDayIndex)
            )
            hourlyScheduleListView.adapter = scheduleAdapter
        }
        update()
    }

    private fun showTaskInputDialog(selectedItem: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_task_output)
        dialog.setTitle("Добавить задание")

        val taskEditText: MaterialTextView = dialog.findViewById(R.id.taskOutput)

        val okButton: Button = dialog.findViewById(R.id.saveButton)
        val cancelButton: Button = dialog.findViewById(R.id.cancelButton)

        val time = selectedItem.substringBefore(" -")

        val regex = Regex("taskB&.*?&date&$currentDayIndex&time&$time&")
        val matchResult = regex.find(userData.toString())
        Log.d("blocksTest", time +" "+ regex.toString())
        if (matchResult != null) {
            val taskText = matchResult.groupValues[0].substring(matchResult.groupValues[0].indexOf("taskB&") + 6, matchResult.groupValues[0].indexOf("&date"))
            taskEditText.setText(taskText)
        }

        okButton.setOnClickListener {
            dialog.dismiss()
        }


        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun update() {
        val teacherId = (getDataFromString(userData.toString())["teacherId"])
        val scheduleAdapter = hourlyScheduleListView.adapter as ArrayAdapter<String>
        //val scheduleAdapter = hourlyScheduleListView.adapter as? ArrayAdapter<String>
        scheduleAdapter?.let {
            for (i in 0 until it.count) {
                val item = it.getItem(i)
                if (item != null && item.endsWith(" - Свободно")) {
                    val time = item.substringBefore(" -")
                    val occupiedItem = userData
                    if (occupiedItem != null) {
                        if (occupiedItem.contains(time) && occupiedItem.contains(currentDayIndex.toString()) && (abs(occupiedItem.indexOf(time) - occupiedItem.indexOf(currentDayIndex.toString())) < 9)) {
                            val taskTextStartIndex = occupiedItem.indexOf("&taskB") + "&taskB".length
                            val taskTextEndIndex = occupiedItem.indexOf("&", taskTextStartIndex)
                            val taskText = occupiedItem.substring(taskTextStartIndex, taskTextEndIndex)
                            val studentNameStartIndex = occupiedItem.indexOf("&id&") + "&id&".length
                            val studentNameEndIndex = occupiedItem.indexOf("&", studentNameStartIndex)
                            val studentName = occupiedItem.substring(studentNameStartIndex, studentNameEndIndex)
                            it.remove(item)
                            it.insert("$time - Занято, $teacherId", i)
                        }
                    }
                }
            }
        }
        scheduleAdapter.notifyDataSetChanged() // Обновление списка после внесения изменени
    }

    fun getDataFromString(dataString: String): Map<String, String> {
        val keyValuePairs = dataString.split("&") // Разделение строки по символу '&'
        val dataMap = mutableMapOf<String, String>()

        var currentIndex = 0
        while (currentIndex < keyValuePairs.size) {
            if (keyValuePairs[currentIndex].isNotEmpty() && currentIndex + 1 < keyValuePairs.size) {
                val key = keyValuePairs[currentIndex].trimStart('&') // Удаление символа '&' из начала ключа
                val value = keyValuePairs[currentIndex + 1]
                dataMap[key] = value
                currentIndex += 2
            } else {
                currentIndex++
            }
        }
        return dataMap
    }
}
