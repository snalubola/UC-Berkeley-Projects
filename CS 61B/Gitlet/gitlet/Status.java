package gitlet;

/** Command for status.
 * @author Swadhin Nalubola
 */
public class Status extends Command {

    @Override
    public void run(Repo repo, String[] args) {
        if (!super.valid(args, 0, true)) {
            System.exit(0);
        }

        Stage stage = Utils.readObject(repo.stageFile(), Stage.class);
        System.out.println("=== Branches ===");
        System.out.println("*" + stage.getBranch());
        for (String branch : stage.getBranches().keySet()) {
            if (!branch.equals(stage.getBranch())) {
                System.out.println(branch);
            }
        }
        System.out.println();
        System.out.println("=== Staged Files ===");
        for (String fileName : stage.getStaged().keySet()) {
            System.out.println(fileName);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (String fileName : stage.getRemoved().keySet()) {
            System.out.println(fileName);
        }
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
    }
}
