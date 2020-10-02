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
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.*
import org.litote.kmongo.reactivestreams.KMongo
import com.mongodb.ConnectionString



fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 9090
    val client = KMongo.createClient().coroutine
    val database = client.getDatabase("shoppingList")
    val collection = database.getCollection<ShoppingListItem>()


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
                    call.respond(collection.find().toList())
                }
                post {
                    collection.insertOne(call.receive<ShoppingListItem>())
                    call.respond(HttpStatusCode.OK)
                }
                route("/{id}"){
                    delete(){
                        val id = call.parameters["id"]?.toInt()?:error("Invalid delete request")
                        collection.deleteOne(ShoppingListItem::id eq id)
                        call.respond(HttpStatusCode.OK)
                    }
                    get() {
                        val id = call.parameters["id"]?.toInt()?:error("Invalid get request")
                        call.respond(collection.find(ShoppingListItem::id eq id))
                    }
                }
            }
        }
    }.start(wait = true)

}