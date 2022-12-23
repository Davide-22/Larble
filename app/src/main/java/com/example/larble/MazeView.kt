package com.example.larble

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import java.util.Random
import java.util.Stack

class MazeView(context: Context) : View(context) {

    private var rows: Int = 11
    private var cols: Int = 6


    private var cellSize : Float = 0f
    private var horizontalMargin : Float = 0f
    private var verticalMargin : Float = 0f
    private var wallThickness: Float = 7f
    private var wallPaint : Paint = Paint()

    private var cells = Array(cols){Array<Cell>(rows){i -> Cell()}}

    //private var stack : Stack<Cell> = Stack<Cell>()
    private lateinit var current : Cell
    private lateinit var next : Cell

    private var random = Random()

    private fun createMaze() {
        //Save position of cell
        for (i in cells.indices){
            for (j in cells[i].indices) {
                cells[i][j].row = j
                cells[i][j].col = i
            }
        }


        current = cells[0][0]
        current.visited = true
        var stack : Stack<Cell> = Stack<Cell>()
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

    private fun getNeighbour(cell : Cell) : Cell {

        var neighbours = ArrayList<Cell>()
        var index : Int
        var debug = Cell()

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
        //println("removeWall chiamata")
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

    override fun onDraw(canvas: Canvas?) {
        //We will draw the maze here
        //super.onDraw(canvas)

        wallPaint.color = Color.BLACK
        wallPaint.strokeWidth = wallThickness
        createMaze()

        //Get size of canvas
        var width : Float = Resources.getSystem().displayMetrics.widthPixels.toFloat()
        var height : Float = Resources.getSystem().displayMetrics.heightPixels.toFloat()

        cellSize = width/(5+1)

        //If we need some margin for the maze, use these parameters
        //horizontalMargin = (width - cellSize)/2
        //verticalMargin = (height - rows*cellSize)/2
        //canvas?.translate(horizontalMargin, verticalMargin)

        for (x in cells.indices) {
            for (y in cells[x].indices) {
                if (cells[x][y].topWall)
                    canvas?.drawLine(
                        x * cellSize,
                        y * cellSize,
                        (x + 1) * cellSize,
                        y * cellSize,
                        wallPaint
                    )


                if (cells[x][y].leftWall)
                    canvas?.drawLine(
                        x * cellSize,
                        y * cellSize,
                        (x) * cellSize,
                        (y + 1) * cellSize,
                        wallPaint
                    )


                if (cells[x][y].bottomWall)
                    canvas?.drawLine(
                        x * cellSize,
                        (y + 1) * cellSize,
                        (x + 1) * cellSize,
                        (y + 1) * cellSize,
                        wallPaint
                    )


                if (cells[x][y].rightWall)
                    canvas?.drawLine(
                        (x + 1) * cellSize,
                        y * cellSize,
                        (x + 1) * cellSize,
                        (y + 1) * cellSize,
                        wallPaint
                    )

                }
            }
        }

    }


