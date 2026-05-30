package ru.mentee.power.crm.web;

public class Main {
  public static void main(String[] args) throws Exception {
    int port = 8080;
    HelloCrmServer server = new HelloCrmServer(port);
    Runtime.getRuntime().addShutdownHook(new Thread(() -> server.stop()));
    server.start();
    Thread.currentThread().join();
  }
}
