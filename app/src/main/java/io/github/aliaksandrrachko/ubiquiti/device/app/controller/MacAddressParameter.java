package io.github.aliaksandrrachko.ubiquiti.device.app.controller;

import io.swagger.v3.oas.annotations.Parameter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(description = "MAC address of the device", example = "AA:BB:CC:DD:EE:FF")
public @interface MacAddressParameter {
}
