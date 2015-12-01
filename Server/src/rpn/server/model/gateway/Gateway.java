package rpn.server.model.gateway;

import io.netty.channel.ChannelPipeline;
import rpn.server.model.client.Client;

import java.net.InetAddress;


public class Gateway extends Client {

    private String serverHost;
    private int serverPort;

    public Gateway(String host, int port, String serverHost, int serverPort) {
        super(host, port);
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    @Override
    protected void initialisePipeline(ChannelPipeline pipeline) {
        pipeline.addLast(new GatewayHandler(serverHost, serverPort));
    }

    public static void main(String[] args) throws Exception {
        String ip = InetAddress.getLocalHost().getHostAddress();
        int port = 43591;

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new Thread(new Gateway("0.0.0.0", 43590, ip, port)).start();
    }
}