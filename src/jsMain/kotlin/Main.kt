import react.child
import react.dom.render
import kotlinx.browser.document
import styled.*
import kotlinx.css.*


fun main() {
    render(document.getElementById("root")) {
        styledDiv{
            css {
                padding(3.rem)
                backgroundColor = Color.lightGreen
                color = Color.white
            }
        child(App)
        }
    }
}