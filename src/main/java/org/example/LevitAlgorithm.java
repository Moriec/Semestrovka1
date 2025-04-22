package org.example;

import java.util.*;

public class LevitAlgorithm {

    private static final int INF = Integer.MAX_VALUE;

    public static int[] levit(int[][] graph, int startNode) {
        int n = graph.length; // Количество вершин в графе
        int[] dist = new int[n]; // Массив расстояний от startNode до каждой вершины
        Arrays.fill(dist, INF); // Инициализация расстояний бесконечностью
        dist[startNode] = 0; // Расстояние от начальной вершины до себя равно 0

        // Три множества для вершин:
        // 1. `Q` - множество "обрабатываемых" вершин (временно)
        // 2. `P` - множество "обработанных" вершин
        // 3. Вершины, не принадлежащие `Q` или `P` считаются "неизвестными"

        Set<Integer> Q = new HashSet<>(); // Очередь для обработки
        Set<Integer> P = new HashSet<>(); // Вершины, расстояния до которых уже посчитаны
        Q.add(startNode); // Начинаем с начальной вершины

        while (!Q.isEmpty()) {
            // Выбираем вершину u из Q. Можно выбрать произвольную, но для лучшей производительности
            // часто выбирают вершину, которая была добавлена в Q позже других.
            // Здесь мы просто берем первую.
            Integer u = Q.iterator().next();
            Q.remove(u);

            // Обходим все смежные вершины v
            for (int v = 0; v < n; v++) {
                if (graph[u][v] != 0) { // Если есть ребро из u в v
                    int weight = graph[u][v]; // Вес ребра (u, v)

                    if (dist[u] != INF && dist[u] + weight < dist[v]) {
                        // Найдено более короткое расстояние до v

                        dist[v] = dist[u] + weight; // Обновляем расстояние

                        //  Теперь нужно добавить v в Q, если она еще не там
                        if (!Q.contains(v) && !P.contains(v)) {
                            Q.add(v); // Если v вообще не посещалась
                        } else if (P.contains(v)) {
                            P.remove(v); // v переходит из P в Q
                            Q.add(v);
                        }
                    }
                }
            }
            P.add(u); // Вершина u обработана
        }

        return dist; // Возвращаем массив расстояний
    }

    public static void main(String[] args) {
        // Пример графа (матрица смежности):
        //  0: A
        //  1: B
        //  2: C
        //  3: D

        int[][] graph = {
                {0, 2, 0, 1},
                {2, 0, 3, 0},
                {0, 3, 0, 4},
                {1, 0, 4, 0}
        };

        int startNode = 0; // Начинаем поиск из вершины A

        int[] shortestDistances = levit(graph, startNode);

        System.out.println("Кратчайшие расстояния от вершины " + (char)('A' + startNode) + ":");
        for (int i = 0; i < shortestDistances.length; i++) {
            System.out.println("До вершины " + (char)('A' + i) + ": " + (shortestDistances[i] == INF ? "Бесконечность" : shortestDistances[i]));
        }
    }
}