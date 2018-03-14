package seng302.Command;

import seng302.Donor;
import seng302.DonorManager;

public class DeleteUserCommand implements Command {
	private Donor donor;
	private DonorManager manager;

	public DeleteUserCommand (Donor donor, DonorManager manager) {
		this.donor = donor;
		this.manager = manager;
	}

	@Override
	public void execute() {
		manager.removeDonor(donor);
	}

	@Override
	public void unExecute() {
		manager.addDonor(donor);
	}
}
