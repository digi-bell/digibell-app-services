package com.digibell.main;

import com.digibell.verticles.DigiBellPetStoreVerticle;
import io.vertx.core.Vertx;

public class DigiBellAppMain {

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new DigiBellPetStoreVerticle());
    }
}