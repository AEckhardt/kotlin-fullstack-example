package shoppinglist

import react.*
import react.dom.*
import kotlinx.html.js.*
import kotlinx.html.InputType
import org.w3c.dom.events.Event
import org.w3c.dom.HTMLInputElement
import styled.*
import kotlinx.css.*


external interface InputProps : RProps {
    var onSubmit: (String) -> Unit
}

val InputComponent = functionalComponent<InputProps> { props ->
    val (text, setText) = useState("")

    val submitHandler: (Event) -> Unit = {
        it.preventDefault()
        setText("")
        props.onSubmit(text)
    }

    val changeHandler: (Event) -> Unit = {
        val value = (it.target as HTMLInputElement).value
        setText(value)
    }

    form {
        attrs.onSubmitFunction = submitHandler
        h2{
            +"Add new Item"
        }
        styledInput(InputType.text) {
            css{
                    padding(10.px)
                    width = LinearDimension("12rem")
                    borderRadius = LinearDimension("3px")
                    backgroundColor = Color.blanchedAlmond
                    color = Color.cornflowerBlue
                    fontSize = 1.rem

            }
            attrs.onChangeFunction = changeHandler
            attrs.value = text
        }
    }
}