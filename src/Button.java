package src;

/**
 * Invoker class that executes each command pass in when pressed.
 */
public class Button {

    private Command currentCmd;

    /**
     * Sets the desired command to execute.
     *
     * @param cmd   command to execute
     */
    public void setCommand(Command cmd) {
        currentCmd = cmd;
    }

    /**
     * Executes the command.
     */
    public void press() {
        if (currentCmd != null) this.currentCmd.execute();
    }

}