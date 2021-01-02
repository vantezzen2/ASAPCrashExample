package io.vantezzen.asaptesting;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.sharksystem.asap.ASAPEngineFS;
import net.sharksystem.asap.ASAPException;
import net.sharksystem.asap.ASAPMessages;
import net.sharksystem.asap.ASAPStorage;
import net.sharksystem.asap.apps.ASAPMessageReceivedListener;

import org.w3c.dom.Text;

import io.vantezzen.asaptesting.R;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

import static net.sharksystem.asap.android.example.ASAPExampleApplication.ASAP_EXAMPLE_APPNAME;

public class ASAPExampleActivity extends ASAPExampleRootActivity implements ASAPMessageReceivedListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example_layout);
        ASAPExampleApplication.getASAPApplication().addASAPMessageReceivedListener(ASAP_EXAMPLE_APPNAME, this);
    }

    public void onClick(View view) throws ASAPException {
        View crashIt = findViewById(R.id.crash);
        View nonCrash = findViewById(R.id.noncrash);

        if(view == nonCrash) {

            // This will work fine so ASAP does work!
            // 127 bytes
            byte[] byteContent = "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo".getBytes();

            this.sendASAPMessage(
                    ASAP_EXAMPLE_APPNAME,
                    "crash://me",
                    byteContent,
                    true
            );

        } else if (view == crashIt) {

            // This will crash other ASAP clients, they don't even need to listen to that URI
            // 128 bytes
            byte[] byteContent = "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo".getBytes();


            this.sendASAPMessage(
                    ASAP_EXAMPLE_APPNAME,
                    "crash://me",
                    byteContent,
                    true
            );

        }

    }

    @Override
    public void asapMessagesReceived(ASAPMessages asapMessages) throws IOException {
        TextView list = this.findViewById(R.id.messagesList);
        Iterator<byte[]> msgInter = asapMessages.getMessages();;

        while(msgInter.hasNext()) {
            byte[] msgBytes = msgInter.next();
            String msg = new String(msgBytes);

            list.setText(
                    list.getText() + "\n" + msg
            );
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //                         ORIGINALER ASAP EXAMPLE CODE - KEINE ÄNDERUNGEN               //
    ///////////////////////////////////////////////////////////////////////////////////////////

    public void asapNotifyOnlinePeersChanged(Set<CharSequence> onlinePeerList) {
        super.asapNotifyOnlinePeersChanged(onlinePeerList);

        TextView peerListTextView = this.findViewById(R.id.onlinePeersList);

        if(onlinePeerList == null || onlinePeerList.size() < 1) {
            peerListTextView.setText("no peers online");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("peers online:");
            sb.append("\n");
            for(CharSequence peerID : onlinePeerList) {
                sb.append("id: ");
                sb.append(peerID);
                sb.append("\n");
            }
            peerListTextView.setText(sb.toString());
        }
        peerListTextView.refreshDrawableState();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //                                         helps debugging                               //
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void asapNotifyBTDiscoverableStopped() {
        super.asapNotifyBTDiscoverableStopped();
        Log.d(this.getLogStart(), "got notified: discoverable stopped");
        Toast.makeText(this, "discoverable stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void asapNotifyBTDiscoveryStopped() {
        super.asapNotifyBTDiscoveryStopped();
        Log.d(this.getLogStart(), "got notified: discovery stopped");
        Toast.makeText(this, "discovery stopped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void asapNotifyBTDiscoveryStarted() {
        super.asapNotifyBTDiscoveryStarted();
        Log.d(this.getLogStart(), "got notified: discovery started");
        Toast.makeText(this, "discovery started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void asapNotifyBTDiscoverableStarted() {
        super.asapNotifyBTDiscoverableStarted();
        Log.d(this.getLogStart(), "got notified: discoverable started");
        Toast.makeText(this, "discoverable started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void asapNotifyBTEnvironmentStarted() {
        super.asapNotifyBTEnvironmentStarted();
        Log.d(this.getLogStart(), "got notified: bluetooth on");
        Toast.makeText(this, "bluetooth on", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void asapNotifyBTEnvironmentStopped() {
        super.asapNotifyBTEnvironmentStopped();
        Log.d(this.getLogStart(), "got notified: bluetooth off");
        Toast.makeText(this, "bluetooth off", Toast.LENGTH_SHORT).show();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //                              asap store test scenario(s)                              //
    ///////////////////////////////////////////////////////////////////////////////////////////

    private final String URI = "sn://chat";
    private final String MESSAGE = "Hi, that's a message";
    private final byte[] BYTE_MESSAGE = MESSAGE.getBytes();

    ASAPStorage asapStorage;

    public void onSetupCleanASAPStorageClick(View view) {
        try {
            this.setupCleanASAPStorage();
        }
        catch (IOException | ASAPException e) {
            Log.d(this.getLogStart(), "exception: " + e.getLocalizedMessage());
        }
        catch (RuntimeException e) {
            Log.d(this.getLogStart(), "runtime exception: " + e.getLocalizedMessage());
        }
    }

    public void onSwitch2ExchangeActivity(View view) {
        this.startActivity(new Intent(this, ASAPExampleMessagingActivity.class));
    }

    private void setupCleanASAPStorage() throws IOException, ASAPException {
        String absoluteFolderName = this.getASAPApplication().getApplicationRootFolder(ASAP_EXAMPLE_APPNAME);
        Log.d(this.getLogStart(), "going to clean folder:  " + absoluteFolderName);

        ASAPEngineFS.removeFolder(absoluteFolderName);

        Log.d(this.getLogStart(), "create asap storage with:  "
                + this.getASAPApplication().getOwnerID()
                + " | "
                + this.getASAPApplication().getApplicationRootFolder(ASAP_EXAMPLE_APPNAME)
                + " | "
                + ASAP_EXAMPLE_APPNAME
        );

        this.asapStorage = ASAPEngineFS.getASAPStorage(
                        this.getASAPApplication().getOwnerID().toString(),
                        this.getASAPApplication().getApplicationRootFolder(ASAP_EXAMPLE_APPNAME),
                        ASAP_EXAMPLE_APPNAME);
    }
}