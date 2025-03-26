import java.util.ArrayList;
import java.util.List;

public class DFS {

    // Метод для выполнения DFS
    public static void dfs(int vertex, List<List<Integer>> graph, boolean[] visited, List<Integer> component) {
        // Отмечаем текущую вершину как посещенную
        visited[vertex] = true;
        // Добавляем вершину в текущую компоненту связности
        component.add(vertex);

        // Обходим всех соседей текущей вершины
        for (int neighbor : graph.get(vertex)) {
            if (!visited[neighbor]) {
                dfs(neighbor, graph, visited, component); // Рекурсивный вызов для соседа
            }
        }
    }

    // Метод для поиска всех компонент связности
    public static List<List<Integer>> findConnectedComponents(int n, List<List<Integer>> graph) {
        boolean[] visited = new boolean[n]; // Массив для отслеживания посещенных вершин
        List<List<Integer>> components = new ArrayList<>(); // Список для хранения компонент связности

        // Проходим по всем вершинам графа
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                List<Integer> component = new ArrayList<>(); // Новая компонента связности
                dfs(i, graph, visited, component); // Запускаем DFS из непосещенной вершины
                components.add(component); // Добавляем найденную компоненту в список
            }
        }

        return components;
    }

    public static void main(String[] args) {
        // Пример графа (список смежности)

        int n = 6; // Количество вершин
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        // Добавляем ребра (граф неориентированный)
        addEdge(graph, 2, 0);
        addEdge(graph, 0, 1);
        addEdge(graph, 4, 3);
        addEdge(graph, 3, 4);
        addEdge(graph, 1, 2);

        // Находим компоненты связности
        List<List<Integer>> components = findConnectedComponents(n, graph);

        // Выводим результаты
        System.out.println("Количество компонент связности: " + components.size());
        for (int i = 0; i < components.size(); i++) {
            System.out.print("Компонента " + (i + 1) + ": ");
            for (int vertex : components.get(i)) {
                System.out.print((vertex + 1) + " "); // Номера вершин начинаются с 1
            }
            System.out.println();
        }
    }

    // Вспомогательный метод для добавления ребер в граф
    private static void addEdge(List<List<Integer>> graph, int u, int v) {
        graph.get(u).add(v);
        graph.get(v).add(u); // Граф неориентированный
    }
}