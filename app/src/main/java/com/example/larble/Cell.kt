package com.example.larble

class Cell : java.io.Serializable{
    var topWall: Boolean = true
    var leftWall: Boolean = true
    var rightWall: Boolean = true
    var bottomWall: Boolean = true

    var visited = false

    var debug : String = "no"

    var row: Int = -1
    var col: Int = -1

    //var width : Float = 0f
    //var height : Float = 0f

}