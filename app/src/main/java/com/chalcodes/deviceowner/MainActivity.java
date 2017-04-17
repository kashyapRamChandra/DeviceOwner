package com.chalcodes.deviceowner;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
	private final List<PackageInfo> mInstalledPackages = new ArrayList<>();
	// we have to maintain this set because DPM has no methods to add or remove lockable packages individually
	private final Set<String> mLockablePackages = new HashSet<>();
	private DevicePolicyManager mDpm;
	private ComponentName mAdminComponent;
	private ArrayAdapter<PackageInfo> mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//noinspection ConstantConditions - we have an action bar
		getSupportActionBar().setSubtitle(getString(R.string.action_bar_subtitle));
		mDpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mAdminComponent = new ComponentName(this, DummyAdminReceiver.class);
		mListAdapter = new ArrayAdapter<PackageInfo>(this, R.layout.list_item_package_info, mInstalledPackages) {
			@Override
			public View getView(final int position, View view, final ViewGroup parent) {
				if(view == null) {
					view = getLayoutInflater().inflate(R.layout.list_item_package_info, parent, false);
				}
				final PackageInfo pkg = mInstalledPackages.get(position);
				((TextView) view.findViewById(R.id.name_text))
						.setText(getPackageManager().getApplicationLabel(pkg.applicationInfo));
				((TextView) view.findViewById(R.id.package_text))
						.setText(pkg.packageName);
				((CheckBox) view.findViewById(R.id.check))
						.setChecked(mLockablePackages.contains(pkg.packageName));
				return view;
			}
		};
		final ListView listView = (ListView) findViewById(R.id.list_view);
		listView.setAdapter(mListAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, final long id) {
				final CheckBox box = (CheckBox) view.findViewById(R.id.check);
				final String pkg = mInstalledPackages.get(position).packageName;
				if(mLockablePackages.contains(pkg)) {
					mLockablePackages.remove(pkg);
					box.setChecked(false);
				}
				else {
					mLockablePackages.add(pkg);
					box.setChecked(true);
				}
				final String[] strings = new String[mLockablePackages.size()];
				mLockablePackages.toArray(strings);
				mDpm.setLockTaskPackages(mAdminComponent, strings);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		populatePackageLists();
		checkDeviceOwnership();
	}

	private static final Comparator<PackageInfo> gPackageComparator = new Comparator<PackageInfo>() {
		@Override
		public int compare(final PackageInfo p0, final PackageInfo p1) {
			return p0.packageName.compareTo(p1.packageName);
		}
	};

	private void populatePackageLists() {
		mInstalledPackages.clear();
		mLockablePackages.clear();
		for(PackageInfo pkg : getPackageManager().getInstalledPackages(0)) {
			if((pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				mInstalledPackages.add(pkg);
			}
			if(mDpm.isLockTaskPermitted(pkg.packageName)) {
				mLockablePackages.add(pkg.packageName);
			}
		}
		Collections.sort(mInstalledPackages, gPackageComparator);
		mListAdapter.notifyDataSetChanged();
	}

	private void checkDeviceOwnership() {
		// find out if we are the device owner
		if(!mDpm.isDeviceOwnerApp(BuildConfig.APPLICATION_ID)) {
			// find out if we can become the device owner
			final Account[] accounts = AccountManager.get(this).getAccounts();
			if(accounts.length == 0) {
				// find out if we are a device administrator
				if(mDpm.isAdminActive(mAdminComponent)) {
					// become the device owner
					RunDpmDialog.show(this);
				}
				else {
					// become a device administrator
					final Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
					intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mAdminComponent);
					intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.grant_admin));
					startActivity(intent);
				}
			}
			else {
				// inform the user that we cannot become the device owner
				AccountExistsDialog.show(this);
			}
		}
	}

}
