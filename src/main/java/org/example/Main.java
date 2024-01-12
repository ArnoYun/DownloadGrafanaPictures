package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {

        String URL = args[0];

        String token = args[1];

        String[] parameters = URL.split("/");

        String address = parameters[2];
        String uid = parameters[4];
        String apiUrl = "http://" + address + "/api/dashboards/uid/" + uid;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body();

        Pattern patternId = Pattern.compile("\"id\"\\s*:\\s*(\\d+)");
        Matcher matchId = patternId.matcher(body);

        List<String> panelsId = new ArrayList<>();
        while (matchId.find()) {
            panelsId.add(matchId.group(1));
        }
        System.out.println(panelsId);

        Pattern patternTimeInterval = Pattern.compile("from=\\d+&to=\\d+");
        Matcher matchTimeInterval = patternTimeInterval.matcher(URL);
        String timeInterval = null;
        if(matchTimeInterval.find()){
            timeInterval = matchTimeInterval.group();
        }
        System.out.println(timeInterval);

        for (int i = 0; i < panelsId.size(); i++) {
            getPicture(client, address, uid, panelsId.get(i), timeInterval, "D:\\LT School\\GrafanaReport" +
                    "\\picture" + i +
                    ".png", token);
        }

        System.out.println("DONE!");
        System.exit(0);


    }

    public static void getPicture(HttpClient client, String address, String uid, String panelId,
                                  String timeInterval, String filePath, String token) throws IOException,
            InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://" + address + "/render/d-solo/" + uid + "?orgId=1&" +
                        timeInterval + "&panelId=" + panelId + "&var-transaction=MainPage&width=1500&height=600&tz=Asia" +
                        "%2FYekaterinburg"))
                .header("Authorization", "Bearer "+token)
                .GET()
                .build();

        HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(Path.of(filePath)));
    }
}