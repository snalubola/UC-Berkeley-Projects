package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Objects;

/** Object for commits.
 * @author Swadhin Nalubola
 */
public class CommitObject implements Serializable {

    /**
     * Initialize CommitObject.
     * @param sha string
     * @param logMessage string
     * @param timestamp string
     * @param parentSHA string
     * @param parent2SHA string
     * @param branch string
     * @param stage stage
     */
    public CommitObject(String sha, String logMessage, String timestamp,
                        String parentSHA, String parent2SHA, String branch,
                        Stage stage) {
        _sha = sha;
        _logMessage = logMessage;
        _timestamp = timestamp;
        _parentSHA = parentSHA;
        _parent2SHA = parent2SHA;
        _branch = branch;
        _stage = stage;
    }

    /**
     * Get sha.
     * @return string
     */
    public String getSHA() {
        return _sha;
    }

    /** Get timestamp.
     * @return string
     */
    public String getTimeStamp() {
        return _timestamp;
    }

    /** Get log message.
     * @return string
     */
    public String getLogMessage() {
        return _logMessage;
    }

    /** Get parent sha.
     * @return string
     */
    public String getParentSHA() {
        return _parentSHA;
    }

    /** Get other parent sha.
     * @return string
     */
    public String getParent2SHA() {
        return _parent2SHA;
    }

    /**
     * set other parent sha.
     * @param parent2SHA string
     */
    public void setParent2SHA(String parent2SHA) {
        _parent2SHA = parent2SHA;
    }

    /** Get branch.
     * @return string
     */
    public String getBranch() {
        return _branch;
    }

    /** Get stage.
     * @return stage
     */
    public Stage getStage() {
        return _stage;
    }

    /** Get tracked blobs.
     * @return blobs
     */
    public HashMap<String, String> getTrackedBlobs() {
        return getStage().getTracked();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSHA());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        CommitObject commitObject = (CommitObject) obj;
        return this.getSHA().equals(commitObject.getSHA());
    }

    /** sha. */
    private final String _sha;
    /** logmessage. */
    private final String _logMessage;
    /** timestamp. */
    private final String _timestamp;
    /** parent sha. */
    private final String _parentSHA;
    /** other parent sha. */
    private String _parent2SHA;
    /** branch. */
    private final String _branch;
    /** stage. */
    private final Stage _stage;
}
