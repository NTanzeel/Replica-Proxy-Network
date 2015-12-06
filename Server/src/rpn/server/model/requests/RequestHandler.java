package rpn.server.model.requests;

import io.netty.buffer.ByteBuf;
import rpn.server.Server;
import rpn.server.model.replica.Replica;
import rpn.server.model.replica.ReplicaHandler;
import rpn.server.model.requests.processors.Buy;
import rpn.server.model.requests.processors.Refresh;
import rpn.server.model.requests.processors.Sell;
import rpn.server.model.responses.Response;
import rpn.server.model.responses.ResponseHandler;

import java.util.LinkedList;
import java.util.Queue;

public class RequestHandler implements Runnable {

    public static RequestHandler instance = new RequestHandler();

    public static RequestHandler getInstance() {
        return instance;
    }

    private boolean isRunning = false;

    private Queue<Request> requests = new LinkedList<>();

    private RequestProcessor[] requestProcessors = {new Buy(), new Sell(), new Refresh()};

    public boolean queue(Request request) {
        boolean added;

        synchronized (this) {
            added = requests.add(request);
            if (added && !isRunning) {
                new Thread(this).start();
            }
        }

        return added;
    }

    private Request synchronizedPoll() {
        Request request;
        synchronized (this) {
            isRunning = !requests.isEmpty();
            request = requests.poll();
        }
        return request;
    }

    private void process() {
        Request request;
        while ((request = synchronizedPoll()) != null) {
            process(request);
        }
    }

    private void process(Request request) {
        ByteBuf responsePayload = requestProcessors[request.getOpCode()].process(request);

        Response response = new Response(request.getId(), request.getSenderId(), request.getSender(), responsePayload);

        if (!request.isReadOnly()) {
            for (Replica replica : ReplicaHandler.getInstance().getReplicas()) {
                replica.update(request.getId(), Server.getInstance().getService().toString());
                response.require(replica.getId());
            }
        }

        ResponseHandler.getInstance().queue(response);
    }

    public void run() {
        process();
    }
}
