import kotlinx.css.*
import react.*
import react.dom.*
import kotlinx.html.js.*
import styled.*
import org.w3c.dom.events.Event
import org.w3c.dom.HTMLInputElement

external interface ChangeProps : RProps {
    var shoppingListItem: ShoppingListItem
    var onSubmit: (ShoppingListItem) -> Unit
}

val EditComponent = functionalComponent<ChangeProps> { props ->

    val (desc, setDesc) = useState("")
    val (prio, setPrio) = useState("")

    useEffect(listOf(props)) {
            setDesc(props.shoppingListItem.desc)
            setPrio(props.shoppingListItem.priority.toString())
    }

    val submitHandler: (Event) -> Unit = {
        it.preventDefault()
        if (desc!="" && prio!="")
            props.onSubmit(ShoppingListItem(desc,prio.toInt()))
    }

    val changeDescHandler: (Event) -> Unit = {
        val value = (it.target as HTMLInputElement).value
        setDesc(value)
    }

    val changePriorityHandler: (Event) -> Unit = {
        val value = (it.target as HTMLInputElement).value
        setPrio(value)
    }

    form {
        attrs.onSubmitFunction = submitHandler
        h2{
            +"Change Item"
        }
        styledInput {
            css{
                padding(10.px)
                margin(right = 10.px)
                width = LinearDimension("4rem")
                borderRadius = LinearDimension("3px")
                backgroundColor = Color.blanchedAlmond
                color = Color.cornflowerBlue
                fontSize = 1.rem

            }
            attrs.onChangeFunction = changePriorityHandler
            attrs.value = prio
            attrs.placeholder = ""
        }
        styledInput {
            css{
                padding(10.px)
                margin(right = 10.px)
                width = LinearDimension("12rem")
                borderRadius = LinearDimension("3px")
                backgroundColor = Color.blanchedAlmond.darken(15)
                color = Color.cornflowerBlue
                fontSize = 1.rem

            }
            attrs.onChangeFunction = changeDescHandler
            attrs.value = desc
            attrs.placeholder = "Description"
            attrs.disabled = true
        }
        styledButton{
            css{
                padding(10.px)
                backgroundColor = Color.cornflowerBlue
                width = LinearDimension("6rem")
                border = "3px solid cornflowerblue"
                borderRadius = LinearDimension("3px")
                color = Color.blanchedAlmond
            }
            +"update"
        }
    }
}