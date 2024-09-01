import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlin.system.measureTimeMillis

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
suspend fun main() {
    val persons = listOf(
        Person("Иван", "Преподаватель"),
        Person("Ольга", "Разработчик"),
        Person("Роман", "Стажер"),
        Person("Виталий", "Стажер")
    )
    val phonesList: MutableList<String> = mutableListOf()
    val personInfo: MutableList<String> = mutableListOf()
    val time = measureTimeMillis {
        withContext(newSingleThreadContext("phone_thread_context")) {
            launch {
                getPhoneFlow(persons.size).collect { number ->
                    phonesList.add(number)
                    println("Добавлен номер $number в список номеров")

                }
            }
            launch {
                getPersonsFlow(persons).collect { i ->
                    println("Добавлен пользователь: $i")
                    personInfo.add("$i")
                }
            }

        }
    }
    println("Общее затраченное время: $time мс")
    for (i in personInfo.indices) {
        personInfo[i] = personInfo[i] + ", ${phonesList[i]}"
    }
    /**
     * Через .zip  не получилось объединить
     * пробовал так:
     * personInfo.zip(phonesList){a,b -> "$a, $b"}
     */
    println(personInfo)
}

fun getPersonsFlow(list: List<Person>) = list.asFlow().onEach { delay(1000L) }
fun getPhoneFlow(count: Int) = flow {
    for (i in 1..count) {
        delay(1000L)
        val number = randomPhoneNumber()
        println("Сгенерирован номер: $number")
        emit(number)
    }
}

fun randomPhoneNumber(): String = "+7917${(1000000..9999999).random()}"

data class Person(private val name: String = "Имя не указано", private val role: String = "Нет должности") {
    override fun toString(): String {
        return "Пользователь: $name, $role"
    }
}