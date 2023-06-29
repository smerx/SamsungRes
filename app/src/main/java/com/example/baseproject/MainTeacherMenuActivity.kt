package com.example.baseproject

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.system.Os.remove
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlin.math.log

class MainTeacherMenuActivity : AppCompatActivity() {

    private lateinit var userNameTextView: TextView
    private lateinit var userTypeTextView: TextView
    private lateinit var currentDayTextView: TextView
    private lateinit var hourlyScheduleListView: ListView

    private val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
    private var currentDayIndex = 0

    private lateinit var fab: FloatingActionButton
    private lateinit var studentsIdList: List<String>
    var userData: String? = null
    var allData: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_teacher_menu)

        userData = intent.getStringExtra("userData")
        allData = intent.getStringArrayListExtra("AllData")!!

        fab = findViewById(R.id.fab)
        userNameTextView = findViewById(R.id.userNameTextView)
        userTypeTextView = findViewById(R.id.userTypeTextView)
        currentDayTextView = findViewById(R.id.currentDayTextView)
        hourlyScheduleListView = findViewById(R.id.hourlyScheduleListView)
        userNameTextView.text = getDataFromString(userData.toString())["id"]
        userTypeTextView.text = "Учитель"

        currentDayTextView.text = daysOfWeek[currentDayIndex]

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

        if (allData != null) {
            studentsIdList = allData
                .filter { it.contains("&isTeacher&false&") } // Фильтрация элементов с isTeacher=false
                .mapNotNull { element ->
                    val keyValuePairs = element.split("&") // Разделение строки по символу '&'
                    val idIndex = keyValuePairs.indexOf("id") // Поиск индекса элемента "id"
                    if (idIndex != -1 && idIndex < keyValuePairs.size - 1) {
                        keyValuePairs[idIndex + 1] // Получение следующего элемента после "id"
                    } else {
                        null
                    }
                }
        }

        fab.setOnClickListener {
            showUserSelectionDialog()
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

    private fun showUserSelectionDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_user_selection)
        dialog.setTitle("Выберите пользователя")

        val spinner: Spinner = dialog.findViewById(R.id.spinner)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            studentsIdList
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val addButton: Button = dialog.findViewById(R.id.addButton)
        val cancelButton: Button = dialog.findViewById(R.id.cancelButton)

        addButton.setOnClickListener {
            val selectedUser = spinner.selectedItem.toString()
            if ("&chooseStudent&$selectedUser&" !in userData.toString()) {
                writeToDatabase(userData.toString() + "chooseStudent&$selectedUser&", getDataFromString(userData.toString())["id"].toString())
                userData = userData.toString() + "chooseStudent&$selectedUser&"
                Toast.makeText(this, "$selectedUser добавлен", Toast.LENGTH_SHORT).show()
            }
            else {
                userData = userData?.replace("chooseStudent&$selectedUser&", "")
                writeToDatabase(userData.toString(), getDataFromString(userData.toString())["id"].toString())
                Toast.makeText(this, "$selectedUser удалён", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun writeToDatabase(word: String, id: String) {
        Firebase.database.getReference(id).setValue(word)
    }

    private fun showTaskInputDialog(selectedTimes: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_task_input)
        dialog.setTitle("Добавить задание")

        val spinner: Spinner = dialog.findViewById(R.id.spinnerStudent)
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            extractChooseStudents(userData.toString())
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val taskEditText: EditText = dialog.findViewById(R.id.taskEditText)
        val saveButton: Button = dialog.findViewById(R.id.saveButton)
        val cancelButton: Button = dialog.findViewById(R.id.cancelButton)

        saveButton.setOnClickListener {
            val task = taskEditText.text.toString()
            if (task.isNotEmpty()) {
                val selectedStudent = spinner.selectedItem.toString()
                Log.d("choose", selectedStudent)
                // Save the task for the selected user
                var selectedTime = selectedTimes.split(" ")[0].toString()
                Toast.makeText(this, "Задание сохранено для $selectedTime", Toast.LENGTH_SHORT).show()
                for ((index, value) in allData.withIndex()) {
                    if (value.startsWith("&id&$selectedStudent&")) {
                        writeToDatabase(value + "taskB&$task&date&$currentDayIndex&time&$selectedTime&teacherId&${getDataFromString(userData.toString())["id"]}&taskE&", selectedStudent)
                        allData[index] = value + "taskB&$task&date&$currentDayIndex&time&$selectedTime&taskE&"
                        replaceItemInListView(selectedTimes, "$selectedTime - Занято, $selectedStudent")
                        break // Для остановки после первого найденного элемента (если нужно)
                    }
                }

                dialog.dismiss()
            } else {
                Toast.makeText(this, "Введите задание", Toast.LENGTH_SHORT).show()
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    fun extractChooseStudents(input: String): List<String> {
        val chooseStudents = input.split("&chooseStudent&")
        return chooseStudents.subList(1, chooseStudents.size).map { it.trim('&') }
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

    private fun replaceItemInListView(oldItem: String, newItem: String) {
        val adapter = hourlyScheduleListView.adapter as ArrayAdapter<String>
        val count = adapter.count
        for (i in 0 until count) {
            val item = adapter.getItem(i)
            if (item == oldItem) {
                adapter.remove(item)
                adapter.insert(newItem, i)
                break // остановка после замены первого найденного элемента (если нужно)
            }
        }
        adapter.notifyDataSetChanged() // Обновление списка после внесения изменений
    }

    fun update() {
        val teacherId = (getDataFromString(userData.toString())["id"])
        val scheduleAdapter = hourlyScheduleListView.adapter as ArrayAdapter<String>
        //val scheduleAdapter = hourlyScheduleListView.adapter as? ArrayAdapter<String>
        scheduleAdapter?.let {
            for (i in 0 until it.count) {
                val item = it.getItem(i)
                if (item != null && item.endsWith(" - Свободно")) {
                    val time = item.substringBefore(" -")
                    val occupiedItem = allData.find { data ->
                        data.contains("&taskB") && data.contains("&taskE") && data.contains("&date&$currentDayIndex&time&$time&teacherId&$teacherId&")
                    }
                    if (occupiedItem != null) {
                        val taskTextStartIndex = occupiedItem.indexOf("&taskB") + "&taskB".length
                        val taskTextEndIndex = occupiedItem.indexOf("&", taskTextStartIndex)
                        val taskText = occupiedItem.substring(taskTextStartIndex, taskTextEndIndex)
                        val studentNameStartIndex = occupiedItem.indexOf("&id&") + "&id&".length
                        val studentNameEndIndex = occupiedItem.indexOf("&", studentNameStartIndex)
                        val studentName = occupiedItem.substring(studentNameStartIndex, studentNameEndIndex)
                        it.remove(item)
                        it.insert("$time - Занято, $studentName", i)
                    }
                }
            }
        }
        scheduleAdapter.notifyDataSetChanged() // Обновление списка после внесения изменени
    }



}
