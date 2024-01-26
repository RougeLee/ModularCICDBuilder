package pipeline.common.interfaces

import pipeline.common.ssh.Command

interface ICommand {
    void addCommand(Command command)

    ArrayList<ArrayList<String>> executeCommand()

    ArrayList<ArrayList<String>> executeCommand(Command command)

    ArrayList<ArrayList<String>> executeCommands(ArrayList<Command> commands)
}
