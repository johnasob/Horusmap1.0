import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class RESTClient(private var apiurl: String) {

    private lateinit var deferred: Deferred<String>

    fun httpPutAsync(resource: String, data: String="") {

        deferred = GlobalScope.async {
            //Builds URL object
            val url = URL(apiurl + resource)

            //String to return response
            var response: String
            //Creates HTTP connection

            var conn = url.openConnection() as HttpURLConnection

            try {
                conn.readTimeout = 10000
                conn.connectTimeout = 15000
                conn.requestMethod = "PUT"
                conn.doInput = true
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                //Objects to manage data to server
                val outputStream: OutputStream = conn.getOutputStream()
                val outputStreamWriter = OutputStreamWriter(outputStream)
                val bufferedWriter = BufferedWriter(outputStreamWriter)

                bufferedWriter.write(data)
                bufferedWriter.flush()

                //Objects to manage response from server
                val inputStream: InputStream = conn.getInputStream()
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)

                response = bufferedReader.readLine()
                conn.disconnect()

            } catch (e: IOException) {
                response = "No internet connection---EPIC_FAIL"
            }


            response

        }
    }








    fun httpPostAsync(resource: String, data: String="") {

        deferred=  GlobalScope.async {
            //Builds URL object
            val url = URL(apiurl+resource)

            //String to return response
            var response: String
            //Creates HTTP connection
            try {
            var conn= url.openConnection() as HttpURLConnection


                conn.readTimeout = 10000
                conn.connectTimeout = 15000
                conn.requestMethod = "POST"
                conn.doInput = true
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                //Objects to manage data to server
                val outputStream: OutputStream = conn.getOutputStream()
                val outputStreamWriter = OutputStreamWriter(outputStream)
                val bufferedWriter = BufferedWriter(outputStreamWriter)

                bufferedWriter.write(data)
                bufferedWriter.flush()

                //Objects to manage response from server
                val inputStream: InputStream = conn.getInputStream()
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)

                response = bufferedReader.readLine()
                conn.disconnect()
            }catch(e:IOException){
                response = "No internet connection---EPIC_FAIL"
            }


            response
        }



    }

    fun httpGetAsync(resource: String){


        deferred=GlobalScope.async{
            //Builds URL object
            val url = URL(apiurl+resource)

            //String to return response
            var response: String
            //Creates HTTP connection
            try {
                var conn = url.openConnection() as HttpURLConnection


                //Objects to manage response from server
                val inputStream: InputStream = conn.getInputStream()
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)

                response = bufferedReader.readLine()

                conn.disconnect()
            }catch(e:IOException){
                response = "ERROR 404"
            }
            response
        }

    }

    suspend  fun wait():String{
        return deferred.await()
    }
}