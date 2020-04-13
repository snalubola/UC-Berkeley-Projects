package gitlet;

import java.io.File;

/** Command for global log.
 *  @author Swadhin Nalubola
 */
public class GlobalLog extends Command {

    @Override
    public void run(Repo repo, String[] args) {
        if (!super.valid(args, 0, true)) {
            System.exit(0);
        }

        File[] commits = new File(repo.commitPath()).listFiles();
        for (File commit : commits) {
            CommitObject current = Utils.readObject(commit, CommitObject.class);
            System.out.println("===");
            System.out.println("commit " + current.getSHA());
            if (current.getParent2SHA() != null) {
                System.out.println("Merge: "
                        + current.getParentSHA().substring(0, 7)
                        + current.getParent2SHA().substring(0, 7));
            }
            System.out.println("Date: " + current.getTimeStamp());
            System.out.println(current.getLogMessage());
            System.out.println();
        }
    }
}
