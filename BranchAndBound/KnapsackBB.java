import java.util.*;

// Class to represent an item with its value and weight
class Item {
    float weight;
    int value;
    int id;

    public Item(int value, float weight, int id) {
        this.value = value;
        this.weight = weight;
        this.id = id;
    }
}

// Node structure to store state-space tree search information
class Node {
    int level;      // Level of node in decision tree (or index in arr[])
    int profit;     // Profit of nodes on path from root to this node
    int bound;      // Upper bound of maximum profit in subtree of this node
    float weight;   // Total weight at this node

    public Node(int level, int profit, float weight) {
        this.level = level;
        this.profit = profit;
        this.weight = weight;
    }
}

public class KnapsackBB {
    
    // Greedy upper bound function using Fractional Knapsack logic
    static int bound(Node u, int n, int W, Item[] arr) {
        if (u.weight >= W) return 0;

        int profit_bound = u.profit;
        int j = u.level + 1;
        float total_weight = u.weight;

        // Fill greedily with full items
        while ((j < n) && (total_weight + arr[j].weight <= W)) {
            total_weight += arr[j].weight;
            profit_bound += arr[j].value;
            j++;
        }

        // Add fractional part of the next item for the bound calculation
        if (j < n) {
            profit_bound += (W - total_weight) * arr[j].value / arr[j].weight;
        }

        return profit_bound;
    }

    public static int solve(int W, Item[] arr, int n) {
        // Sort items by value/weight ratio for better pruning efficiency
        Arrays.sort(arr, (a, b) -> Double.compare((double) b.value / b.weight, (double) a.value / a.weight));

        // Priority Queue to explore nodes with highest upper bound first
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> b.bound - a.bound);

        Node root = new Node(-1, 0, 0);
        root.bound = bound(root, n, W, arr);
        pq.add(root);

        int maxProfit = 0;

        while (!pq.isEmpty()) {
            Node u = pq.poll();

            // Only explore if the node's bound is better than the current max profit
            if (u.bound > maxProfit) {
                int nextLevel = u.level + 1;

                if (nextLevel < n) {
                    // Scenario 1: Include next item
                    Node include = new Node(nextLevel, u.profit + arr[nextLevel].value, u.weight + arr[nextLevel].weight);
                    
                    if (include.weight <= W && include.profit > maxProfit) {
                        maxProfit = include.profit;
                    }
                    
                    include.bound = bound(include, n, W, arr);
                    if (include.bound > maxProfit) {
                        pq.add(include);
                    }

                    // Scenario 2: Exclude next item
                    Node exclude = new Node(nextLevel, u.profit, u.weight);
                    exclude.bound = bound(exclude, n, W, arr);
                    
                    if (exclude.bound > maxProfit) {
                        pq.add(exclude);
                    }
                }
            }
        }
        return maxProfit;
    }

    public static void main(String[] args) {
        int W = 10;
        Item[] arr = {
            new Item(40, 2, 1),
            new Item(50, 3.14f, 2),
            new Item(100, 1.98f, 3),
            new Item(95, 5, 4),
            new Item(30, 3, 5)
        };
        
        System.out.println("Maximum Profit: " + solve(W, arr, arr.length));
    }
}
