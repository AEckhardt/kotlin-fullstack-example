package shoppinglist

import react.*
import kotlinext.js.*
import kotlinx.coroutines.*
import styled.styledH1
import styled.css
import kotlinx.css.*

private val scope = MainScope()

external interface editItemProps: RProps {
    val editing : Boolean
    val shoppingListItem : ShoppingListItem
}

val App = functionalComponent<RProps> { _ ->

    val (shoppingList, setShoppingList) = useState(emptyList<ShoppingListItem>())
    val initialItem = ShoppingListItem("",0)
    val (currentItem, setCurrentItem) = useState(initialItem)
    val (editing, setEditing) = useState(false)

    useEffect(dependencies = listOf()) {
        scope.launch {
            setShoppingList(getShoppingList())
        }
    }

    styledH1 {
        css {
            padding(vertical = 16.px)
        }
        +"Full-Stack Shopping List"
    }

    shoppingListComponent {
        currentShoppingList = shoppingList
        deleteItem = {item -> scope.launch {
            deleteShoppingListItem(item)
            setShoppingList(getShoppingList())
        }
        }
        editItem = { item -> scope.launch {
                setEditing(true)
                setCurrentItem(item)
            }
        }
    }
    if (editing){
    child(
            EditComponent,
        props = jsObject {
            shoppingListItem = currentItem
            onSubmit = { item ->
                scope.launch {
                    setEditing(false)
                    updateShoppingListItem(item)
                    setShoppingList(getShoppingList())
                }

            }
        }

    ) }else{
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
}