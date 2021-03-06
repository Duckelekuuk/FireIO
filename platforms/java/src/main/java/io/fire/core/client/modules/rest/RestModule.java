package io.fire.core.client.modules.rest;

import io.fire.core.client.FireIoClient;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RestModule {

    //local variables
    //path = path to the server, build based ont he host and port
    //password = is the optionall password, default = null when no password is set
    private FireIoClient client;
    @Setter private String optionalPath = null;
    @Setter private String password = null;

    /**
     * Initialize the module
     *
     * @param client
     */
    public RestModule(FireIoClient client) {
        this.client = client;
    }

    /**
     * Get the required endpoint
     * (if nothing set, use default)
     *
     * @return regestration endpoint
     */
    private String getEnd() {
        if (optionalPath == null) {
            return "fireio/register/" + password;
        }
        return optionalPath + "/" + password;
    }

    /**
     * Build to endpoint, then return it
     *
     * @return full url for request with ip, port and endpoint
     */
    public String initiateHandshake() {
        try {
            //connect to path + endpoint with password parameter
            URL website = new URL("http://" + client.getHost() + ":" + client.getPort() + "/" + getEnd());
            URLConnection connection = website.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            //return body from the web page (containing uuid or error code)
            return response.toString();
        } catch (Exception e) {
            //could not reach server, either the server is busy or the connection is unavailable
            e.printStackTrace();
            //return null, there are null checks to catch this, no nullpointer danger
            return null;
        }
    }

}
