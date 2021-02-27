package net.rulerz.netatmoToTado;

import losty.netatmo.exceptions.NetatmoOAuthException;
import losty.netatmo.exceptions.NetatmoParseException;
import net.rulerz.netatmoToTado.netatmo.Netatmo;
import net.rulerz.netatmoToTado.tado.*;

import java.text.ParseException;
import java.util.Date;

public class App {
    public static void main( String[] args )
    {
        double netatmoTemp = 0.0;
        TadoState tadoState;
        TadoOffset tadoOffset;
        double newTadoOffsetCelcius;
        double newTadoCelsius;
        final double FAHRENHEIT_TO_CELSIUS = 1.8;

        final String tadoLoginEmail = "";
        final String tadoLoginPassword = "";

        Date date=java.util.Calendar.getInstance().getTime();

        System.out.println("---------- " + date + " ----------");
        try {
            Netatmo netatmo = new Netatmo();
            netatmoTemp = netatmo.getTemp();
        } catch (NetatmoOAuthException | NetatmoParseException | ParseException e) {
            e.printStackTrace();
        }

        try {
            Tado tado = new Tado();
            tado.login(tadoLoginEmail, tadoLoginPassword);
            tadoState = tado.getStateData();
            tadoOffset = tado.getOffsetData();

            if (tadoOffset.celsius < 0) {
                 newTadoCelsius = tadoState.sensorDataPoints.insideTemperature.celsius + (tadoOffset.celsius * -1.0);
            } else {
                newTadoCelsius = tadoState.sensorDataPoints.insideTemperature.celsius - tadoOffset.celsius;
            }
            newTadoOffsetCelcius = (newTadoCelsius - netatmoTemp) * -1.0;
            double absoluteValue = Math.abs(newTadoOffsetCelcius - tadoOffset.celsius);
            boolean isDifferenceBetweenOffsetsGreateThenZeroDotFive = absoluteValue >= 0.5;
            if (isDifferenceBetweenOffsetsGreateThenZeroDotFive) {
                tadoOffset.celsius = newTadoOffsetCelcius;
                tadoOffset.fahrenheit = newTadoOffsetCelcius * FAHRENHEIT_TO_CELSIUS;
                tado.setOffsetData(tadoOffset);
            } else {
                System.out.println("Don't set new Offset");
            }
        } catch (TadoOAuthException | TadoParseException e) {
            e.printStackTrace();
        }
    }

}
