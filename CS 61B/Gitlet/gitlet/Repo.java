package gitlet;

import java.io.File;

/** Repository in gitlet.
 * @author Swadhin Nalubola
 */
public class Repo {

    /**
     * initialize repo.
     * @param workingDirectory string
     */
    public Repo(String workingDirectory) {
        _workingDirectory = workingDirectory;
    }

    /**
     * INITIALIZE repo.
     */
    public void init() {
        if (new File(_gitletPath).exists()) {
            System.out.println("A Gitlet version-control "
                    + "system already exists in the current directory.");
            System.exit(0);
        }

        new File(_gitletPath).mkdirs();
        new File(_commitPath).mkdirs();
        new File(_blobPath).mkdirs();
        new File(_stagePath).mkdirs();

        _stage = new Stage();
        _stage.setHead(null);
        _stage.setBranch("master");
        _stage.addBranch("master", null);
        Utils.writeObject(_stageFile, _stage);

        Commit commit = new Commit();
        commit.run(this, new String[]{"initial commit"});
    }

    /**
     * get working direcotry.
     * @return string
     */
    public String getWorkingDirectory() {
        return _workingDirectory;
    }

    /**
     * gitlet path.
     * @return string
     */
    public String gitletPath() {
        return _gitletPath;
    }

    /**
     * commit path.
     * @return string
     */
    public String commitPath() {
        return _commitPath;
    }

    /**
     * blob path.
     * @return string
     */
    public String blobPath() {
        return _blobPath;
    }

    /**
     * stage path.
     * @return string
     */
    public String stagePath() {
        return _stagePath;
    }

    /**
     * stage file.
     * @return file
     */
    public File stageFile() {
        return _stageFile;
    }
    /** working directory. */
    private String _workingDirectory;
    /** path for gitlet. */
    private final String _gitletPath = ".gitlet/";
    /** path for commits. */
    private final String _commitPath = ".gitlet/commit/";
    /** path for blobs. */
    private final String _blobPath = ".gitlet/blobs/";
    /** path for stages. */
    private final String _stagePath = ".gitlet/stage/";
    /** path for stage file. */
    private final File _stageFile = new File(".gitlet/stage/staging.ser");
    /** the stage. */
    private Stage _stage;
}
