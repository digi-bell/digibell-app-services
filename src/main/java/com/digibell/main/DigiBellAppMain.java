package com.digibell.main;

import com.digibell.verticles.DigiBellPetStoreVerticle;
import com.digibell.verticles.UserManagementVerticle;
import io.vertx.core.Vertx;

public class DigiBellAppMain {

    public static void main(String[] args) {
        System.out.println("***** Initializing Digi-Bell Application ********");
        Vertx vertx = Vertx.vertx();

        //vertx.deployVerticle(new UserManagementVerticle());
        System.out.println("******** Digi-Bell Application Started *********");
    }
}