package hey.btk;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        String shape = String.join("\n", new String[]{
                "         +------------+--+      +--+",
                "         |            |  |      |  |",
                "         | +-------+  |  |      |  |",
                "         | |       |  |  +------+  |",
                "         | |       |  |            |",
                "         | |       |  |    +-------+",
                "         | +-------+  |    |        ",
                " +-------+            |    |        ",
                " |       |            |    +-------+",
                " |       |            |            |",
                " +-------+            |            |",
                "         |            |            |",
                "    +----+---+--+-----+------------+",
                "    |    |   |  |     |            |",
                "    |    |   |  +-----+------------+",
                "    |    |   |                     |",
                "    +----+---+---------------------+",
                "    |    |                         |",
                "    |    | +----+                  |",
                "+---+    | |    |     +------------+",
                "|        | |    |     |             ",
                "+--------+-+    +-----+             "});
        String shape2 = String.join("\n", new String[]
                {
                        "   +----+-------+",
                        "   |    |       |",
                        "+--+    |   +---+",
                        "|       |   |",
                        "+-++-+  +---+",
                        "| || |  |   |",
                        "| || +--+-+-+",
                        "| ||      |",
                        "+-++------+"
                });

        System.out.println(shape2);
        System.out.println(Arrays.toString(process(shape2)));
    }

    public static String[] process(String shape) {
        String[] init = shape.split("\n");
        String[][] matrix = new String[init.length][];
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = init[i].split("");
        }

        int[][] starts = findStarts(matrix);
        List<String> res = new ArrayList<>();

        for (int[] _start : starts) {
            DirectionHolder holder = new DirectionHolder(matrix, _start);
            while (true) {
                holder.stepForward();
                if (Arrays.equals(holder.getPoint(), _start)) {
                    break;
                }
                if (holder.canGoRight()) {
                    holder.turnRight();
                    holder.rightCount++;
                } else {
                    int i = 0;
                    while (!holder.canGoForward()) {
                        i++;
                        holder.turnRight();
                        if (holder.isPrevious()) {
                            holder.turnRight();
                            i++;
                        }
                        if (i == 3) {
                            holder.leftCount++;
                        }
                    }
                }
            }
            if (holder.rightCount > holder.leftCount) {
                res.add(holder.getFigure());
            }

        }
        Set<String> set = new HashSet<>(res);
        res.clear();
        res.addAll(set);
        return res.toArray(new String[0]);
    }

    private static int[][] findStarts(String[][] matrix) {
        List<int[]> list = new ArrayList<>();
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j].equals("+") && new DirectionHolder(matrix, new int[]{i, j}).canGoForward()
                        && new DirectionHolder(matrix, new int[]{i, j}).canGoRight()) {
                    list.add(new int[]{i, j});
                }
            }
        }
//        return new int[][]{{0, 3},{4, 8},{4, 0},{4, 2},{0,8},{4,3}};
        return list.toArray(new int[0][]);
    }
}

class DirectionHolder {
    private int index = 0;
    private int[] point;
    private int[] prevPoint;
    private final String[][] matrix;
    private final String[][] resMatrix;
    public int leftCount;
    public int rightCount;
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
        this.resMatrix = new String[matrix.length][matrix[0].length];
        for (String[] strings : resMatrix) {
            Arrays.fill(strings, " ");
        }
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
        prevPoint = point;
        point = currentDirection.apply(point);
        show();
    }

    public boolean canGoRight() {
        int[] checkPoint = rightDirection.apply(point);
        String element;
        try {
            element = matrix[checkPoint[0]][checkPoint[1]];
            if (matrix[point[0]][point[1]].equals("+"))
                return element.equals("-") || element.equals("|") || element.equals("+");
            if (matrix[point[0]][point[1]].equals("-"))
                return element.equals("|") || element.equals("+");
            if (matrix[point[0]][point[1]].equals("|"))
                return element.equals("-") || element.equals("+");
            return true;
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public boolean canGoForward() {
        int[] checkPoint = currentDirection.apply(point);
        String element;
        try {
            element = matrix[checkPoint[0]][checkPoint[1]];
            return element.equals("-") || element.equals("|") || element.equals("+");
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
    }

    public void turnRight() {
        index = index < 3 ? index + 1 : 0;
        updateDirections();
    }

    public void show() {
        resMatrix[point[0]][point[1]] = matrix[point[0]][point[1]];
    }

    public int[] getPoint() {
        return point;
    }

    public boolean isPrevious() {
        int[] checkPoint = currentDirection.apply(point);
        return Arrays.equals(checkPoint, prevPoint);
    }

    public String getFigure() {
        StringBuilder res = new StringBuilder();
        List<Integer> spacesList = new ArrayList<>();


        for (String[] strings : resMatrix) {
            int spaces = 0;
            for (String string : strings) {
                if (string.equals(" ")) {
                    spaces++;
                } else {
                    spacesList.add(spaces);
                    break;
                }
            }
        }

        Optional<Integer> minOp = spacesList.stream().min(Comparator.naturalOrder());
        int min = minOp.get();
        String mask = "^ {" + min + "}";
        for (String[] strings : resMatrix) {
            StringBuilder chars = new StringBuilder();
            for (String string : strings) {
                chars.append(string);
            }
            String str = chars.toString().replaceAll(" *$", "");
            str = str.replaceAll(mask, "");
            res.append(str);
            if (!str.matches(" *")) {
                res.append("\n");
            }
        }

        String resultFigure = res.toString();
//        resultFigure = resultFigure.replaceAll("-+\\++-+","---");
        Pattern pattern = Pattern.compile("-+\\++-+");
        Matcher matcher = pattern.matcher(resultFigure);
        if (matcher.find()) {
            int length = matcher.group().length();
            StringBuilder replacement = new StringBuilder();
            for (int i = 0; i < length; i++) {
                replacement.append("-");
            }
            resultFigure = resultFigure.replace(matcher.group(), replacement);
        }


        return resultFigure;
    }
}