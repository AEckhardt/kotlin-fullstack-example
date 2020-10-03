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
            css {
                padding(10.px)
                backgroundColor = Color.antiqueWhite
                color = Color.cornflowerBlue
                borderRadius = LinearDimension("3px")
            }
            thead {
                styledTr {
                    css {
                        borderBottom = "2px solid lightgrey"
                    }
                    th { +"Priority" }
                    th { +"Item" }
                }
            }
            tbody {
                props.currentShoppingList.sortedByDescending(ShoppingListItem::priority).forEach { item ->
                    styledTr {
                        css {
                            hover {
                                backgroundColor = Color.pink
                            }
                        }
                        key = item.toString()
                        styledTd {
                            css {
                                put("text-align", "center")
                                padding(horizontal = 2.rem)
                                width = LinearDimension("6rem")
                            }
                            +"${item.priority}"
                        }
                        styledTd {
                            css {
                                width = LinearDimension("12rem")
                            }
                            +item.desc
                        }
                        styledTd {
                            css {
                                width = LinearDimension("4rem")
                            }
                            styledButton {
                                css {
                                    backgroundColor = Color.cornflowerBlue
                                    width = LinearDimension("4rem")
                                    border = "3px solid cornflowerblue"
                                    borderRadius = LinearDimension("3px")
                                    color = Color.blanchedAlmond
                                }
                                attrs.onClickFunction = {
                                    props.editItem(item)
                                }
                                +"edit"
                            }
                        }
                        styledTd {
                            css {
                                width = LinearDimension("4rem")
                            }
                            styledButton {
                                css {
                                    backgroundColor = Color.cornflowerBlue
                                    border = "3px solid cornflowerblue"
                                    borderRadius = LinearDimension("3px")
                                    width = LinearDimension("4rem")
                                    color = Color.blanchedAlmond

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