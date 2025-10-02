package tetris.game;

public class Piece {
    private int[][] shape;
    private int x;
    private int y;
    private int type;
    private int rotation;
    private int[][][] rotations;

    public Piece(int[][][] rotations, int type) {
        this.rotations = rotations;
        this.type = type;
        this.rotation = 0;
        this.shape = rotations[0];
        this.x = 0;
        this.y = 0;
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }

    public void moveDown() {
        y++;
    }

    public void moveUp() { 
        y--;
    }

    public void rotate() {
        int newRotation = (rotation + 1) % rotations.length;
        rotation = newRotation;
        shape = rotations[rotation];
    }

    public void rotateBack() {
        int newRotation = (rotation - 1 + rotations.length) % rotations.length;
        rotation = newRotation;
        shape = rotations[rotation];
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int[][] getShape() {
        return shape;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getType() {
        return type;
    }

    public int getRotation() {
        return rotation;
    }

    public Piece copy() {
        Piece copy = new Piece(rotations, type);
        copy.x = this.x;
        copy.y = this.y;
        copy.rotation = this.rotation;
        copy.shape = this.shape;
        return copy;
    }
}