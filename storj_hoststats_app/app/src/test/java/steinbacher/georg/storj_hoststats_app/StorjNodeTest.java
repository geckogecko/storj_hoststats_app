package steinbacher.georg.storj_hoststats_app;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import steinbacher.georg.storj_hoststats_app.util.Version;

import static org.junit.Assert.*;

/**
 * Created by georg on 03.09.17.
 */

public class StorjNodeTest {
    public static final String mTestNodeID = "3217206e6e00c336ddf164a0ad88df7f22c8891b";

    @Test
    public void getNodeID() {
        StorjNode storjNode = new StorjNode(mTestNodeID);

        assertEquals(mTestNodeID, storjNode.getNodeID());
    }

    @Test
    public void getLastSeen() {
        StorjNode storjNode = new StorjNode(mTestNodeID);

        //if no lastSeen is set it should be null
        assertEquals(storjNode.getLastSeen(), null);

        // else it should return the set value
        final String dateString = "2017-09-03T15:53:59.927Z";
        storjNode.setLastSeen(dateString);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date date = simpleDateFormat.parse(dateString);
            assertEquals(storjNode.getLastSeen(), date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getPort() {
        StorjNode storjNode = new StorjNode(mTestNodeID);

        //if no port is set it should be 0
        assertEquals(storjNode.getPort(), 0);

        final int port = 4000;
        storjNode.setPort(port);
        assertEquals(storjNode.getPort(), port);
    }

    @Test
    public void getAddress() {
        StorjNode storjNode = new StorjNode(mTestNodeID);

        //if no port is set it should be ""
        assertEquals(storjNode.getAddress(), "");

        final String address = "173.212.220.3";
        storjNode.setAddress(address);
        assertEquals(storjNode.getAddress(), address);
    }

    @Test
    public void getUserAgent() {
        StorjNode storjNode = new StorjNode(mTestNodeID);

        //if no userAgent is set it should be mull
        assertEquals(storjNode.getUserAgent(), null);

        final String userAgent = "10.2.0";
        storjNode.setUserAgent(userAgent);
        assertEquals(storjNode.getUserAgent().toString(), userAgent);
    }

    @Test
    public void getProtocol() {
        StorjNode storjNode = new StorjNode(mTestNodeID);

        //if no userAgent is set it should be mull
        assertEquals(storjNode.getProtocol(), null);

        final String userAgent = "10.2.0";
        storjNode.setProtocol(userAgent);
        assertEquals(storjNode.getProtocol().toString(), userAgent);
    }

    @Test
    public void getResponseTime() {
        StorjNode storjNode = new StorjNode(mTestNodeID);

        //if no userAgent is set it should be mull
        assertEquals(storjNode.getResponseTime(), 0);

        final String responseTime = "2972.1472171335968";
        String split[] = responseTime.split("\\.");
        storjNode.setResponseTime(responseTime);
        assertEquals(storjNode.getResponseTime(), Integer.parseInt(split[0]));
    }

    @Test
    public void getLastTimeout() {
        StorjNode storjNode = new StorjNode(mTestNodeID);

        //if no lastSeen is set it should be null
        assertEquals(storjNode.getLastTimeout(), null);

        // else it should return the set value
        final String dateString = "2017-08-31T23:02:43.442Z";
        storjNode.setLastTimeout(dateString);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date date = simpleDateFormat.parse(dateString);
            assertEquals(storjNode.getLastTimeout(), date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTimeoutRate() {
        StorjNode storjNode = new StorjNode(mTestNodeID);

        //if no userAgent is set it should be mull
        assertEquals(storjNode.getTimeoutRate(), 0, 0.001);

        final String timeoutRate = "0.12";
        storjNode.setTimeoutRate(timeoutRate);
        assertEquals(storjNode.getTimeoutRate(), Float.parseFloat(timeoutRate), 0.001);
    }

    @Test
    public void getLastChecked() {
        StorjNode storjNode = new StorjNode(mTestNodeID);

        //if no userAgent is set it should be mull
        assertEquals(storjNode.getLastChecked(), null);

        final Date currentDate = Calendar.getInstance().getTime();
        storjNode.setLastChecked(currentDate);
        assertEquals(storjNode.getLastChecked(), currentDate);
    }
}
