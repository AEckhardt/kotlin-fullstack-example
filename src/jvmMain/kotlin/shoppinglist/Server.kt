package shoppinglist

import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.gzip
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.serialization.json
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


class ShoppingItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ShoppingItem>(ShoppingList)
    var description by ShoppingList.description
    var priority by ShoppingList.priority
}
object ShoppingList : IntIdTable() {
    val desc_id = integer("desc_id")
    val description = varchar("description", 50)
    val priority = integer("priority")
}
fun main() {
    val databaseURL = System.getenv("JDBC_DATABASE_URL")?.toString()
            ?: "jdbc:postgresql://localhost:5432/shoppinglist_table"
    val user = System.getenv("JDBC_DATABASE_USERNAME")?.toString() ?: "api_user"
    val password = System.getenv("JDBC_DATABASE_PASSWORD")?.toString() ?: "password"

    val database = Database.connect(
            databaseURL,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
    )
    transaction(database) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(ShoppingList)
    }
    val port = System.getenv("PORT")?.toInt() ?: 9090

    embeddedServer(Netty, port) {
        install(ContentNegotiation) {
            json()
        }
        install(CORS) {
            method(HttpMethod.Get)
            method(HttpMethod.Post)
            method(HttpMethod.Put)
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        routing {
            get("/") {
                call.respondText(
                        this::class.java.classLoader.getResource("index.html")!!.readText(),
                        ContentType.Text.Html
                )
            }
            static("/") {
                resources("")
            }
            route(ShoppingListItem.path) {
                get {
                    val items: ArrayList<ShoppingListItem> = arrayListOf()
                    transaction(database) {
                        ShoppingList.selectAll().forEach {
                            items.add(ShoppingListItem(it[ShoppingList.description], it[ShoppingList.priority]))
                        }
                    }
                    call.respond(items)
                }
                post {
                    val received = call.receive<ShoppingListItem>()
                    transaction(database) {
                        ShoppingList.insertAndGetId {
                            it[desc_id] = received.id
                            it[description] = received.desc
                            it[priority] = received.priority
                        }
                    }
                    call.respond(HttpStatusCode.OK)
                }
                route("/{id}") {

                    delete {
                        val id = call.parameters["id"]?.toInt() ?: error("Invalid get request")
                        transaction(database) {
                            ShoppingList.deleteWhere { ShoppingList.desc_id eq id }
                        }
                        call.respond(HttpStatusCode.OK)
                    }
                    get {
                        val id = call.parameters["id"]?.toInt() ?: error("Invalid get request")
                        val items: ArrayList<ShoppingListItem> = arrayListOf()
                        transaction(database) {
                            ShoppingList.select { ShoppingList.desc_id eq id }.forEach {
                                items.add(ShoppingListItem(
                                        it[ShoppingList.description],
                                        it[ShoppingList.priority]
                                ))
                            }
                        }
                        call.respond(items)
                    }

                    put {
                        val id = call.parameters["id"]?.toInt() ?: error("Invalid get request")
                        val received = call.receive<ShoppingListItem>()
                        transaction(database) {
                            ShoppingList.update({ ShoppingList.desc_id eq id }) {
                                it[desc_id] = received.id
                                it[priority] = received.priority
                                it[description] = received.desc
                            }
                        }
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }.start(wait = true)

}