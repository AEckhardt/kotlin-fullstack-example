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