package org.example;

import java.io.*;
import java.util.*;

public class LevitAlgorithm {

    private static final int INF = Integer.MAX_VALUE;
    private static final String DATA_DIRECTORY = "data/";

    public static void main(String[] args) throws IOException {
        // Запуск экспериментов
        for (int i = 5; i <= 50; i ++) { // Кол-во элементов в одном наборе

            long middleResultTime = 0, middleResultIteration = 0; // Среднее время

            for(int j = 0; j < 100; j++){ // Кол-во наборов на одинаковое кол-во элементов
                generateDatasets(1, i, i); // Генерация наборов данных и запись в файлы
                String filename = DATA_DIRECTORY + "graph_" + i + ".txt";
                long[] results = runExperiment(filename);

                middleResultTime += results[0];
                middleResultIteration += results[1];
            }

            middleResultTime /= 100;
            middleResultIteration /= 100;

            System.out.println(i + " Время: " + middleResultTime + " Итерации: " + middleResultIteration);
        }
    }


    public static void generateDatasets(int numDatasets, int minSize, int maxSize) throws IOException {
        File directory = new File(DATA_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs(); // Создаем директорию, если она не существует
        }

        Random random = new Random();
        for (int i = 0; i < numDatasets; i++) {
            int size = minSize;
            String filename = DATA_DIRECTORY + "graph_" + size + ".txt";

            // Генерация графа (матрицы смежности)
            int[][] graph = new int[size][size];
            for (int row = 0; row < size; row++) {
                for (int col = 0; col < size; col++) {
                    if (row == col) {
                        graph[row][col] = 0; // Нет петли
                    } else {
                        // Задаем вероятность наличия ребра и веса
                        if (random.nextDouble() < 0.3) {  // 30% вероятность наличия ребра
                            graph[row][col] = random.nextInt(10) + 1; // Вес ребра от 1 до 10
                        } else {
                            graph[row][col] = 0; // Нет ребра
                        }
                    }
                }
            }

            // Запись графа в файл
            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println(size);
                for (int row = 0; row < size; row++) {
                    for (int col = 0; col < size; col++) {
                        writer.print(graph[row][col] + " ");
                    }
                    writer.println();
                }
            }
        }
    }



    public static int[][] readGraphFromFile(String filename) throws IOException {
        int[][] graph = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            int size = Integer.parseInt(reader.readLine()); // Читаем размер графа из первой строки
            graph = new int[size][size];
            for (int row = 0; row < size; row++) {
                String[] values = reader.readLine().split(" ");
                for (int col = 0; col < size; col++) {
                    graph[row][col] = Integer.parseInt(values[col]);
                }
            }
        }
        return graph;
    }


    public static long[] runExperiment(String filename) throws IOException {
        int[][] graph = readGraphFromFile(filename);
        int startNode = 0; // Начинаем с нулевой вершины

        long startTime = System.nanoTime();

        LevitAlgorithmExperiment.LevitResult result = levit(graph, startNode);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);

        return new long[]{duration, result.iterations};
        // int[] shortestDistances = result.distances;
        // for (int i = 0; i < shortestDistances.length; i++) {
        //     System.out.println("    До вершины " + i + ": " + (shortestDistances[i] == INF ? "Бесконечность" : shortestDistances[i]));
        // }
    }


    static class LevitResult {
        int[] distances;
        int iterations;

        public LevitResult(int[] distances, int iterations) {
            this.distances = distances;
            this.iterations = iterations;
        }
    }


    public static LevitAlgorithmExperiment.LevitResult levit(int[][] graph, int startNode) {
        int n = graph.length;
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[startNode] = 0;

        // Три множества для вершин:
        // 1. `Q` - множество "обрабатываемых" вершин (временно)
        // 2. `P` - множество "обработанных" вершин
        // 3. Вершины, не принадлежащие `Q` или `P` считаются "неизвестными"

        Deque<Integer> Q = new LinkedList<>(); // Очередь для обработки
        Set<Integer> P = new HashSet<>(); // Вершины, расстояния до которых уже посчитаны
        Q.add(startNode);

        int countIteration = 0;

        while (!Q.isEmpty()) {
            Integer u = Q.iterator().next();
            Q.remove(u);

            // Обходим все смежные вершины v
            for (int v = 0; v < n; v++) {

                countIteration++;

                if (graph[u][v] != 0) { // Если есть ребро из u в v
                    int weight = graph[u][v]; // Вес ребра (u, v)

                    if (dist[u] != INF && dist[u] + weight < dist[v]) {
                        // Найдено более короткое расстояние до v

                        dist[v] = dist[u] + weight; // Обновляем расстояние

                        // добавить v в Q, если она еще не там
                        if (!Q.contains(v) && !P.contains(v)) {
                            Q.add(v); // Если v вообще не посещалась
                        } else if (P.contains(v)) {
                            P.remove(v); // v переходит из P в Q
                            Q.addFirst(v);
                        }
                    }
                }
            }
            P.add(u); // Вершина u обработана
        }

        return new LevitAlgorithmExperiment.LevitResult(dist, countIteration); // Возвращаем данные для теста
    }
}