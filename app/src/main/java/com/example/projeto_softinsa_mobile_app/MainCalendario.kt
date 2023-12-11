package com.example.projeto_softinsa_mobile_app

import android.R.color
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.text.style.LineBackgroundSpan
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.projeto_softinsa_mobile_app.API.Evento
import com.example.projeto_softinsa_mobile_app.API.Perfil
import com.example.projeto_softinsa_mobile_app.Des_tudo.imagem_calendario
import com.example.projeto_softinsa_mobile_app.Des_tudo.imagem_evento
import com.example.projeto_softinsa_mobile_app.login.Authorization
import com.example.projeto_softinsa_mobile_app.lvadapador.Lvadapador_calendario
import com.example.projeto_softinsa_mobile_app.lvadapador.Lvadapador_parceria
import com.google.android.material.navigation.NavigationView
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.*


class MainCalendario : AppCompatActivity() {


        private lateinit var toggle: ActionBarDrawerToggle
        private lateinit var drawerLayout: DrawerLayout
        private lateinit var navigationView: NavigationView
        private lateinit var listView: ListView
        private lateinit var adapter: Lvadapador_calendario
        private val decoratorDates = mutableListOf<CalendarDay>()
        private val calendarioArrayList = ArrayList<imagem_evento>()
        private var decorator: YourDecorator = YourDecorator(emptyList())
        var cargoId = 0
        var userId = 0

        class YourDecorator(dates: List<CalendarDay>) : DayViewDecorator {
                private val dates: HashSet<CalendarDay> = HashSet(dates)

                override fun shouldDecorate(day: CalendarDay): Boolean {
                        return dates.contains(day)
                }

                override fun decorate(view: DayViewFacade) {
                        view.addSpan(DotSpan(8f, Color.RED)) // Customize the dot size and color as desired
                        view.setDaysDisabled(false)
                }
        }


        private fun filterEventsByDate(selectedDate: Date) {
                val filteredEvents = ArrayList<imagem_evento>()

                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val selectedDateString = dateFormat.format(selectedDate)
                Log.i("Tag", "Selected Date: $selectedDateString")

                for (event in calendarioArrayList) {
                        val eventDateStr = event.dataInicio_img_evento

                        // Parse the eventDate string to a Date object
                        val eventDate = dateFormat.parse(eventDateStr)

                        // Format the eventDate back to a string for comparison (optional, for logging purposes)
                        val eventDateString = dateFormat.format(eventDate)


                        if (eventDateString == selectedDateString) {
                                filteredEvents.add(event)
                        }
                }

                // Pass the filtered events to your adapter and update the ListView
                val listView = findViewById<ListView>(R.id.lv_lista_Eventos)
                listView.adapter = Lvadapador_calendario(this, filteredEvents)
        }


        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContentView(R.layout.activity_main_calendario)
                val user = Perfil(this, null)
                cargoId = user.getStoredCargoId()
                val auth = Authorization(this, null)
                userId = auth.getUserId()

                listView = findViewById<ListView>(R.id.lv_lista_Eventos)
                adapter = Lvadapador_calendario(this, calendarioArrayList)
                listView.adapter = adapter
                val calendarView = findViewById<MaterialCalendarView>(R.id.calendarView)

                // ...
                calendarView.addDecorators(decorator)

                // ...

                // Inside the onSuccess callback after updating the calendarioArrayList, update the decoratorDates
                val eventoAPI = Evento(this, null)
                eventoAPI.listEventos(object : Evento.GetEventoCallback {
                        override fun onSuccess(eventos: List<imagem_evento>) {
                                calendarioArrayList.clear()
                                if (cargoId <= 2) {
                                        calendarioArrayList.addAll(eventos)
                                        Log.d("tag", eventos.toString())
                                } else {
                                        val filteredEventos = eventos.filter { it.userId_img_evento == userId }
                                        calendarioArrayList.addAll(filteredEventos)
                                        Log.d("tag", filteredEventos.toString())
                                }


                                decoratorDates.clear() // Clear the previous decoratorDates
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                                for (event in calendarioArrayList) {
                                        val date = dateFormat.parse(event.dataInicio_img_evento)
                                        val calendar = Calendar.getInstance()
                                        calendar.time = date
                                        val calendarDay = CalendarDay.from(
                                                calendar.get(Calendar.YEAR),
                                                calendar.get(Calendar.MONTH) + 1,
                                                calendar.get(Calendar.DAY_OF_MONTH)
                                        )

                                        Log.i("Tag", "Ano: ${calendar.get(Calendar.YEAR)}")
                                        Log.i("Tag", "Mês: ${calendar.get(Calendar.MONTH) + 1}")
                                        Log.i("Tag", "Dia: ${calendar.get(Calendar.DAY_OF_MONTH)}")
                                        val decorator = YourDecorator(
                                                calendarioArrayList.map { imagemEvento ->
                                                        CalendarDay.from(
                                                                calendar.get(Calendar.YEAR),
                                                                calendar.get(Calendar.MONTH) + 1,
                                                                calendar.get(Calendar.DAY_OF_MONTH)
                                                        )
                                                }
                                        )
                                }

                                // Recreate the decorator using the updated list of CalendarDay objects
                                calendarView.selectedDate = CalendarDay.today()
                                var date = CalendarDay.today()
                                val calendar = Calendar.getInstance()
                                calendar.set(date.year, date.month-1, date.day)
                                val selectedDateSql: java.sql.Date = Date(calendar.timeInMillis)
                                filterEventsByDate(selectedDateSql)
                                calendarView.addDecorators(decorator)

                                // Notify the adapter and update the ListView with the filtered events
                                adapter.notifyDataSetChanged()

                                calendarView.setOnDateChangedListener { widget, date, selected ->
                                        val calendar = Calendar.getInstance()
                                        calendar.set(date.year, date.month-1, date.day)
                                        val selectedDateSql: java.sql.Date = Date(calendar.timeInMillis)
                                        filterEventsByDate(selectedDateSql)
                                }
                        }

                        override fun onFailure(errorMessage: String) {
                                // Handle the API request failure
                                Toast.makeText(this@MainCalendario, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                })




                /*-----------------------------NAVGATION MENU BAR-----------------------------*/
                var drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
                navigationView = findViewById(R.id.nav_view)

                toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
                drawerLayout.addDrawerListener(toggle)
                toggle.syncState()

                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                val menuBtn = findViewById<ImageButton>(R.id.menu_btn)
                menuBtn.setOnClickListener {
                        drawerLayout.openDrawer(GravityCompat.START)
                }

                toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
                drawerLayout.addDrawerListener(toggle)
                toggle.syncState()
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                NavigationViewHelper.setupNavigationView(this, drawerLayout, navigationView)
        }

}