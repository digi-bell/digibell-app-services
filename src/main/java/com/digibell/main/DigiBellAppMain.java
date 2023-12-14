package com.digibell.main;

import com.digibell.verticles.DigiBellVerticle;
import com.digibell.verticles.UserManagementVerticleCore;
import com.digibell.verticles.UserManagementVerticleWeb;
import io.vertx.core.Vertx;

public class DigiBellAppMain {

    public static void main(String[] args) {
        System.out.println("***** Initializing Digi-Bell Application ********");
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new DigiBellVerticle());
        //vertx.deployVerticle(new UserManagementVerticleCore());
        vertx.deployVerticle(new UserManagementVerticleWeb());
        System.out.println("******** Digi-Bell Application Started *********");
    }
}