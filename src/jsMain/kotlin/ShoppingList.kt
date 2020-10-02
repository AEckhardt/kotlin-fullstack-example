import kotlinx.html.js.onClickFunction
import react.*
import react.dom.*
import styled.*
import kotlinx.css.*

external interface ShoppingListProps : RProps {
    var currentShoppingList: List<ShoppingListItem>
    var deleteItem: (ShoppingListItem) -> Unit
    var editItem: (ShoppingListItem) -> Unit
}

class ShoppingList: RComponent<ShoppingListProps, RState>() {
    override fun RBuilder.render() {
        styledTable {
            css{
                padding(10.px)
                put("font-familiy","sans-serif")
                border = "5px solid gray"
            }
            thead {
                styledTr{
                    css{put("font-family","sans-serif")}
                    th{+"Priority"}
                    th{+"Item"}
                }
            }
            tbody {
                props.currentShoppingList.sortedByDescending(ShoppingListItem::priority).forEach { item ->
                    styledTr {
                        css{
                            put("font-family","sans-serif")
                            padding(1.rem)
                        }
                        key = item.toString()
                        styledTd{
                            css{
                                put("text-align","center")
                            }
                            +"${item.priority}"}
                        styledTd{
                            css{
                                margin(1.rem)
                            }
                            +item.desc}
                        td{
                            styledButton{
                                css{
                                    padding(1.rem)
                                    put("font-family","sans-serif")
                                    color = Color.aqua
                                    backgroundColor = Color.azure
                                }
                                attrs.onClickFunction = {
                                    props.editItem(item)
                                }
                                +"edit"
                            }
                        }
                        td{
                            styledButton{
                                css{
                                    padding(1.rem)
                                    put("font-family","sans-serif")
                                    color = Color.aqua
                                    backgroundColor = Color.azure

                                }
                                attrs.onClickFunction = {
                                    props.deleteItem(item)
                                }
                                +"delete"
                            }
                        }
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