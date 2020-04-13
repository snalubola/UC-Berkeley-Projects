package gitlet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/** Merge command.
 * @author Swadhin Nalubola
 */
public class Merge extends Command {

    @Override
    public void run(Repo repo, String[] args) {
        if (!super.valid(args, 1, true)) {
            System.exit(0);
        }
        Stage stage = Utils.readObject(repo.stageFile(), Stage.class);
        String currentBranch = stage.getBranch();
        String givenBranch = args[0];
        if (!stage.getStaged().isEmpty() || !stage.getRemoved().isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        } else if (!stage.getBranches().containsKey(givenBranch)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (givenBranch.equals(currentBranch)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        File[] workingDirectoryFiles =
                new File(repo.getWorkingDirectory()).listFiles();
        for (File file : workingDirectoryFiles) {
            String fileName = file.getName();
            if (file.isFile() && !stage.getStaged().containsKey(fileName)
                    && !stage.getTracked().containsKey(fileName)) {
                System.out.println("There is an untracked file in "
                        + "the way; delete it or add it first.");
                System.exit(0);
            }
        }
        String currBranchSHA = stage.getHead();
        String giveBranchSHA = stage.getBranches().get(givenBranch);
        CommitObject currBraCommit = Utils.readObject(new File(repo.commitPath()
                        + currBranchSHA + ".ser"), CommitObject.class);
        CommitObject giveBraCommit =
                Utils.readObject(new File(repo.commitPath()
                        + giveBranchSHA + ".ser"), CommitObject.class);
        String latestAnce = findSplit(currBraCommit, giveBraCommit, stage);
        CommitObject latestAncestor =
                Utils.readObject(new File(repo.commitPath()
                        + latestAnce + ".ser"), CommitObject.class);
        if (latestAncestor.equals(giveBraCommit)) {
            System.out.println("Given branch is an "
                    + "ancestor of the current branch.");
            System.exit(0);
        } else if (latestAncestor.equals(currBraCommit)) {
            new Checkout().run(repo, new String[]{givenBranch});
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }
        Utils.writeObject(repo.stageFile(), stage);
        merger(repo, latestAncestor, currBraCommit, giveBraCommit);
        String[] message = new String[]{"Merged "
                + givenBranch + " into " + currentBranch + "."};
        new Commit().run(repo, message);
        stage.getCommits().get(stage.getHead()).
                setParent2SHA(giveBraCommit.getSHA());
        if (_mergeConflict) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /**
     * Finds latest ancestor.
     * @param current commitobject
     * @param given commitobject
     * @param stage stage
     * @return string
     */
    private String findSplit(CommitObject current,
                                   CommitObject given, Stage stage) {
        CommitObject currCopy = current;
        CommitObject giveCopy = given;
        ArrayList<String> parents = new ArrayList<>();
        while (giveCopy != null) {
            parents.add(giveCopy.getSHA());
            String parentSHA = giveCopy.getParentSHA();
            if (giveCopy.getParent2SHA() != null) {
                parents.add(giveCopy.getParent2SHA());
            }
            giveCopy = stage.getCommits().get(parentSHA);
        }
        while (currCopy != null) {
            if (parents.contains(currCopy.getSHA())) {
                return currCopy.getSHA();
            }
            String parentSHA = currCopy.getParentSHA();
            if (currCopy.getParent2SHA() != null) {
                if (parents.contains(currCopy.getParent2SHA())) {
                    return currCopy.getParent2SHA();
                }
            }
            currCopy = stage.getCommits().get(parentSHA);
        }
        return null;
    }

    /**
     * Compare a file between two commitobjects.
     * @param older commitobject
     * @param newer commitobject
     * @param fileName string
     * @return string
     */
    private String compare(CommitObject older,
                           CommitObject newer, String fileName) {
        HashMap<String, String> olderBlobs = older.getTrackedBlobs();
        HashMap<String, String> newerBlobs = newer.getTrackedBlobs();

        if (!olderBlobs.containsKey(fileName)) {
            return "created";
        } else if (!newerBlobs.containsKey(fileName)) {
            return "deleted";
        } else {
            String oldBlobSHA = olderBlobs.get(fileName);
            String newBlobSHA = newerBlobs.get(fileName);
            if (!oldBlobSHA.equals(newBlobSHA)) {
                return "modified";
            } else {
                return "unmodified";
            }
        }
    }

    /**
     * Updates contents of fileName for merge conflicts.
     * @param repo repo
     * @param fileName string
     * @param currBraBlobs hashmap
     * @param giveBraBlobs hashmap
     */
    private void updateContents(Repo repo, String fileName,
                                HashMap<String, String> currBraBlobs,
                                HashMap<String, String> giveBraBlobs) {
        _mergeConflict = true;
        byte[] head = "<<<<<<< HEAD\n".getBytes();
        byte[] middle = "=======\n".getBytes();
        byte[] tail = ">>>>>>>\n".getBytes();
        byte[] curr = null;
        byte[] give = null;
        String currSHA = currBraBlobs.get(fileName);
        if (currSHA != null) {
            Blob currBlob = Utils.readObject(new File(
                    repo.blobPath() + currSHA + ".ser"), Blob.class);
            curr = currBlob.getContents();
        }
        String giveSHA = giveBraBlobs.get(fileName);
        if (giveSHA != null) {
            Blob giveBlob = Utils.readObject(new File(
                    repo.blobPath() + giveSHA + ".ser"), Blob.class);
            give = giveBlob.getContents();
        }
        Utils.writeContents(new File(fileName), head, curr, middle, give, tail);
    }

    /**
     * Merges things.
     * @param repo repo
     * @param latestAncestor commitobject
     * @param currBraCommit commitobject
     * @param giveBraCommit commitobject
     */
    private void merger(Repo repo, CommitObject latestAncestor, CommitObject
            currBraCommit, CommitObject giveBraCommit) {
        HashMap<String, String> latestABlobs = latestAncestor.getTrackedBlobs();
        HashMap<String, String> currBraBlobs = currBraCommit.getTrackedBlobs();
        HashMap<String, String> giveBraBlobs = giveBraCommit.getTrackedBlobs();

        for (String fileName : giveBraBlobs.keySet()) {
            if (!currBraBlobs.containsKey(fileName)) {
                if (!latestABlobs.containsKey(fileName)) {
                    String[] args = new
                            String[]{giveBraCommit.getSHA(), "--", fileName};
                    new Checkout().run(repo, args);
                    new Add().run(repo, new String[]{fileName});
                }
            } else if (compare(latestAncestor, giveBraCommit,
                    fileName).equals("modified")) {
                if (compare(latestAncestor, currBraCommit,
                        fileName).equals("unmodified")) {
                    String[] args = new
                            String[]{giveBraCommit.getSHA(), "--", fileName};
                    new Checkout().run(repo, args);
                    new Add().run(repo, new String[]{fileName});
                } else if (compare(latestAncestor, currBraCommit,
                        fileName).equals("deleted")) {
                    updateContents(repo, fileName, currBraBlobs, giveBraBlobs);
                }
            }
        }

        for (String fileName : currBraBlobs.keySet()) {
            if (compare(latestAncestor, currBraCommit,
                    fileName).equals("unmodified")) {
                if (compare(latestAncestor, giveBraCommit,
                        fileName).equals("deleted")) {
                    new Remove().run(repo, new String[]{fileName});
                }
            } else if (giveBraBlobs.containsKey(fileName)) {
                if (!currBraBlobs.get(fileName).equals(
                        giveBraBlobs.get(fileName))) {
                    updateContents(repo, fileName, currBraBlobs, giveBraBlobs);
                }
            } else if (!giveBraBlobs.containsKey(fileName)) {
                if (compare(latestAncestor, currBraCommit,
                        fileName).equals("modified")) {
                    updateContents(repo, fileName, currBraBlobs, giveBraBlobs);
                }
            }
        }
    }

    /** Boolean for whether a merge conflict occurred. */
    private boolean _mergeConflict;
}
