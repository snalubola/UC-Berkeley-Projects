package gitlet;

import java.io.File;
import java.util.HashMap;

/** Command for checkout in gitlet.
 * @author Swadhin Nalubola
 */
public class Checkout extends Command {

    @Override
    public void run(Repo repo, String[] args) {
        if (args.length ==  1) {
            checkoutBranch(repo, args);
        } else if (args.length == 2) {
            checkoutFile(repo, args);
        } else if (args.length == 3) {
            checkoutFileWithCommit(repo, args);
        } else {
            System.exit(0);
        }
    }

    /**
     * Checkout file.
     * @param repo repo
     * @param args string[]
     */
    public void checkoutFile(Repo repo, String[] args) {

        Stage stage = Utils.readObject(repo.stageFile(), Stage.class);
        String fileName = args[1];
        String headCommitSHA = stage.getHead();

        if (headCommitSHA != null) {
            CommitObject headCommit =
                    Utils.readObject(new File(repo.commitPath()
                            + headCommitSHA + ".ser"), CommitObject.class);
            HashMap<String, String> blobs = headCommit.getTrackedBlobs();
            if (!blobs.containsKey(fileName)) {
                System.out.println("File does not exist in that commit.");
                System.exit(0);
            }

            String fileSHA = blobs.get(fileName);
            Blob blob = Utils.readObject(new File(repo.blobPath()
                    + fileSHA + ".ser"), Blob.class);
            String blobName = blob.getName();
            byte[] newFileContents = blob.getContents();
            Utils.writeContents(new File(blobName), newFileContents);

            if (stage.getStaged().containsKey(fileName)) {
                stage.unStage(fileName, fileSHA);
            }
        }

        Utils.writeObject(repo.stageFile(), stage);
    }

    /**
     * Checkout file with commit.
     * @param repo repo
     * @param args string[]
     */
    public void checkoutFileWithCommit(Repo repo, String[] args) {

        if (!args[1].equals("--")) {
            System.out.println("Incorrect operands.");
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
        String fileName = args[2];

        if (!stage.getCommits().keySet().contains(commitSHA)) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }

        CommitObject newCommit =
                Utils.readObject(new File(repo.commitPath()
                        + commitSHA + ".ser"), CommitObject.class);
        HashMap<String, String> blobs = newCommit.getTrackedBlobs();
        if (!blobs.containsKey(fileName)) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }

        String fileSHA = blobs.get(fileName);
        Blob blob = Utils.readObject(new File(repo.blobPath()
                + fileSHA + ".ser"), Blob.class);
        byte[] newFileContents = blob.getContents();
        Utils.writeContents(new File(fileName), newFileContents);

        if (stage.getStaged().containsKey(fileName)) {
            stage.unStage(fileName, fileSHA);
        }

        Utils.writeObject(repo.stageFile(), stage);
    }

    /**
     * Checkout branch.
     * @param repo repo
     * @param args string[]
     */
    public void checkoutBranch(Repo repo, String[] args) {
        Stage stage = Utils.readObject(repo.stageFile(), Stage.class);
        String checkoutBranch = args[0];
        String currentBranch = stage.getBranch();

        if (!stage.getBranches().containsKey(checkoutBranch)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else if (checkoutBranch.equals(currentBranch)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        String checkoutCommitSHA = stage.getBranches().get(checkoutBranch);
        CommitObject checkoutCommit =
                Utils.readObject(new File(repo.commitPath()
                        + checkoutCommitSHA + ".ser"), CommitObject.class);
        HashMap<String, String> checkoutBlobs =
                checkoutCommit.getTrackedBlobs();
        File[] workingDirectoryFiles =
                new File(repo.getWorkingDirectory()).listFiles();
        for (File file : workingDirectoryFiles) {
            String fileName = file.getName();
            if (file.isFile() && checkoutBlobs.containsKey(fileName)
                    && !stage.getStaged().containsKey(fileName)
                    && !stage.getTracked().containsKey(fileName)) {
                String fileSHA = Utils.sha1(Utils.readContents(file));
                if (!fileSHA.equals(checkoutBlobs.get(fileName))) {
                    System.out.println("There is an untracked file in "
                            + "the way; delete it or add it first.");
                    System.exit(0);
                }
            }
        }
        for (File file : workingDirectoryFiles) {
            String fileName = file.getName();
            if (stage.getTracked().containsKey(fileName)
                    && !checkoutBlobs.containsKey(fileName)) {
                String fileSHA = stage.getTracked().get(fileName);
                stage.unTrack(fileName, fileSHA);
                Utils.restrictedDelete(file);
            }
        }
        boolean isBranchSame = checkoutCommitSHA != null
                && checkoutCommitSHA.equals(stage.getHead());
        if (!isBranchSame) {
            for (String blobFileName : checkoutBlobs.keySet()) {
                String blobSHA = checkoutBlobs.get(blobFileName);
                Blob blob = Utils.readObject(new
                        File(repo.blobPath() + blobSHA + ".ser"), Blob.class);
                byte[] newFileContents = blob.getContents();
                Utils.writeContents(new File(blobFileName), newFileContents);
                stage.track(blobFileName, blob.getSHA1());
            }
        }
        stage.emptyStage();
        stage.setHead(checkoutCommit.getSHA());
        stage.setBranch(checkoutBranch);
        stage.addBranch(checkoutBranch, checkoutCommit.getSHA());
        Utils.writeObject(repo.stageFile(), stage);
    }
}
