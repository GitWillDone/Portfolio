#include <iostream>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <memory.h>
#include <errno.h>
#include <readline/readline.h>
#include <readline/history.h>
#include "shelpers.h"

/**
 * shellProg functions similar to a terminal shell.  It will take the commands of the output and use the execvp routine
 * to execute them.
 * Built-ints:  Because this is barebones, the only built in supported is cd.
 * Features:
 *  - tab completion - because this utilizes readline, the user will need to have the readline library path exported
 *  - piping is supported in the program
 *  - the program can handle multiple flags for commands.  I.e., cat fileName.txt | tail -n 5
 */
int main() {
    //start by grabbing the command from the user
    std::cout << "Please enter the command(s) you want executed: \n";
    std::string commandStr;
    rl_bind_key('\t', rl_complete); //look at history, instead of ll

    //get commands from the user
    while (!(commandStr = readline("> ")).empty()) {
        if (commandStr == "exit") break;

        std::vector<std::string> tokens = tokenize(commandStr);
        std::vector<Command> commands = getCommands(tokens);
        std::vector<pid_t> pids;

        for (int i = 0; i < commands.size(); ++i) {
            if (commands[i].exec == "cd") {
                if (commands[i].argv.size() == 2) {
                    if (chdir(getenv("HOME")) < 0) perror("error changing to home directory");
                } else {
                    if (chdir(commands[i].argv[1]) < 0) perror("error changing directory");
                }
            } else {
                pid_t pid = fork();
                pids.push_back(pid);

                if (pid < 0) {
                    perror("Fork error\n");
                    break;
                }

                if (pid == 0) {
                    for (int j = 0; j < commands.size(); ++j) {
                        if (j != i) {
                            close(commands[j].fdStdin);
                            close(commands[j].fdStdout);
                        }
                    }

                    if (dup2(commands[i].fdStdin, 0) < 0) exit(1);
                    if (dup2(commands[i].fdStdout, 1) < 0) exit(1);
                    if (execvp(commands[i].exec.c_str(), const_cast<char **>(&commands[i].argv[0])) < 0) exit(1);

                } else {
                    if (commands[i].fdStdin != 0) close(commands[i].fdStdin);
                    if (commands[i].fdStdout != 1) close(commands[i].fdStdout);

                }
            }
        }

        //wait for every process here or else zombies will be created and prompt will show before output
        for (pid_t currPID: pids) waitpid(currPID, 0, 0);
    }
    return 0;
}


