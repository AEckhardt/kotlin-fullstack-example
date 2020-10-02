import react.*
import react.dom.*
import kotlinext.js.*
import kotlinx.html.js.*
import kotlinx.coroutines.*
import styled.styledH1
import styled.css
import styled.StyleSheet
import kotlinx.css.*

private val scope = MainScope()


val App = functionalComponent<RProps> { _ ->
    val (shoppingList, setShoppingList) = useState(emptyList<ShoppingListItem>())

    useEffect(dependencies = listOf()) {
        scope.launch {
            setShoppingList(getShoppingList())
        }
    }

    styledH1 {
        css {
            padding(vertical = 16.px)
            put("color","#808080")
            put("font-family","sans-serif")
        }
        +"Full-Stack Shopping List"
    }

    shoppingListComponent {
        currentShoppingList = shoppingList
        onClickItem = {item -> scope.launch {
            deleteShoppingListItem(item)
            setShoppingList(getShoppingList())}
        }
        editItem = {
            item -> scope.launch {
            updateShoppingListItem(item)
            setShoppingList(getShoppingList())
        }
        }
    }
    child(
        InputComponent,
        props = jsObject {
            onSubmit = { input ->
                val cartItem = ShoppingListItem(input.replace("!", ""), input.count { it == '!' })
                scope.launch {
                    addShoppingListItem(cartItem)
                    setShoppingList(getShoppingList())
                }
            }
        }
    )
}