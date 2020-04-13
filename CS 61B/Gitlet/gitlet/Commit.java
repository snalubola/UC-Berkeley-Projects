package gitlet;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;

/** Command for commit.
 * @author Swadhin Nalubola
 */
public class Commit extends Command {

    @Override
    public void run(Repo repo, String[] args) {

        if (!super.valid(args, 1, true)) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }
        if (args[0].equals("")) {
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        Stage stage = Utils.readObject(repo.stageFile(), Stage.class);

        if (stage.isStageEmpty() && stage.getHead()
                != null && stage.getRemoved().isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        for (String fileName : stage.getStaged().keySet()) {
            String blobSHA = stage.getStaged().get(fileName);
            stage.track(fileName, blobSHA);
        }
        stage.emptyStage();

        String timestamp = new SimpleDateFormat(
                "EEE MMM dd hh:mm:ss YYYY Z").format(new Date());
        String logMessage = args[0];
        String sha1 = Utils.sha1(timestamp + logMessage);
        String parentSHA = stage.getHead();
        String branch = stage.getBranch();

        CommitObject thisCommit =
                new CommitObject(sha1, logMessage, timestamp,
                        parentSHA, null, branch, stage);
        stage.setHead(thisCommit.getSHA());
        stage.addBranch(branch, thisCommit.getSHA());
        stage.addCommit(thisCommit);

        File thisCommitFile = new File(repo.commitPath()
                + thisCommit.getSHA() + ".ser");
        Utils.writeObject(thisCommitFile, thisCommit);

        Utils.writeObject(repo.stageFile(), stage);
    }
}
