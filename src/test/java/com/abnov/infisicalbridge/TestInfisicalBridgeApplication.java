package com.abnov.infisicalbridge;

import org.springframework.boot.SpringApplication;

public class TestInfisicalBridgeApplication {

	public static void main(String[] args) {
		SpringApplication.from(InfisicalBridgeApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
