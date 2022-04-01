package hey.btk;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
//        String shape = String.join("\n", new String[]
//                {"+------------+",
//                        "|            |",
//                        "|            |",
//                        "|            |",
//                        "+------+-----+",
//                        "|      |     |",
//                        "|      |     |",
//                        "+------+-----+"});
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


        System.out.println(shape2);
        for (String str : process(shape2)) {
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

        while (true) {
            step(start);
        }
    }


    static void step(int[] start) {
        int[] point = start;
        point = DirectionHolder.currentDirection.apply(point);
    }

    static boolean checkRight(int[]) {

    }

    static void changeDirection() {

    }


    static class DirectionHolder {
        static int index = 0;
        static List<Function<int[], int[]>> list = List.of(
                (int[] arr) -> new int[]{arr[0], arr[1] + 1},
                (int[] arr) -> new int[]{arr[0] + 1, arr[1]},
                (int[] arr) -> new int[]{arr[0], arr[1] - 1},
                (int[] arr) -> new int[]{arr[0] - 1, arr[1]}
        );
        Function<int[], int[]> currentDirection = list.get(index);

        static void turnRight() {
            index = index < 4 ? index + 1 : 0;
        }

        static void stepForward(){}
    }
}
