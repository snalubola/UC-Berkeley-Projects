package gitlet;

import java.io.File;

/** Command for remove.
 * @author Swadhin Nalubola
 */
public class Remove extends Command {

    @Override
    public void run(Repo repo, String[] args) {
        if (!super.valid(args, 1, true)) {
            System.exit(0);
        }

        Stage stage = Utils.readObject(repo.stageFile(), Stage.class);
        String toRemove = args[0];
        if (!stage.isStaged(toRemove) && !stage.isTracked(toRemove)) {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        if (stage.isTracked(toRemove)) {
            String sha = stage.getTracked().get(toRemove);
            stage.remove(toRemove, sha);
            stage.unTrack(toRemove, sha);
            new File(toRemove).delete();
        } else if (stage.isStaged(toRemove)) {
            String sha = stage.getStaged().get(toRemove);
            stage.unStage(toRemove, sha);
        }

        Utils.writeObject(repo.stageFile(), stage);
    }
}
