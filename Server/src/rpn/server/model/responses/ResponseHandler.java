package rpn.server.model.responses;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import rpn.server.Server;
import rpn.server.entities.listener.Ack;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class ResponseHandler implements Runnable {

    private static ResponseHandler instance = new ResponseHandler();

    public static ResponseHandler getInstance() {
        return instance;
    }

    private boolean isRunning = false;

    private HashMap<Integer, Response> pending = new HashMap<>();

    private Queue<Response> acknowledged = new LinkedList<>();

    public ResponseHandler() {

    }

    public boolean queue(Response response) {
        if (response.isAcknowledged()) {
            return queueAcknowledged(response);
        } else {
            return queuePending(response);
        }
    }

    private boolean queueAcknowledged(Response response) {
        boolean added;

        synchronized (this) {
            added = acknowledged.add(response);
            if (added && !isRunning) {
                new Thread(this).start();
            }
        }

        return added;
    }

    private boolean queuePending(Response response) {
        synchronized (this) {
            pending.put(response.getId(), response);
        }

        return true;
    }

    public void acknowledge(int id) {
        synchronized (this) {
            for (Response response : pending.values()) {
                response.acknowledge(id);
            }
        }
    }

    public void acknowledge(Ack ack) {
        synchronized (this) {
            if (pending.containsKey(ack.getId())
                    && pending.get(ack.getId()).acknowledge(ack.getSenderID())) {
                System.out.println("Acknowledged " + ack.getId());
                queueAcknowledged(pending.remove(ack.getId()));
            }
        }
    }

    private Response synchronizedPoll() {
        Response response;
        synchronized (this) {
            isRunning = !acknowledged.isEmpty();
            response = acknowledged.poll();
        }
        return response;
    }

    private void process() {
        Response response;
        while ((response = synchronizedPoll()) != null) {
            process(response);
        }
    }

    private void process(Response response) {
        Channel channel = Server.getInstance().getGateway().getChannel();

        ByteBuf out = channel.alloc().buffer();

        out.writeInt(1);
        out.writeInt(response.getSize());
        out.writeInt(response.getRecipientID());

        String host = response.getRecipient();
        for (String octet : host.split("\\.")) {
            out.writeInt(Integer.parseInt(octet));
        }

        out.writeBytes(response.getPayload());

        channel.writeAndFlush(out);
    }

    @Override
    public void run() {
        process();
    }
}
