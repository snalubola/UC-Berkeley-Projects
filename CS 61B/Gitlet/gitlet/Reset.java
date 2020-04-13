package gitlet;

import java.io.File;
import java.util.HashMap;

/** Command for reset.
 * @author Swadhin Nalubola
 */
public class Reset extends Command {

    @Override
    public void run(Repo repo, String[] args) {
        if (!super.valid(args, 1, true)) {
            System.exit(0);
        }

        Stage stage = Utils.readObject(repo.stageFile(), Stage.class);
        String commitSHA = args[0];
        if (commitSHA.length() < 10) {
            for (String fullSHA : stage.getCommits().keySet()) {
                if (commitSHA.substring(0, 5).equals(fullSHA.substring(0, 5))) {
                    commitSHA = fullSHA;
                }
            }
        }
        if (!stage.getCommits().keySet().contains(commitSHA)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        CommitObject commit = Utils.readObject(new File(
                repo.commitPath() + commitSHA + ".ser"), CommitObject.class);
        HashMap<String, String> blobs = commit.getTrackedBlobs();
        File[] workingDirectoryFiles =
                new File(repo.getWorkingDirectory()).listFiles();
        for (File file : workingDirectoryFiles) {
            String fileName = file.getName();
            if (file.isFile() && blobs.containsKey(fileName)
                    && !stage.getStaged().containsKey(fileName)
                    && !stage.getTracked().containsKey(fileName)) {
                String fileSHA = Utils.sha1(Utils.readContents(file));
                if (!fileSHA.equals(blobs.get(fileName))) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it or add it first.");
                    System.exit(0);
                }
            }
        }
        for (File file : workingDirectoryFiles) {
            String fileName = file.getName();
            if (stage.getTracked().containsKey(fileName)
                    && !blobs.containsKey(fileName)) {
                String fileSHA = stage.getTracked().get(fileName);
                stage.unTrack(fileName, fileSHA);
                Utils.restrictedDelete(file);
            }
        }
        for (String blobFileName : blobs.keySet()) {
            String blobSHA = blobs.get(blobFileName);
            Blob blob = Utils.readObject(new File(
                    repo.blobPath() + blobSHA + ".ser"), Blob.class);
            String blobName = blob.getName();
            byte[] newFileContents = blob.getContents();
            Utils.writeContents(new File(blobName), newFileContents);
            stage.track(blobName, blob.getSHA1());
        }
        stage.emptyStage();
        stage.setHead(commit.getSHA());
        stage.addBranch(commit.getBranch(), commit.getSHA());
        Utils.writeObject(repo.stageFile(), stage);
    }
}
