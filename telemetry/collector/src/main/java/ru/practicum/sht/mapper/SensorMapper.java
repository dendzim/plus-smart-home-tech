package ru.practicum.sht.mapper;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import ru.practicum.sht.model.sensor.*;
import ru.yandex.practicum.kafka.telemetry.event.*;

@Component
public class SensorMapper {
    public SensorEventAvro toAvro(@Valid SensorEvent sensorEvent) {
        if (sensorEvent == null) {
            return null;
        }
        SensorEventAvro.Builder builder = SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp());
        switch (sensorEvent) {
            case ClimateSensorEvent e -> {
                return builder.setPayload(toClimateSensorEventAvro(e)).build();
            }
            case LightSensorEvent e -> {
                return builder.setPayload(toLightSensorEventAvro(e)).build();
            }
            case MotionSensorEvent e -> {
                return builder.setPayload(toMotionSensorEventAvro(e)).build();
            }
            case SwitchSensorEvent e -> {
                return builder.setPayload(toSwitchSensorEventAvro(e)).build();
            }
            case TemperatureSensorEvent e -> {
                return builder.setPayload(toTemperatureSensorEventAvro(e)).build();
            }
            default -> throw new IllegalArgumentException("Unknown type: " + sensorEvent.getClass());
        }
    }

    private ClimateSensorAvro toClimateSensorEventAvro(ClimateSensorEvent climateSensorEvent) {
        if (climateSensorEvent == null) {
            return null;
        }
        return ClimateSensorAvro.newBuilder()
                .setCo2Level(climateSensorEvent.getCo2Level())
                .setHumidity(climateSensorEvent.getHumidity())
                .setTemperatureC(climateSensorEvent.getTemperatureC())
                .build();
    }

    private LightSensorAvro toLightSensorEventAvro(LightSensorEvent lightSensorEvent) {
        if (lightSensorEvent == null) {
            return null;
        }
        return LightSensorAvro.newBuilder()
                .setLinkQuality(lightSensorEvent.getLinkQuality())
                .setLuminosity(lightSensorEvent.getLuminosity())
                .build();
    }

    private MotionSensorAvro toMotionSensorEventAvro(MotionSensorEvent motionSensorEvent) {
        if (motionSensorEvent == null) {
            return null;
        }
        return MotionSensorAvro.newBuilder()
                .setLinkQuality(motionSensorEvent.getLinkQuality())
                .setMotion(motionSensorEvent.getMotion())
                .setVoltage(motionSensorEvent.getVoltage())
                .build();
    }

    private SwitchSensorAvro toSwitchSensorEventAvro(SwitchSensorEvent switchSensorEvent) {
        if (switchSensorEvent == null) {
            return null;
        }
        return SwitchSensorAvro.newBuilder()
                .setState(switchSensorEvent.getState())
                .build();
    }

    private TemperatureSensorAvro toTemperatureSensorEventAvro(TemperatureSensorEvent temperatureSensorEvent) {
        if (temperatureSensorEvent == null) {
            return null;
        }
        return TemperatureSensorAvro.newBuilder()
                .setTemperatureC(temperatureSensorEvent.getTemperatureC())
                .setTemperatureF(temperatureSensorEvent.getTemperatureF())
                .build();
    }
}
