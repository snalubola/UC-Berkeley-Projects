package gitlet;

import java.util.Arrays;
import java.util.HashMap;

/** Driver class for Gitlet, the tiny stupid version-control system.
 *  @author Swadhin Nalubola
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND> .... */
    public static void main(String... args) {

        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        _commands = new HashMap<>();
        _commands.put("init", new Init());
        _commands.put("commit", new Commit());
        _commands.put("add", new Add());
        _commands.put("log", new Log());
        _commands.put("checkout", new Checkout());
        _commands.put("rm", new Remove());
        _commands.put("global-log", new GlobalLog());
        _commands.put("find", new Find());
        _commands.put("status", new Status());
        _commands.put("branch", new Branch());
        _commands.put("rm-branch", new RemoveBranch());
        _commands.put("reset", new Reset());
        _commands.put("merge", new Merge());

        if (!_commands.containsKey(args[0])) {
            System.out.println("No command with that name exists.");
            System.exit(0);
        }
        Command command = _commands.get(args[0]);
        String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
        command.run(new Repo(System.getProperty("user.dir")), commandArgs);
    }

    /** Hashmap for all commands. */
    private static HashMap<String, Command> _commands;
}
