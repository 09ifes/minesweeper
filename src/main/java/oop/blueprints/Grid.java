package oop.blueprints;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

public class Grid {
    int width;
    int height;
    boolean game;
    int totalCells;
    ArrayList<Cells>[][] cells;

    // creates initial grid, all cells set to 'unopened'
    public Grid(int width, int height){
        this.width = width;
        this.height = height;
        this.cells = new ArrayList[width][height];

        System.out.print("   ");
        for (int i = 0; i < this.height; i++){
            System.out.print(i + "  ");
        }
        System.out.print("\n");
        for (int row = 0; row < this.height; row++){
            System.out.print(row + "  ");
            for (int column = 0; column < this.width; column++){
                // for each coordinate (row, column), creates new cell object, adds to arraylist
                Cells cell = new Cells(row, column);

                this.cells[row][column] = new ArrayList<>();
                this.cells[row][column].add(cell);            // adds cell to 2d arraylist
                cell = this.cells[row][column].get(0);        // retrieves cell from arraylist
                System.out.print(cell.appearance);
            }
            System.out.print("\n");
        }
        this.totalCells = this.width * this.height;
        this.setMines();
        this.setAdjacents();
    }

    public void gridBuilder(){
        System.out.print("\n");
        System.out.print("   ");
        for (int i = 0; i < this.height; i++){
            System.out.print(i + "  ");
        }
        System.out.print("\n");
        for (int row = 0; row < this.height; row++){
            System.out.print(row + "  ");
            for (int column = 0; column < this.width; column++){
                // for each coordinate (row, column), gets cell from 2d arraylist
                Cells cell = this.cells[row][column].get(0);
                System.out.print(cell.appearance);
            }
            System.out.print("\n");
        }
    }

    public void receiveInputs(int coord_x, int coord_y, String flag){
        boolean check_x = this.validate_x(coord_x);
        boolean check_y = this.validate_y(coord_y);
        boolean check_flag = this.validate_flag(flag);
        if (check_x || check_y || check_flag)
            {
                if (flag == "F"){
                    this.placeFlag(coord_x, coord_y);
                }
                else {

                }

            }
            else {
                System.out.println("Please enter valid inputs!");
            }
        }





    public Boolean validate_x (int x) {
        if (x >= 0 && x < this.width) {
            return true;
        }
        else return false;
    }

    public Boolean validate_y (int y){
        if (y >= 0 && y < this.height){
            return true;
        }
        else {
            return false;
        }
    }

    public Boolean validate_flag(String flag){
        if (flag == "F" || flag == "NF"){
            return true;
        }
        else {
            return false;
        }
    }

    public void determineCell(int x, int y){
        Cells cell = this.cells[x][y].get(0);
        if (cell.isMine){
            this.showMines();
            this.game = false;
            System.out.println("Game over");
        }
        else if (cell.adjacentMinesCount > 0){
            this.numberedCell(x,y);
        }
        else if (cell.adjacentMinesCount == 0){
            this.blankCell(x,y);
        }


    }


    // generates random coordinates
    public ArrayList randCoords(){
        Random rand_x = new Random();
        Random rand_y = new Random();
        int x = rand_x.nextInt(this.width);
        int y = rand_y.nextInt(this.height);

        ArrayList<Integer> coords = new ArrayList<>();
        coords.add(x);
        coords.add(y);
        return coords;
    }

    public void setMines(){
        ArrayList coords;
        Cells cell;
        int mineNumber = (this.totalCells * 20) / 100;     // number of mines - 20%
        // gets coordinates for mines by executing randCoords method x amount of times
        for (int i = 0; i < mineNumber; i++){
            coords = this.randCoords();
            int x = (int) coords.get(0);             // converts from object type to int
            int y = (int) coords.get(1);
            cell = this.cells[x][y].get(0);

            // sets mine variable of cell objects to 'true'
            if (cell.isMine){
                while (cell.isMine){      // if cell already has mine, randomize coords until cell with no mine is found
                    coords = this.randCoords();
                    x = (int) coords.get(0);
                    y = (int) coords.get(1);
                    cell = this.cells[x][y].get(0);
                }
            }
            cell.isMine = true;
        }
    }


    // places flag in cell if cell is unopened/unclickable
    public void placeFlag(int coord_x, int coord_y){
        Cells cell = this.cells[coord_x][coord_y].get(0);
        if (cell.state == "opened"){
            System.out.println("Cell has already been opened, cannot place flag");
        }
        else if (cell.state == "unopened"){
            cell.state = "flagged";
            cell.isClickable = false;
            cell.appearance = "F  ";
        }
        else {
            cell.state = "unopened";
            cell.isClickable = true;
            cell.appearance = "*  ";
        }
        this.gridBuilder();
    }


    public void blankCell(int coord_x, int coord_y){
        Cells cell = this.cells[coord_x][coord_y].get(0);
        cell.state = "opened";
        cell.isClickable = false;
        cell.appearance = "0  ";
        this.adjacentChecker(coord_x, coord_y);
        this.gridBuilder();
    }

    public void numberedCell(int coord_x, int coord_y){
        Cells cell = this.cells[coord_x][coord_y].get(0);
        cell.state = "opened";
        cell.isClickable = false;
        cell.appearance = cell.adjacentMinesCount + "  ";
        this.gridBuilder();
    }

    public void showMines(){
        Cells cell;
        for (int row = 0; row < this.height; row++){
            for (int column = 0; column < this.width; column++){
                cell = this.cells[column][row].get(0);
                if (cell.isMine) {
                    cell.state = "opened";
                    cell.isClickable = false;
                    cell.appearance = "*  ";
                }
            }
        }
        this.gridBuilder();
    }

    public void setAdjacents() {
        for (int row = 0; row < this.height; row++) {
            for (int column = 0; column < this.width; column++) {
                Cells cell = this.cells[column][row].get(0);
                int n = 0;
                int[][] adjCoords = {{column-1,row}, {column+1,row}, {column,row-1}, {column,row+1},
                        {column+1,row+1}, {column-1,row-1}, {column+1,row-1}, {column-1,row+1}};

                // checks if adjacent cells has mine only if coordinates are within grid
                for (int i = 0; i < adjCoords.length; i++){
                    int x = adjCoords[i][0];
                    int y = adjCoords[i][1];
                    if (x < 0 || x > (this.width - 1)){
                        n = n;
                    }
                    else if (y < 0 || y > (this.height - 1)){
                        n = n;
                    }
                    else {
                        Cells adjCell = this.cells[x][y].get(0);
                        if (adjCell.isMine){
                            n += 1;
                        }
                        else {
                            n = n;
                        }
                    }
                }
                cell.adjacentMinesCount = n;
            }
        }
    }

    public void adjacentChecker(int coord_x, int coord_y){
        int[][] adjCoords = {{coord_x-1,coord_y}, {coord_x+1,coord_y}, {coord_x,coord_y-1}, {coord_x,coord_y+1},
                {coord_x+1,coord_y+1}, {coord_x-1,coord_y-1}, {coord_x+1,coord_y-1}, {coord_x-1,coord_y+1}};

        for (int i = 0; i < adjCoords.length; i++){
            int x = adjCoords[i][0];
            int y = adjCoords[i][1];

            if (x < 0 || x > (this.width - 1)){
                // do nothing
            }
            else if (y < 0 || y > (this.height - 1)){
               // do nothing
            }
            else {
                Cells adjCell = this.cells[x][y].get(0);
                if (adjCell.adjacentMinesCount > 0) {
                    adjCell.state = "opened";
                    adjCell.isClickable = false;
                    adjCell.isChecked = true;
                    adjCell.appearance = adjCell.adjacentMinesCount + "  ";

                }
                else {
                    if (!adjCell.isChecked) {
                        adjCell.appearance = adjCell.adjacentMinesCount + "  ";
                        adjCell.state = "opened";
                        adjCell.isClickable = false;
                        adjCell.isChecked = true;
                        this.adjacentChecker(x, y);
                    }
                    }
            }
        }

    }
    }














