#!/bin/bash


if ! which gnome-terminal >/dev/null; then
  echo "gnome-terminal is not installed"
  sudo apt-get install -y gnome-terminal
fi



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
  # cant make it work any better... --dump-po-strings -- is only there in order to kill the right process if it finishes
  # gnome-terminal --tab --title="testing-terminal" -e "bash -c '$cmd; exec bash'" &
  gnome-terminal -e "bash -e -c 'export FOO=bar; $cmd; exec bash --dump-po-strings --'" &
  # store PID of child process
  # pids+=($!)
done

# wait for all commands to finish
wait

# close terminals on user input
read -n 1 -s -r -p "Press any key to close terminals..."

# pkill -f "gnome-terminal --title=testing-terminal"

#send SIGTERM signal to child processes
# for pid in "${pids[@]}"; do
#   kill -9 "$pid"
# done

# kill gnome-terminal processes
# killall gnome-terminal

# pkill -f "*--my-flag*"
pkill -f "export FOO=bar;"
pkill -f "bash --dump-po-strings --"

# remove PIDs from array
pids=()