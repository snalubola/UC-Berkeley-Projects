package gitlet;

/** Command for removebranch.
 *  @author Swadhin Nalubola
 */
public class RemoveBranch extends Command {

    @Override
    public void run(Repo repo, String[] args) {
        if (!super.valid(args, 1, true)) {
            System.exit(0);
        }

        Stage stage = Utils.readObject(repo.stageFile(), Stage.class);
        String removeBranch = args[0];
        String currentBranch = stage.getBranch();

        if (!stage.getBranches().containsKey(removeBranch)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (removeBranch.equals(currentBranch)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        stage.getBranches().remove(removeBranch);

        Utils.writeObject(repo.stageFile(), stage);
    }
}
