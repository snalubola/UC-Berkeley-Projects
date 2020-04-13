package gitlet;

import java.io.Serializable;
import java.util.Objects;

/** Blobs for files in gitlet.
 * @author Swadhin Nalubola
 */
public class Blob implements Serializable {

    /**
     * Initialize Blob.
     * @param sha1 string
     * @param fileName string
     * @param fileContents byte[]
     */
    public Blob(String sha1, String fileName, byte[] fileContents) {
        _sha1 = sha1;
        _name = fileName;
        _contents = fileContents;
    }

    /**
     * Get sha1.
     * @return sha1.
     */
    public String getSHA1() {
        return _sha1;
    }

    /**
     * Get name.
     * @return name.
     */
    public String getName() {
        return _name;
    }

    /**
     * Get contents.
     * @return contents.
     */
    public byte[] getContents() {
        return _contents;
    }

    /**
     * Override hashcode.
     * @return hashcode.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getSHA1());
    }

    /**
     * Override equals.
     * @return boolean.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Blob blob = (Blob) obj;
        return this.getSHA1().equals(blob.getSHA1());
    }

    /** sha1 code for the blob. */
    private final String _sha1;

    /** name of the blob. */
    private final String _name;

    /** contents of the blob. */
    private final byte[] _contents;
}
