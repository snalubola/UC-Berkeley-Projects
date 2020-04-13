package gitlet;

import java.util.HashMap;

/** Command for log.
 * @author Swadhin Nalubola
 */
public class Log extends Command {

    @Override
    public void run(Repo repo, String[] args) {
        if (!super.valid(args, 0, false)) {
            System.exit(0);
        }

        Stage stage = Utils.readObject(repo.stageFile(), Stage.class);
        HashMap<String, CommitObject> commits = stage.getCommits();

        if (stage.getHead() != null) {
            CommitObject current = commits.get(stage.getHead());
            while (current != null) {
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
                current = commits.get(current.getParentSHA());
            }
        }
    }
}
