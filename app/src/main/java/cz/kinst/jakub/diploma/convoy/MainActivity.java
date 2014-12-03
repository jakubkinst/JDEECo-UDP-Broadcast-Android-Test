package cz.kinst.jakub.diploma.convoy;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cz.cuni.mff.d3s.deeco.annotations.processor.AnnotationProcessor;
import cz.cuni.mff.d3s.deeco.knowledge.CloningKnowledgeManagerFactory;
import cz.cuni.mff.d3s.deeco.model.runtime.api.RuntimeMetadata;
import cz.cuni.mff.d3s.deeco.model.runtime.custom.RuntimeMetadataFactoryExt;
import cz.cuni.mff.d3s.deeco.runtime.RuntimeFramework;
import cz.kinst.jakub.diploma.convoy.components.Device;
import cz.kinst.jakub.diploma.convoy.ensembles.DeviceNetworkEnsemble;
import cz.kinst.jakub.diploma.convoy.udpbroadcast.AndroidUDPBroadcast;
import cz.kinst.jakub.diploma.convoy.udpbroadcast.PacketReceivedEvent;
import cz.kinst.jakub.diploma.convoy.udpbroadcast.PacketReceiverTask;
import cz.kinst.jakub.diploma.convoy.udpbroadcast.UDPRuntimeBuilder;


public class MainActivity extends ActionBarActivity {


    @InjectView(R.id.myName)
    TextView mMyName;
    @InjectView(R.id.others)
    TextView mOthers;
    private RuntimeFramework mDEECoRuntime;
    private boolean mRunning = false;
    private PacketReceiverTask mPacketReceiverTask;
    private AndroidUDPBroadcast mUdpBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        BusProvider.get().register(this);


        // initialize runtime
        initRuntime();
    }

    private void initRuntime() {
        try {

            mUdpBroadcast = new AndroidUDPBroadcast(this) {
                @Override
                protected void onPacketReceived(DatagramPacket packet) {
                    BusProvider.get().post(new PacketReceivedEvent(packet.getData(), packet.getAddress().getHostAddress()));
                }
            };

            UDPRuntimeBuilder builder = new UDPRuntimeBuilder();

            RuntimeMetadata model = RuntimeMetadataFactoryExt.eINSTANCE.createRuntimeMetadata();
            AnnotationProcessor processor = new AnnotationProcessor(RuntimeMetadataFactoryExt.eINSTANCE, model, new CloningKnowledgeManagerFactory());

            processor.process(
                    new Device(mUdpBroadcast.getMyIpAddressString()), // Components
                    DeviceNetworkEnsemble.class // Ensembles
            );

            mDEECoRuntime = builder.build(mUdpBroadcast.getMyIpAddressString(), model, mUdpBroadcast);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void startRuntime() {
        mPacketReceiverTask = new PacketReceiverTask(mUdpBroadcast);
        mPacketReceiverTask.execute();
        mDEECoRuntime.start();
        mRunning = true;
        invalidateOptionsMenu();
    }

    private void stopRuntime() {
        if (mPacketReceiverTask != null)
            mPacketReceiverTask.cancel(true);
        mDEECoRuntime.stop();
        mRunning = false;
        invalidateOptionsMenu();
    }


    /*
    This method is called by EventBus on main thread whenever new log message is issued from components/ensembles
     */
    public void onEventMainThread(DeviceUpdateEvent e) {
        String name = e.getName();
        String others = "";
        for (String s : e.getOtherDevices()) {
            others = others + s + " ";
        }
        mMyName.setText(name);
        mOthers.setText(others);
        Log.e("DeviceUpdate", name + ": " + others);

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
