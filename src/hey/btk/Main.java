package hey.btk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
        String shape = String.join("\n", new String[]
                {       "+--------+",
                        "|        |",
                        "|        |",
                        "|        |",
                        "+--+-----+",
                        "|  |     |",
                        "|  |     |",
                        "+--+-----+"});
        String shape2 = String.join("\n", new String[]
                {
                        "   +----+-------+",
                        "   |    |       |",
                        "+--+    |   +---+",
                        "|       |   |",
                        "+----+  +---+",
                        "|    |  |   |",
                        "|    +--+-+-+",
                        "|         |",
                        "+---------+"
                });


        System.out.println(shape);
        for (String str : process(shape)) {
            System.out.println(str);
        }
    }

    public static String[] process(String shape) {
        String[] init = shape.split("\n");
        String[][] matrix = new String[init.length][];
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = init[i].split("");
        }
        int[] start = {0, 3};

        DirectionHolder holder = new DirectionHolder(matrix, start);
        while (true) {
            holder.stepForward();
            if (Arrays.equals(holder.getPoint(), start)){
                break;
            }
            if (holder.canGoRight()){
                holder.turnRight();
            }else {
                while (!holder.canGoForward()){
                    holder.turnRight();
                }
            }
        }
        return null;
    }
}

class DirectionHolder {
    private int index = 0;
    private int[] point;
    private final String[][] matrix;
    private final List<Function<int[], int[]>> list = List.of(
            (int[] arr) -> new int[]{arr[0], arr[1] + 1},
            (int[] arr) -> new int[]{arr[0] + 1, arr[1]},
            (int[] arr) -> new int[]{arr[0], arr[1] - 1},
            (int[] arr) -> new int[]{arr[0] - 1, arr[1]}
    );
    private Function<int[], int[]> currentDirection = list.get(index);
    private Function<int[], int[]> rightDirection;

    public DirectionHolder(String[][] matrix, int[] start) {
        this.matrix = matrix;
        point = start;
        updateDirections();
    }

    void updateDirections() {
        currentDirection = list.get(index);
        try {
            rightDirection = list.get(index + 1);
        } catch (Exception e) {
            rightDirection = list.get(0);
        }
    }

    public void stepForward() {
        point = currentDirection.apply(point);
        show();
    }

    public boolean canGoRight() {
        int[] checkPoint = rightDirection.apply(point);
        String element = matrix[checkPoint[0]][checkPoint[1]];
        return element.equals("-") || element.equals("|") || element.equals("+");
    }

    public boolean canGoForward() {
        int[] checkPoint = currentDirection.apply(point);
        String element = matrix[checkPoint[0]][checkPoint[1]];
        return element.equals("-") || element.equals("|") || element.equals("+");
    }

    public void turnRight() {
        index = index < 3 ? index + 1 : 0;
        updateDirections();
    }

    public void show(){
        System.out.print(matrix[point[0]][point[1]]);
    }

    public int[] getPoint() {
        return point;
    }
}