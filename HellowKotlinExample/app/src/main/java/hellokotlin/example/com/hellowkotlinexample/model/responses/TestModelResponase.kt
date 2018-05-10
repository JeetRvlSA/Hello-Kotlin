package hellokotlin.example.com.hellowkotlinexample.model.responses

import com.google.gson.annotations.SerializedName
import java.util.ArrayList

class TestModelResponase: BaseResponse() {


    @SerializedName("test")
    var contacts: ArrayList<Model>? = null
    val contact = Model()

    class Model {

        @SerializedName("name")
        var name: String? = null

        @SerializedName("value1")
        var value1: String? = null

        @SerializedName("value2")
        var value2: String? = null

    }

}