import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*

external interface ShoppingListProps : RProps {
    var currentShoppingList: List<ShoppingListItem>
    var onClickItem: (ShoppingListItem) -> Unit
}

class ShoppingList: RComponent<ShoppingListProps, RState>() {
    override fun RBuilder.render() {
        table {
            thead {
                tr{
                    th{+"Priority"}
                    th{+"Item"}
                }
            }
            tbody {
                props.currentShoppingList.sortedByDescending(ShoppingListItem::priority).forEach { item ->
                    tr {
                        key = item.toString()
                        attrs.onClickFunction = {
                            props.onClickItem(item)
                        }
                        td{+"${item.priority}"}
                        td{+item.desc}
                    }
                }
            }
        }
    }
}

fun RBuilder.shoppingListComponent(handler: ShoppingListProps.() -> Unit): ReactElement {
    return child(ShoppingList::class) {
        this.attrs(handler)
    }
}