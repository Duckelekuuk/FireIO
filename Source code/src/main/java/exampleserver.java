import io.fire.core.common.body.RequestString;
import io.fire.core.common.eventmanager.enums.Event;
import io.fire.core.common.packets.ChannelPacketPacket;
import io.fire.core.server.FireIoServer;
import io.fire.core.server.modules.client.superclasses.Client;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class exampleserver {

    public static void main(String[] args) {

        System.out.println("Starting server... TESTSERVER.JAVA");

        try {
            FireIoServer server = new FireIoServer(80)
                .setPassword("testpassword1")
                .setRateLimiter(2, 10)

            .on(Event.CONNECT, eventPayload -> {
                Client client = (Client) eventPayload;
                client.send("MOTD", "test");
            })

            .on(Event.CLOSED_UNEXPECTEDLY, eventPayload -> {
                Client client = (Client) eventPayload;
                System.out.println(client.getId() + " closed unexpectedly!");
            })

            .on(Event.DISCONNECT, eventPayload -> {
                Client client = (Client) eventPayload;
                System.out.println(client.getId() + " just disconnected");
            })

            .on("cookie_jar", eventPayload -> {
                ChannelPacketPacket receivedPacket = (ChannelPacketPacket) eventPayload;
                CookieJar cookieJar = (CookieJar) receivedPacket.getPacket();
                System.out.println("Received a cookie jar from : " + receivedPacket.getSender().getId() + ". The jar contains " + cookieJar.getAmount() + " cookies. The cookies type is: " + cookieJar.getType());
                //thank the client for the cookies
                receivedPacket.getSender().send("thanks", "thanks");
            });

            //simple request based endpoint
            server.onRequest("whoami", (client, request, response) -> {
                System.out.println(client.getId().toString() + " asked who it is! sending ip back");
                response.complete(new RequestString("You are: " + client.getInfo().getHostname()));
            });

            //simple http rest endpoints, one clear example and one with a variable
            server.registerEndpoint("/time", req -> {
                return "The server time is: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            });

            //one with a variable, the path is set to /hi/?name
            //this will mean that ?name will be a variable, example
            server.registerEndpoint("/hi/?name", req -> {
                return "Welcome to FireIO " + req.getVariable("name") + "!";
            });


            //debug;
            /*server.getEventHandler().on(gl -> {
                if (gl.getIsEvent()) {
                    System.out.println("Debug: receved event " + gl.getChannel() + " with payload " + gl.getOriginalPayload());
                } else {
                    System.out.println("Debug: receved channel " + gl.getChannel() + " with payload " + gl.getOriginalPayload());
                }
            });*/

            server.broadcast("message", "welcome everybody!");

           //Client client = server.getClient(UUID.fromString("067e6162-3b6f-4ae2-a171-2470b63dff00"));
           //client.send("message", "well hi there! you are the best.");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
