package gitlet;

import java.io.File;

/** Command to add files in gitlet.
 *  @author Swadhin Nalubola
 */
public class Add extends Command {

    @Override
    public void run(Repo repo, String[] args) {
        if (!super.valid(args, 1, true)) {
            System.exit(0);
        }

        boolean modified = true;
        String fileName = args[0];
        File toAdd = new File(fileName);
        if (!toAdd.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }

        Stage stage = Utils.readObject(repo.stageFile(), Stage.class);
        String newFileSHA = Utils.sha1(Utils.readContents(toAdd));
        String headCommitSHA = stage.getHead();

        if (headCommitSHA != null) {
            CommitObject headCommit =
                    Utils.readObject(new File(repo.commitPath()
                            + headCommitSHA + ".ser"), CommitObject.class);
            if (headCommit != null
                    && headCommit.getTrackedBlobs().containsKey(fileName)) {
                String oldFileSHA = headCommit.getTrackedBlobs().get(fileName);
                modified = !oldFileSHA.equals(newFileSHA);
            }
        }

        if (stage.getRemoved().containsKey(fileName)) {
            stage.unRemove(fileName, newFileSHA);
        } else if (modified) {
            stage.stage(fileName, newFileSHA);
            Blob blob = new Blob(newFileSHA, fileName,
                    Utils.readContents(toAdd));
            Utils.writeObject(new File(repo.blobPath()
                    + newFileSHA + ".ser"), blob);
        }

        Utils.writeObject(repo.stageFile(), stage);
    }
}
