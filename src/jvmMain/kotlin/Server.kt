import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.serialization.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.*

object ShoppingList : IntIdTable() {
    val desc_id = integer("desc_id")
    val description = varchar("description", 50)
    val priority = integer("priority")
}
class ShoppingItem(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ShoppingItem>(ShoppingList)
    var description by ShoppingList.description
    var priority by ShoppingList.priority
}

fun main() {
    val database = Database.connect("jdbc:postgresql://localhost:5432/shoppinglist_table", driver = "org.postgresql.Driver",
            user = "api_user", password = "password")
    transaction(database){
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
            method(HttpMethod.Delete)
            anyHost()
        }
        install(Compression) {
            gzip()
        }
        routing {
            get("/"){
                call.respondText(
                    this::class.java.classLoader.getResource("index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }
            static("/"){
                resources("")
            }
            route(ShoppingListItem.path){
                get {
                    val items : ArrayList<ShoppingListItem> = arrayListOf()
                    transaction(database) {
                        ShoppingList.selectAll().forEach{
                            items.add(ShoppingListItem(it[ShoppingList.description],it[ShoppingList.priority]))}
                    }
                    call.respond(items)
                }
                post {
                    val received = call.receive<ShoppingListItem>()
                    transaction(database) {
                        ShoppingList.insertAndGetId{
                            it[desc_id] = received.id
                            it[description] = received.desc
                            it[priority] = received.priority
                        }
                    }
                    call.respond(HttpStatusCode.OK)
                }
                route("/{id}"){

                    delete(){
                        val id = call.parameters["id"]?.toInt()?:error("Invalid get request")
                        transaction(database) {
                            ShoppingList.deleteWhere{ShoppingList.desc_id eq id}
                        }
                        call.respond(HttpStatusCode.OK)
                    }
                    get() {
                        val id = call.parameters["id"]?.toInt()?:error("Invalid get request")
                        val items : ArrayList<ShoppingListItem> = arrayListOf()
                        transaction(database) {
                            ShoppingList.select{ShoppingList.desc_id eq id}.forEach{
                                items.add(ShoppingListItem(
                                        it[ShoppingList.description],
                                        it[ShoppingList.priority]
                                ))}
                        }
                        call.respond(items)
                    }

                    put(){
                        val id = call.parameters["id"]?.toInt()?:error("Invalid get request")
                        val received = call.receive<ShoppingListItem>()
                        transaction(database) {
                            ShoppingList.update({ShoppingList.desc_id eq id}){
                                it[desc_id] = received.id
                                it[ShoppingList.priority] = received.priority
                                it[ShoppingList.description] = received.desc
                            }
                        }
                        call.respond(HttpStatusCode.OK)
                    }
                }
            }
        }
    }.start(wait = true)

}