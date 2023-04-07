#!/bin/bash

# list of commands to run
commands=(
  "./mvnw -pl core-demo clean test"
  "./mvnw -pl acl-demo clean test"
  "cd auth-demo; ./test.sh"
)



# array to store PIDs of child terminal processes
# pids=()

# start each command in a new gnome terminal
for cmd in "${commands[@]}"; do
  gnome-terminal --tab -e "bash -c '$cmd; exec bash'" &
  # store PID of child process
  # pids+=($!)
done

# wait for all commands to finish
wait

# close terminals on user input
read -n 1 -s -r -p "Press any key to close terminals..."

#send SIGTERM signal to child processes
# for pid in "${pids[@]}"; do
#   kill -9 "$pid"
# done

# kill gnome-terminal processes
# killall gnome-terminal

# pkill -f "gnome-terminal"

# remove PIDs from array
# pids=()