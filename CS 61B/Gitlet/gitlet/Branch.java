package gitlet;

/** Command for branching.
 * @author Swadhin Nalubola
 */
public class Branch extends Command {

    @Override
    public void run(Repo repo, String[] args) {
        if (!super.valid(args, 1, true)) {
            System.exit(0);
        }

        Stage stage = Utils.readObject(repo.stageFile(), Stage.class);
        String branchName = args[0];

        if (stage.getBranches().containsKey(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        stage.addBranch(branchName, stage.getHead());

        Utils.writeObject(repo.stageFile(), stage);
    }
}
