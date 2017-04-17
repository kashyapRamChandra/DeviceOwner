package com.chalcodes.deviceowner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;

/**
 * Informs the user that the app cannot become the device owner because a user
 * account exists.
 *
 * @author Kevin Krumwiede
 */
@SuppressWarnings("WeakerAccess") // fragments should be public
public class AccountExistsDialog extends DialogFragment {

	@NonNull
	@Override
	public Dialog onCreateDialog(final Bundle savedInstanceState) {
		setCancelable(false);
		return new AlertDialog.Builder(getActivity())
				.setMessage(R.string.error_account_exists)
				.setNeutralButton(R.string.quit, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialogInterface, final int i) {
						getActivity().finish();
					}
				})
				.create();
	}

	@Override
	public void onPause() {
		super.onPause();
		dismiss();
	}

	static void show(final FragmentActivity activity) {
		new AccountExistsDialog().show(activity.getSupportFragmentManager(), null);
	}
}
