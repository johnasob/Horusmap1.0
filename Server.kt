//Horus Map Server, developed by Elite Engineering design.
//This file contains an Http Server and a Mongo DB database.

package com.horus

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.gson.*
import io.ktor.features.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import org.litote.kmongo.*

data class User(var name:String, var password:String, var email:String, var vision:String , val _id:String? = null )


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

    val client = KMongo.createClient()

    val database = client.getDatabase("HorusDB")

    val col = database.getCollection<User>()


@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

   
    install(Authentication) {
        basic("myBasicAuth") {
            realm = "Ktor Server"
            validate {
                val credentials = col.findOne(User::name eq it.name, User::password eq it.password)

                if (credentials == null)
                    null
                else
                    UserIdPrincipal(credentials._id.toString())
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }

        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    routing {

        var newUser:User

        get("/") {
            call.respondText("Connected to Horus-Server!     By: Elite Engineering Design", contentType = ContentType.Text.Plain)
        }


        authenticate("myBasicAuth") {
            get("/auth") {
                val principal = call.principal<UserIdPrincipal>()!!
                call.respondText(" ${principal.name}")
            }
        }

        //http://localhost:8080/user/info?auth=616459a909080c1868ec65d8
        get("/user/info"){
            val userId = call.request.queryParameters["auth"]
            val myUser = col.findOne(User::_id eq userId)

            if(myUser!= null){
                call.respond(mapOf("Name: " to (myUser.name) , "Password: " to (myUser.password)    , "Email: " to (myUser.email) , "Vision: " to (myUser.vision) ))

            }else{
                call.respondText ("404")
            }


        }


        //curl -X POST -v -d "user=John&password=3125&email=jasolanob@uqvirtual.edu.co&vision=low" http://localhost:8080/create/user
        // Apikey: 616459a909080c1868ec65d8

        post("/create/user"){
            val parameters: Parameters = call.receiveParameters()
            val name= parameters["user"]!!
            val pass= parameters["password"]!!
            val email= parameters["email"]!!
            val vision= parameters["vision"]!!

            if(col.findOne(User::name eq name)==null){
                newUser = User( name, pass ,email, vision )
                col.insertOne(newUser)
                call.respond(mapOf("User ID" to (newUser._id)))
                println("${call.request.httpMethod.value} Name is: $name, Pass: $pass, email: $email, vision: $vision and  ID is: ${newUser?._id}")
            }else {
                call.respondText("User already exists", contentType = ContentType.Text.Plain)
            }
        }

        //curl -X PUT -v -d "user=John&password=3125&email=jasolanob@uqvirtual.edu.co&vision=low" http://localhost:8080/update/user?auth=616459a909080c1868ec65d8
        // Apikey: 616459a909080c1868ec65d8

        put("/update/user") {

            val parameters: Parameters = call.receiveParameters()
            val userId = call.request.queryParameters["auth"]
            val name = parameters["user"]!!
            val pass = parameters["password"]!!
            val email = parameters["email"]!!
            val vision = parameters["vision"]!!

            val myUser = col.findOne(User::_id eq userId)
            println("Los datos originales son, name:  ${(myUser!!.name)}, password ${myUser.password}, email: ${myUser.email} y vision ${myUser.vision}")


            if (myUser != null) {
                myUser.name = name
                myUser.email = email
                myUser.password = pass
                myUser.vision = vision

                col.updateOneById(userId!!, myUser)

                call.respondText("200")
                println("Los datos actualizados son, name:  ${(myUser.name)}, password ${myUser.password}, email: ${myUser.email} y vision ${myUser.vision}")

            } else {

                call.respondText("Usuario no encontrado, intente nuevamente",contentType = ContentType.Text.Plain)
            }
        }


        //curl -X DELETE -v http://localhost:8080/delete/user?auth=616459a909080c1868ec65d8

        delete("/delete/user"){
            val userId = call.request.queryParameters["auth"]
            val myUser = col.findOne(User::_id eq userId)

            if(myUser!=null){
                col.deleteOne(User:: _id eq userId)
                call.respondText ("200", contentType = ContentType.Text.Plain)
            }else{
                call.respondText ("404", contentType = ContentType.Text.Plain)
            }
        }







            get("/json/gson") {
                call.respond(mapOf("Hello" to "world"))
            }

            get("/json/jackson") {
                call.respond(mapOf("hello" to "world"))
            }

            get("/echo") {

                //var user1 = User("John", 1001)
                val name = call.request.queryParameters["name"]
                val pass = call.request.queryParameters["password"]

                // call.respond(user1)
                call.respond(mapOf("name" to name, "password" to pass))
            }

        
        }
}
