package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/** Stage object.
 * @author Swadhin Nalubola
 */
public class Stage implements Serializable {

    /**
     * Initialize stage.
     */
    public Stage() {
        _commits = new HashMap<>();
        _tracked = new HashMap<>();
        _staged = new HashMap<>();
        _removed = new HashMap<>();
        _branches = new HashMap<>();
    }

    /**
     * stage a file.
     * @param fileName string
     * @param sha1 string
     */
    public void stage(String fileName, String sha1) {
        _staged.put(fileName, sha1);
    }

    /**
     * unstage a file.
     * @param fileName string
     * @param sha1 string
     */
    public void unStage(String fileName, String sha1) {
        if (_staged.containsKey(fileName)) {
            _staged.remove(fileName);
        }
    }

    /**
     * track a file.
     * @param fileName string
     * @param sha1 string
     */
    public void track(String fileName, String sha1) {
        _tracked.put(fileName, sha1);
    }

    /**
     * untrack a file.
     * @param fileName string
     * @param sha1 string
     */
    public void unTrack(String fileName, String sha1) {
        if (_tracked.containsKey(fileName)) {
            _tracked.remove(fileName);
        }
    }

    /**
     * remove a file.
     * @param fileName string
     * @param sha1 string
     */
    public void remove(String fileName, String sha1) {
        _removed.put(fileName, sha1);
        unTrack(fileName, sha1);
        unStage(fileName, sha1);
    }

    /**
     * unremove a file.
     * @param fileName string
     * @param sha1 string
     */
    public void unRemove(String fileName, String sha1) {
        if (_removed.containsKey(fileName)) {
            _removed.remove(fileName);
        }
    }

    /**
     * add a branch.
     * @param branch string
     * @param sha1 string
     */
    public void addBranch(String branch, String sha1) {
        _branches.put(branch, sha1);
    }

    /**
     * get commits.
     * @return hashmap
     */
    public HashMap<String, CommitObject> getCommits() {
        return _commits;
    }

    /**
     * add commit.
     * @param commitObject commitobject
     */
    public void addCommit(CommitObject commitObject) {
        _commits.put(commitObject.getSHA(), commitObject);
    }

    /**
     * get tracked files.
     * @return hashmap
     */
    public HashMap<String, String> getTracked() {
        return _tracked;
    }

    /**
     * is a file tracked.
     * @param fileName string
     * @return boolean
     */
    public boolean isTracked(String fileName) {
        return _tracked.containsKey(fileName);
    }

    /**
     * get staged files.
     * @return hashmap
     */
    public HashMap<String, String> getStaged() {
        return _staged;
    }

    /**
     * is a file staged.
     * @param fileName string
     * @return boolean
     */
    public boolean isStaged(String fileName) {
        return _staged.containsKey(fileName);
    }

    /**
     * is the stage empty.
     * @return boolean
     */
    public boolean isStageEmpty() {
        return _staged.isEmpty();
    }

    /**
     * get removed files.
     * @return hashmap
     */
    public HashMap<String, String> getRemoved() {
        return _removed;
    }

    /**
     * empty the stage.
     */
    public void emptyStage() {
        _staged = new HashMap<>();
        _removed = new HashMap<>();
    }

    /**
     * get branches.
     * @return hashmap
     */
    public HashMap<String, String> getBranches() {
        return _branches;
    }

    /**
     * get head.
     * @return string
     */
    public String getHead() {
        return _head;
    }

    /**
     * set the head.
     * @param commitsha string
     */
    public void setHead(String commitsha) {
        _head = commitsha;
    }

    /**
     * get the current branch.
     * @return string
     */
    public String getBranch() {
        return _branch;
    }

    /**
     * set the branch.
     * @param branch string
     */
    public void setBranch(String branch) {
        _branch = branch;
    }

    /** hashmap for commits. */
    private HashMap<String, CommitObject> _commits;

    /** Follows the form of filename, sha1. */
    private HashMap<String, String> _tracked;
    /** Follows the form of filename, sha1. */
    private HashMap<String, String> _staged;
    /** Follows the form of filename, sha1. */
    private HashMap<String, String> _removed;
    /** Follows the form of branchname, sha1 of latest commit. */
    private HashMap<String, String> _branches;
    /** the head. */
    private String _head;
    /** the branch. */
    private String _branch;

}
