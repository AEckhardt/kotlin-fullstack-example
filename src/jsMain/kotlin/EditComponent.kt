import react.*
import react.dom.*
import kotlinx.html.js.*
import kotlinx.html.InputType
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
        print("submitted")
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
        input {
            attrs.onChangeFunction = changeDescHandler
            attrs.value = desc
            attrs.placeholder = "Description"
            attrs.disabled = true
        }
        input {
            attrs.onChangeFunction = changePriorityHandler
            attrs.value = prio.toString()
            attrs.placeholder = ""
        }
        button{
            +"update"
        }
    }
}