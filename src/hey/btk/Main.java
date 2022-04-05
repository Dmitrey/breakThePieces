package hey.btk;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Main {

    public static void main(String[] args) {
        String shape = String.join("\n", new String[]{
                "+-------------------+--+",
                "|                   |  |",
                "|                   |  |",
                "|  +----------------+  |",
                "|  |                   |",
                "|  |                   |",
                "+--+-------------------+"});

        System.out.println(shape);
        String[] process = process(shape);
        Arrays.stream(process(shape)).forEach(System.out::println);
    }

    public static String[] process(String shape) {
        String[] init = shape.split("\n");
        String[][] matrix = new String[init.length][];
        for (int i = 0; i < matrix.length; i++) {
            matrix[i] = init[i].split("");
        }

        int[][] starts = findStarts(matrix);
        List<Figure> res = new ArrayList<>();

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
                res.add(new Figure(holder.getFigure(),holder.getPointsList()));
            }
        }

        res.forEach(f-> f.getPoints().sort((a,b) -> {
            if (a[0] > b[0]){
                return 1;
            }
            if (b[0] > a[0]){
                return -1;
            }
            if (a[1] > b[1]){
                return 1;
            }
            if (b[1] > a[1]){
                return -1;
            }
            return 0;
        }));

        HashMap<Integer,Figure> map = new HashMap<>();
        for (Figure figure:res) {
            map.put(figure.hashCode(),figure);
        }

        List<String> list = new ArrayList<>();
        for (Figure fig: map.values()) {
            list.add(fig.getFigure());
        }
        return list.toArray(new String[0]);
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
        return list.toArray(new int[0][]);
    }
}

class Figure{
    private String figure;
    private List<int[]> points;
    public Figure(String figure, List<int[]> points){
        this.figure = figure;
        this.points = points;
    }

    public List<int[]> getPoints() {
        return points;
    }

    public void setPoints(List<int[]> points) {
        this.points = points;
    }

    public String getFigure() {
        return figure;
    }

    @Override
    public int hashCode() {
        int result = 1;
        for (int[] arr:points) {
            result+=arr[0]*100+arr[1]*10000;
        }
        return result;
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
    private List<int[]> pointsList = new ArrayList<>();
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
        pointsList.add(point);
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

    public List<int[]> getPointsList() {
        return pointsList;
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
        Pattern pattern;
        Matcher matcher;
        while (true) {
            pattern = Pattern.compile("-+\\++-+");
            matcher = pattern.matcher(resultFigure);
            if (matcher.find()) {
                int length = matcher.group().length();
                StringBuilder replacement = new StringBuilder();
                replacement.append("-".repeat(length));
                resultFigure = resultFigure.replace(matcher.group(), replacement);
            }else break;
        }
        pattern = Pattern.compile("\\+\\+-+");
        matcher = pattern.matcher(resultFigure);
        if (matcher.find()) {
            int length = matcher.group().length();
            StringBuilder replacement = new StringBuilder("+");
            for (int i = 0; i < length - 1; i++) {
                replacement.append("-");
            }
            resultFigure = resultFigure.replace(matcher.group(), replacement);
        }
        pattern = Pattern.compile("-+\\+\\+");
        matcher = pattern.matcher(resultFigure);
        if (matcher.find()) {
            int length = matcher.group().length();
            StringBuilder replacement = new StringBuilder();
            for (int i = 0; i < length - 1; i++) {
                replacement.append("-");
            }
            replacement.append("+");
            resultFigure = resultFigure.replace(matcher.group(), replacement);
        }

//        System.out.println(resultFigure);
        String[][] arr1 = stringToArray(resultFigure);
//        printArray(arr1);
        String[][] transpone = transpone(arr1);
//        printArray(transpone);
        String transpStr = arrayToString(transpone);
//        System.out.println(transpStr);

        pattern = Pattern.compile("\\|+\\++\\|");
        matcher = pattern.matcher(transpStr);
        if (matcher.find()) {
            int length = matcher.group().length();
            StringBuilder replacement = new StringBuilder();
            for (int i = 0; i < length; i++) {
                replacement.append("|");
            }
            transpStr = transpStr.replace(matcher.group(), replacement);
        }


        transpone = stringToArray(transpStr);
//        printArray(transpone);
        arr1 = transpone(transpone);
//        printArray(arr1);
        resultFigure = arrayToString(arr1);
//        System.out.println(resultFigure);
        return resultFigure;
    }

    public static String[][] stringToArray(String str) {
        String[] arr = str.split("\n");
        String[][] matrix = new String[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            matrix[i] = arr[i].split("");
        }
        return matrix;
    }

    public static String arrayToString(String[][] arr) {
        StringBuilder res = new StringBuilder();
        for (String[] strings : arr) {
            StringBuilder chars = new StringBuilder();
            for (String string : strings) {
                chars.append(string);
            }
            String str = chars.toString().replaceAll(" *$", "");
            res.append(str).append("\n");

        }
        String result = res.toString();
        result = result.replaceAll("\n$", "");
        return result;
    }

    public static String[][] transpone(String[][] matrix) {
        Optional<String[]> maxOp = Arrays.stream(matrix).max((arr1, arr2) -> Integer.compare(arr1.length, arr2.length));
        int max = maxOp.get().length;
        String[][] transp = new String[max][matrix.length];
        for (String[] s : transp) {
            Arrays.fill(s, " ");
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                transp[j][i] = matrix[i][j];
            }
        }
        return transp;
    }

    public static void printArray(String[][] arr) {
        for (String[] a : arr) {
            System.out.println(Arrays.toString(a));
        }
    }
}