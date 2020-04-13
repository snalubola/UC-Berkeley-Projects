package gitlet;

import java.io.File;

/** Command for find.
 *  @author Swadhin Nalubola
 */
public class Find extends Command {

    @Override
    public void run(Repo repo, String[] args) {
        if (!super.valid(args, 1, true)) {
            System.exit(0);
        }

        String message = args[0];
        File[] commits = new File(repo.commitPath()).listFiles();
        int found = 0;
        for (File commit : commits) {
            CommitObject current = Utils.readObject(commit, CommitObject.class);
            if (current.getLogMessage().equals(message)) {
                System.out.println(current.getSHA());
                found += 1;
            }
        }

        if (found == 0) {
            System.out.println("Found no commit with that message.");
        }
    }
}
