# Round Robin CPU Scheduling with Priority - README

Overview:
This project simulates a Round Robin CPU scheduling algorithm combined with Priority scheduling. The program reads a list of processes from a CSV file and schedules them based on their priority and a given time quantum.

Files:
RoundRobinScheduler.java — Java source code implementing the scheduler.
processes.csv — Sample input file with process details (PID, Arrival Time, Burst Time, Priority).

Input File Format (processes.csv):
<img width="302" alt="image" src="https://github.com/user-attachments/assets/eb6d83c1-51d2-49f0-8e9b-49ddfb51d03a" />

pid: Process ID (integer)
arrival: Arrival time of the process (integer)
burst: CPU burst time required (integer)
priority: Priority of the process (integer, lower number = higher priority)

Running the Program on GitHub Codespaces

Open your GitHub repository in Codespaces. In this case : https://github.com/geniusruler/roundrobincpu
Place your RoundRobinScheduler.java and processes.csv files in the workspace. Just how I added in my github repo then
Open the terminal in Codespaces.
Compile the Java program:
  javac RoundRobinScheduler.java

Run the program with two arguments:
Path to the process CSV file
Time quantum (integer) after the path to csv file
Ex: java RoundRobinScheduler processes.csv 2

View the output in the terminal.
