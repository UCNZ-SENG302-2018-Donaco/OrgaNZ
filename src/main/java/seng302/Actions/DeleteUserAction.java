package seng302.Actions;

import seng302.Donor;
import seng302.DonorManager;

public class DeleteUserAction implements Action {
	private Donor donor;
	private DonorManager manager;

	public DeleteUserAction(Donor donor, DonorManager manager) {
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