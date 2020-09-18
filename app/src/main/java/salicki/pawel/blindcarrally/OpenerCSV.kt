package salicki.pawel.blindcarrally

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

object OpenerCSV {
    private const val TEXT_KEY = 0
    private const val ENGLISH_KEY = 1
    private const val POLISH_KEY = 2

    fun readData(resource: Int, languageTTS: LanguageTTS) : HashMap<String, String> {
        var line: String?

        var texts : HashMap<String, String> = HashMap()

        val inputStream: InputStream? = Settings.CONTEXT?.resources?.openRawResource(resource)
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))

        reader.readLine()

        line = reader.readLine()

        while (line != null) {
            val tokens = line.split(";")
            if (tokens.isNotEmpty()) {

                if(languageTTS == LanguageTTS.ENGLISH){
                    texts[tokens[TEXT_KEY]] = tokens[ENGLISH_KEY]
                }
                else{
                    texts[tokens[TEXT_KEY]] = tokens[POLISH_KEY]
                }
            }

            line = reader.readLine()
        }

        return texts
    }
}