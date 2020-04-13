package gitlet;

/** Command for init.
 * @author Swadhin Nalubola
 */
public class Init extends Command {

    @Override
    public void run(Repo repo, String[] args) {
        if (!super.valid(args, 0, false)) {
            System.exit(0);
        }
        repo.init();
    }
}
