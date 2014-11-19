package cz.kinst.jakub.diploma.convoy;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessor;
import cz.cuni.mff.d3s.deeco.knowledge.CloningKnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.RuntimeMetadataFactoryExt;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.kinst.jakub.diploma.convoy.components.Follower;
import cz.kinst.jakub.diploma.convoy.components.LeaderA;
import cz.kinst.jakub.diploma.convoy.components.LeaderB;
import cz.kinst.jakub.diploma.convoy.ensembles.ConvoyEnsemble;
import cz.kinst.jakub.diploma.convoy.model.LogEvent;
import cz.kinst.jakub.diploma.convoy.udpbroadcast.UDPRuntimeBuilder;


public class MainActivity extends ActionBarActivity {

	@InjectView(R.id.log)
	ListView mLog;

	private ArrayAdapter<String> mLogAdapter;
	private RuntimeFramework mDEECoRuntime;
	private boolean mRunning = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.inject(this);
		BusProvider.get().register(this);

		// init ListView
		mLogAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new ArrayList<String>());
		mLog.setAdapter(mLogAdapter);

		// initialize runtime
		initRuntime();
	}

	private void initRuntime() {
		try {

			UDPRuntimeBuilder builder = new UDPRuntimeBuilder();

			RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
			AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model, new CloningKnowledgeManagerFactory());

			processor.process(
					new LeaderA(), new LeaderB(), new Follower(), // Components
					ConvoyEnsemble.class // Ensembles
			);

			mDEECoRuntime = builder.build(getMyIp(), model);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		}
	}

	private String getMyIp() {
		// FIXME: return current device IP address
		return "127.0.0.1";
	}

	private void startRuntime() {
		mDEECoRuntime.start();
		mRunning = true;
		invalidateOptionsMenu();
	}

	private void stopRuntime() {
		mDEECoRuntime.stop();
		mRunning = false;
		invalidateOptionsMenu();
	}

	void addLogMessage(String message) {
		mLogAdapter.insert(message, 0);
		mLogAdapter.notifyDataSetChanged();
	}

	/*
	This method is called by EventBus on main thread whenever new log message is issued from components/ensembles
	 */
	public void onEventMainThread(LogEvent e) {
		addLogMessage(e.getMessage());
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		stopRuntime();
		super.onStop();
	}


	@Override
	protected void onDestroy() {
		BusProvider.get().unregister(this);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.action_start).setVisible(!mRunning);
		menu.findItem(R.id.action_stop).setVisible(mRunning);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_start:
				startRuntime();
				return true;

			case R.id.action_stop:
				stopRuntime();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
