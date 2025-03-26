import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class A2 {
    public static void main(String[] args) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(System.in));
        var conditions = parseLineIntoArray(reader.readLine());
        var vertexCount = conditions[0];
        var pairsCount = conditions[1];
        List<List<Integer>> graph = new ArrayList<>();
        for (var i = 0; i < vertexCount; i++) {
            graph.add(new ArrayList<>());
        }

        while (pairsCount > 0) {
            addEdge(graph, parseLineIntoArray(reader.readLine()));
            pairsCount--;
        }

        var components = findConnectedComponents(vertexCount, graph);

        var resultSb = new StringBuilder();
        resultSb.append(components.size()).append(System.lineSeparator());
        for (var component : components) {
            resultSb.append(component.size()).append(System.lineSeparator());
            for (var vertex : component) {
                resultSb.append(vertex + 1).append(" ");
            }
            resultSb.append(System.lineSeparator());
        }
        System.out.println(resultSb);
    }

    private static int[] parseLineIntoArray(String line) {
        return Arrays.stream(line.split(" ")).mapToInt(Integer::parseInt).toArray();
    }

    private static void addEdge(List<List<Integer>> graph, int[] edges) {
        graph.get(edges[0] - 1).add(edges[1] - 1);
        graph.get(edges[1] - 1).add(edges[0] - 1);
    }

    public static void dfs(int vertex, List<List<Integer>> graph, boolean[] visited, List<Integer> component) {
        visited[vertex] = true;
        component.add(vertex);

        for (var neighbor : graph.get(vertex)) {
            if (!visited[neighbor]) {
                dfs(neighbor, graph, visited, component);
            }
        }
    }

    public static List<List<Integer>> findConnectedComponents(int n, List<List<Integer>> graph) {
        var visited = new boolean[n];
        var components = new ArrayList<List<Integer>>();

        for (var i = 0; i < n; i++) {
            if (!visited[i]) {
                List<Integer> component = new ArrayList<>();
                dfs(i, graph, visited, component);
                components.add(component);
            }
        }
        return components;
    }
}
