package com.example.javarest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;
import java.io.IOException;
import org.springframework.web.client.RestTemplate;

@RestController
@SpringBootApplication
public class Application {

  private static final String template = "Hello, %s!";
  private final AtomicLong counter = new AtomicLong();


  @RequestMapping("/quote")
  public String quote() {
    RestTemplate restTemplate = new RestTemplate();
    Quote quote = restTemplate.getForObject("https://gturnquist-quoters.cfapps.io/api/random", Quote.class);

    if (quote != null) {
      return quote.getValue().getQuote();
    }
    return "";
  }


  @RequestMapping("/pods")
  public List<String> listPods(@RequestParam(value="ns", defaultValue = "default") String ns) throws IOException, ApiException {
    ApiClient client = Config.defaultClient();
    Configuration.setDefaultApiClient(client);

    CoreV1Api api = new CoreV1Api();
    V1PodList list = api.listNamespacedPod(ns, null, null, null, null, null, null, null, null, null);

    List<String> podNameList = new ArrayList<>();
    for (V1Pod item : list.getItems()) {
      podNameList.add(item.getMetadata().getName());
    }
    return podNameList;
  }

  @RequestMapping("/greeting")
  public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
    return new Greeting(counter.incrementAndGet(),
        String.format(template, name));
  }

  public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
  }
}
