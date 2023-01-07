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
    //private var horizontalMargin : Float = 0f
    //private var verticalMargin : Float = 0f
    private var wallThickness: Float = 7f
    private var wallPaint : Paint = Paint()

    private var cells = Array(cols){Array(rows){Cell()}}

    //private var stack : Stack<Cell> = Stack<Cell>()
    private lateinit var current : Cell
    private lateinit var next : Cell

    //Get size of canvas
    //var width : Float = Resources.getSystem().displayMetrics.widthPixels.toFloat()
    //var height : Float = Resources.getSystem().displayMetrics.heightPixels.toFloat()

    private var random = Random()

    private var width : Float = Resources.getSystem().displayMetrics.widthPixels.toFloat()
    private var height : Float = Resources.getSystem().displayMetrics.heightPixels.toFloat()



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
            } else
                current = stack.pop()


        }while(!stack.empty())


    }

    fun getCells(): Array<Array<Cell>> {
        return cells
    }

    fun setCells(cell: Array<Array<Cell>>) {
        cells = cell
    }

    fun findCell(x: Float, y: Float): Cell {
        var colCell: Int = ((x / width) * cols).toInt()
        if((x/width).toInt() == 1) colCell -= 1

        var rowCell: Int = ((y / height) * rows).toInt()
        if((y/height).toInt() == 1) rowCell -= 1

        return cells[colCell][rowCell]
    }

    fun setLimits(cell: Cell): Array<Float> {
        var top = -1f
        var bottom = -1f
        var left = -1f
        var right = -1f
        if (cell.topWall) top = (height / rows) * cell.row
        if (cell.bottomWall) bottom = (height / rows) * (cell.row + 1)
        if (cell.leftWall) left = (width / cols) * cell.col
        if (cell.rightWall) right = (width / cols) * (cell.col + 1)
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
            //Ex: we have 3 neigh. unvisited, this index is could be 0,1,2.
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
        //We will draw the maze here
        //super.onDraw(canvas)

        if(isDarkTheme()){
            wallPaint.color = Color.WHITE
        }else{
            wallPaint.color = Color.BLACK
        }
        wallPaint.strokeWidth = wallThickness
        createMaze()

        xCellSize = width/(5+1)
        yCellSize = height/10



        //If we need some margin for the maze, use these parameters
        //horizontalMargin = (width - cellSize)/2
        //verticalMargin = (height - rows*cellSize)/2
        //canvas?.translate(horizontalMargin, verticalMargin)

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


