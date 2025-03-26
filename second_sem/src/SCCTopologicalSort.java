import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

public class SCCTopologicalSort {

    // Метод для выполнения DFS и записи порядка завершения вершин
    private static void dfs(int vertex, List<List<Integer>> graph, boolean[] visited, Deque<Integer> stack) {
        visited[vertex] = true;
        for (int neighbor : graph.get(vertex)) {
            if (!visited[neighbor]) {
                dfs(neighbor, graph, visited, stack);
            }
        }
        stack.push(vertex); // Добавляем вершину в стек после обработки всех соседей
    }

    private static void dfs(int vertex, List<List<Integer>> graph, boolean[] visited, Consumer<Integer> callback) {
        visited[vertex] = true;
        for (int neighbor : graph.get(vertex)) {
            if (!visited[neighbor]) {
                dfs(neighbor, graph, visited, callback);
            }
        }
        callback.accept(vertex); // Добавляем вершину в стек после обработки всех соседей
    }

    // Метод для обратного DFS на транспонированном графе
    private static void reverseDfs(int vertex, List<List<Integer>> transposedGraph, boolean[] visited, int componentId, int[] componentNumbers) {
        visited[vertex] = true;
        for (int neighbor : transposedGraph.get(vertex)) {
            if (!visited[neighbor]) {
                reverseDfs(neighbor, transposedGraph, visited, componentId, componentNumbers);
            }
        }
        componentNumbers[vertex] = componentId; // Присваиваем номер компоненты текущей вершине
    }

    // Метод для вычисления компонент сильной связности и топологической сортировки
    public static void findSCCsAndTopoSort(int n, List<List<Integer>> graph) {
        // Строим транспонированный граф
        List<List<Integer>> transposedGraph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            transposedGraph.add(new ArrayList<>());
        }
        for (int u = 0; u < n; u++) {
            for (int v : graph.get(u)) {
                transposedGraph.get(v).add(u); // Переворачиваем ребра
            }
        }

        // Первый проход DFS для получения порядка завершения
        Deque<Integer> stack = new ArrayDeque<>();
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(i, graph, visited, stack::push);
            }
        }

        // Обнуляем массив visited для второго прохода
        visited = new boolean[n];

        // Второй проход DFS на транспонированном графе
        int componentNumber = 1;
//        var componentNumbers = new ArrayList<Integer>(n); // Для хранения номеров компонент для каждой вершины
        var componentNumbers = new int[n]; // Для хранения номеров компонент для каждой вершины
        while (!stack.isEmpty()) {
            int vertex = stack.pop();
            if (!visited[vertex]) {
                var currentComponent = componentNumber;
                dfs(vertex, transposedGraph, visited, vert -> componentNumbers[vert] = currentComponent);
                componentNumber++;
            }
        }

        // Вывод результата
        System.out.println(componentNumber - 1); // Количество компонент сильной связности
        for (int num : componentNumbers) {
            System.out.print(num + " ");
        }
    }

    public static void main(String[] args) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter pw = new PrintWriter(System.out)) {

            // Чтение входных данных
            String[] input = br.readLine().split(" ");
            int N = Integer.parseInt(input[0]);
            int M = Integer.parseInt(input[1]);

            // Инициализация графа
            List<List<Integer>> graph = new ArrayList<>();
            for (int i = 0; i < N; i++) {
                graph.add(new ArrayList<>());
            }

            // Чтение ребер
            for (int i = 0; i < M; i++) {
                input = br.readLine().split(" ");
                int u = Integer.parseInt(input[0]) - 1; // Индексация с 0
                int v = Integer.parseInt(input[1]) - 1; // Индексация с 0
                graph.get(u).add(v);
            }

            // Находим компоненты сильной связности и выполняем топологическую сортировку
            findSCCsAndTopoSort(N, graph);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}