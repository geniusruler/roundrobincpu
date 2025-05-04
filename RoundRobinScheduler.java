
import java.io.*;
import java.util.*;

class Process {
    int pid;
    int remaining_time;
    int completion_time;
    int arrival_time;
    int burst_time;
    int startTime = -1;
    int waitingTime;
    int turnaroundTime;
    int responseTime = -1;
    int priority;  

    Process(int pid, int arrival_time, int burst_time, int priority) {
        this.pid = pid;
        this.arrival_time = arrival_time;
        this.burst_time = burst_time;
        this.remaining_time = burst_time;
        this.priority = priority;
    }
}

public class RoundRobinScheduler {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java RoundRobinScheduler <process_file.csv> <time_quantum>");
            System.exit(1);
        }

        String filePath = args[0];
        int timeQuantum = Integer.parseInt(args[1]);

        List<Process> processes = readProcesses(filePath);
        roundRobinWithPriority(processes, timeQuantum);
    }

    static List<Process> readProcesses(String filePath) {
        List<Process> processes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (firstLine && line.toLowerCase().startsWith("pid")) {
                    firstLine = false;
                    continue; 
                }
                String[] parts = line.split(",");
                int pid = Integer.parseInt(parts[0].trim());
                int arrival = Integer.parseInt(parts[1].trim());
                int burst = Integer.parseInt(parts[2].trim());
                int priority = Integer.parseInt(parts[3].trim());
                processes.add(new Process(pid, arrival, burst, priority));
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            System.exit(1);
        }
        return processes;
    }

    static void roundRobinWithPriority(List<Process> processes, int timeQuantum) {
        int time = 0;
        int completed = 0;
        int n = processes.size();
        int contextSwitches = 0;
        int cpuIdleTime = 0;
        int contextSwitchTime = 0; 

        
        processes.sort(Comparator.comparingInt(p -> p.arrival_time));

       
        Map<Integer, Queue<Process>> priorityQueues = new TreeMap<>();

        int i = 0; // index for processes to add to queues

        while (completed < n) {
            
            while (i < n && processes.get(i).arrival_time <= time) {
                Process p = processes.get(i);
                priorityQueues.putIfAbsent(p.priority, new LinkedList<>());
                priorityQueues.get(p.priority).add(p);
                i++;
            }

            
            Process current = null;
            Integer currentPriority = null;
            for (Map.Entry<Integer, Queue<Process>> entry : priorityQueues.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    currentPriority = entry.getKey();
                    current = entry.getValue().poll();
                    break;
                }
            }

            if (current != null) {
                if (current.responseTime == -1) {
                    current.responseTime = time - current.arrival_time;
                }

                int runTime = Math.min(timeQuantum, current.remaining_time);
                time += runTime;
                current.remaining_time -= runTime;

                
                while (i < n && processes.get(i).arrival_time <= time) {
                    Process p = processes.get(i);
                    priorityQueues.putIfAbsent(p.priority, new LinkedList<>());
                    priorityQueues.get(p.priority).add(p);
                    i++;
                }

                if (current.remaining_time == 0) {
                    current.completion_time = time;
                    completed++;
                } else {
                    
                    priorityQueues.get(currentPriority).add(current);
                }

                
                boolean queuesNotEmpty = priorityQueues.values().stream().anyMatch(q -> !q.isEmpty());
                if (queuesNotEmpty) {
                    contextSwitches++;
                    time += contextSwitchTime;
                }
            } else {
                
                time++;
                cpuIdleTime++;
            }
        }

        
        int totalTurnaround = 0;
        int totalWaiting = 0;
        int totalResponse = 0;

        for (Process p : processes) {
            p.turnaroundTime = p.completion_time - p.arrival_time;
            p.waitingTime = p.turnaroundTime - p.burst_time;
            totalTurnaround += p.turnaroundTime;
            totalWaiting += p.waitingTime;
            totalResponse += p.responseTime;
        }

        double cpuUtilization = 1.0 - ((double)(contextSwitchTime * contextSwitches + cpuIdleTime) / time);
        double throughput = (double)n / time;
        double avgWaiting = (double)totalWaiting / n;
        double avgTurnaround = (double)totalTurnaround / n;
        double avgResponse = (double)totalResponse / n;

        System.out.println("PID\tArrival\tBurst\tPriority\tCompletion\tTurnaround\tWaiting\tResponse");
        for (Process p : processes) {
            System.out.printf("%d\t%d\t%d\t%d\t\t%d\t\t%d\t\t%d\t%d\n",
                p.pid, p.arrival_time, p.burst_time, p.priority, p.completion_time,
                p.turnaroundTime, p.waitingTime, p.responseTime);
        }

        System.out.printf("\nCPU Utilization: %.2f\n", cpuUtilization);
        System.out.printf("Throughput: %.2f processes/unit time\n", throughput);
        System.out.printf("Average Waiting Time: %.2f\n", avgWaiting);
        System.out.printf("Average Turnaround Time: %.2f\n", avgTurnaround);
        System.out.printf("Average Response Time: %.2f\n", avgResponse);
        System.out.printf("Context Switches: %d\n", contextSwitches);
    }
}