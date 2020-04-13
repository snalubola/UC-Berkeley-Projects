package gitlet;

import java.io.File;

/** Parent class for commands.
 * @author Swadhin Nalubola
 */
public class Command {

    /**
     * Run the command.
     * @param repo repo
     * @param args string[]
     */
    public void run(Repo repo, String[] args) {

    }

    /**
     * Check the command is valid.
     * @param args string[]
     * @param argsNumber int
     * @param needsRepo boolean
     * @return boolean
     */
    public boolean valid(String[] args, int argsNumber, boolean needsRepo) {
        if (needsRepo) {
            if (!new File(".gitlet/").exists()) {
                System.out.println("Not in an initialized Gitlet directory.");
                return false;
            }
        }
        if (args.length != argsNumber) {
            System.out.println("Incorrect operands.");
            return false;
        }
        return true;
    }
}
