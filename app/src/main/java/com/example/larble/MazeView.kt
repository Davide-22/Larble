package com.example.larble

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import java.util.Random
import java.util.Stack

class MazeView(context: Context) : View(context) {

    private var rows: Int = 10
    private var cols: Int = 6

    private var xCellSize : Float = 0f
    private var yCellSize : Float = 0f
    private var wallThickness: Float = 7f
    private var wallPaint : Paint = Paint()

    private var cells = Array(cols){Array(rows){Cell()}}

    private lateinit var current : Cell
    private lateinit var next : Cell

    private var random = Random()

    private var width : Float = Resources.getSystem().displayMetrics.widthPixels.toFloat()
    private var height : Float = Resources.getSystem().displayMetrics.heightPixels.toFloat()

    private var multiplayer: Boolean = false

    private fun createMaze() {
        //Save position of cell
        for (i in cells.indices){
            for (j in cells[i].indices) {
                cells[i][j].row = j
                cells[i][j].col = i

                //cells[i][j].width = width/cols*(i+1)
                //cells[i][j].height = height/rows*(j+1)
            }
        }


        current = cells[0][0]
        current.visited = true
        val stack : Stack<Cell> = Stack<Cell>()
        //Recursive back-tracker
        do{
            next = getNeighbour(current)
            if(next.debug != "yes") {
                removeWall(current, next)
                stack.push(current)
                current = next
                current.visited = true
            } else if(!stack.empty()) {
                current = stack.pop()
            }

        }while(!stack.empty())


    }

    fun getCells(): Array<Array<Cell>> {
        createMaze()
        return cells
    }

    fun setCells(cell: Array<Array<Cell>>) {
        cells = cell
        multiplayer = true
    }


    fun findCell(x: Float, y: Float): Array<Cell> {

        var colCellCenter: Int = (((x+50f) / width) * cols).toInt()
        if(((x+50f)/width).toInt() == 1) colCellCenter -= 1

        var rowCellCenter: Int = (((y+50f) / height) * rows).toInt()
        if(((y+50f)/height).toInt() == 1) rowCellCenter -= 1

        var colCellTopLeft: Int = (((x+1) / width) * cols).toInt()
        if(((x+1)/width).toInt() == 1) colCellTopLeft -= 1

        var rowCellTopLeft: Int = (((y+1) / height) * rows).toInt()
        if(((y+1)/height).toInt() == 1) rowCellTopLeft -= 1

        var colCellTopRight: Int = (((x+99f) / width) * cols).toInt()
        if(((x+99f)/width).toInt() == 1) colCellTopRight -= 1

        var rowCellTopRight: Int = (((y+1) / height) * rows).toInt()
        if(((y+1)/height).toInt() == 1) rowCellTopRight -= 1

        var colCellBotRight: Int = (((x+99f) / width) * cols).toInt()
        if(((x+99f)/width).toInt() == 1) colCellBotRight -= 1

        var rowCellBotRight: Int = (((y+99f) / height) * rows).toInt()
        if(((y+99f)/height).toInt() == 1) rowCellBotRight -= 1

        var colCellBotLeft: Int = (((x+1) / width) * cols).toInt()
        if(((x+1)/width).toInt() == 1) colCellBotLeft -= 1

        var rowCellBotLeft: Int = (((y+99f) / height) * rows).toInt()
        if(((y+99f)/height).toInt() == 1) rowCellBotLeft -= 1


        if(colCellTopRight == cols) colCellTopRight = cols-1
        if(colCellBotRight == cols) colCellBotRight = cols-1

        return arrayOf(cells[colCellTopLeft][rowCellTopLeft],
            cells[colCellTopRight][rowCellTopRight],
            cells[colCellBotRight][rowCellBotRight],
            cells[colCellBotLeft][rowCellBotLeft],
            cells[colCellCenter][rowCellCenter])
    }


    fun setLimits(cell_ar: Array<Cell>, yVel: Float, xVel: Float): Array<Float> {
        var top = -1f
        var bottom = -1f
        var left = -1f
        var right = -1f

        if (cell_ar[4].topWall) top = (height / rows) * cell_ar[4].row + 1
        if (cell_ar[4].bottomWall) bottom = (height / rows) * (cell_ar[4].row + 1) - 1
        if (cell_ar[4].leftWall) left = (width / cols) * cell_ar[4].col + 1
        if (cell_ar[4].rightWall) right = (width / cols) * (cell_ar[4].col + 1) - 1

        if (cell_ar[2].leftWall && cell_ar[3].rightWall && (cell_ar[2] != cell_ar[3]) && yVel < 0) bottom = (height / rows) * (cell_ar[4].row + 1) - 1
        if (cell_ar[1].leftWall && cell_ar[0].rightWall && (cell_ar[1] != cell_ar[0]) && yVel > 0) top = (height / rows) * cell_ar[4].row + 1
        if (cell_ar[0].bottomWall && cell_ar[3].topWall && (cell_ar[0] != cell_ar[3]) && xVel > 0) left = (width / cols) * cell_ar[4].col + 1
        if (cell_ar[1].bottomWall && cell_ar[2].topWall && (cell_ar[1] != cell_ar[2]) && xVel < 0)  right = (width / cols) * (cell_ar[4].col + 1) - 1

        return arrayOf(top, left, bottom, right)
    }

    private fun getNeighbour(cell : Cell) : Cell {

        val neighbours = ArrayList<Cell>()
        var index : Int
        val debug = Cell()

        //left neighbour
        if(cell.col > 0)
            if(!cells[cell.col-1][cell.row].visited)
                neighbours.add(cells[cell.col-1][cell.row])
        //right neighbour
        if(cell.col < cols - 1)
            if(!cells[cell.col+1][cell.row].visited)
                neighbours.add(cells[cell.col+1][cell.row])
        //top neighbour
        if(cell.row > 0)
            if(!cells[cell.col][cell.row-1].visited)
                neighbours.add(cells[cell.col][cell.row-1])
        //bottom neighbour
        if(cell.row < rows - 1)
            if(!cells[cell.col][cell.row+1].visited) {
                neighbours.add(cells[cell.col][cell.row+1])
            }

        if(neighbours.size > 0) {
            index = random.nextInt(neighbours.size)
            return neighbours[index]
        }
        debug.debug = "yes"
        return debug
    }

    private fun removeWall(current : Cell, next : Cell) {
        if(current.col == next.col && current.row == next.row+1) {
            current.topWall = false
            next.bottomWall = false
        }
        if(current.col == next.col && current.row == next.row-1) {
            current.bottomWall = false
            next.topWall = false
        }
        if(current.col == next.col+1 && current.row == next.row) {
            current.leftWall = false
            next.rightWall = false
        }
        if(current.col == next.col-1 && current.row == next.row) {
            current.rightWall = false
            next.leftWall = false
        }
    }

    private fun isDarkTheme(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onDraw(canvas: Canvas?) {

        if(isDarkTheme()){
            wallPaint.color = Color.WHITE
        }else{
            wallPaint.color = Color.BLACK
        }
        wallPaint.strokeWidth = wallThickness
        if(!multiplayer) createMaze()

        xCellSize = width/(5+1)
        yCellSize = height/10

        for (x in cells.indices) {
            for (y in cells[x].indices) {
                if (cells[x][y].topWall)
                    canvas?.drawLine(
                        x * xCellSize,
                        y * yCellSize,
                        (x + 1) * xCellSize,
                        y * yCellSize,
                        wallPaint
                    )


                if (cells[x][y].leftWall)
                    canvas?.drawLine(
                        x * xCellSize,
                        y * yCellSize,
                        (x) * xCellSize,
                        (y + 1) * yCellSize,
                        wallPaint
                    )


                if (cells[x][y].bottomWall)
                    canvas?.drawLine(
                        x * xCellSize,
                        (y + 1) * yCellSize,
                        (x + 1) * xCellSize,
                        (y + 1) * yCellSize,
                        wallPaint
                    )


                if (cells[x][y].rightWall)
                    canvas?.drawLine(
                        (x + 1) * xCellSize,
                        y * yCellSize,
                        (x + 1) * xCellSize,
                        (y + 1) * yCellSize,
                        wallPaint
                    )

                }
            }
        }

    }


