package net.rulerz.netatmoToTado.netatmo;


import losty.netatmo.NetatmoHttpClient;
import losty.netatmo.model.Measures;
import losty.netatmo.model.Params;
import losty.netatmo.model.Station;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Netatmo {
    final String CLIENT_ID = "";
    final String CLIENT_SECRET = "";
    final String E_MAIL = "";
    final String PASSWORD = "";
    final NetatmoHttpClient client = new NetatmoHttpClient(CLIENT_ID, CLIENT_SECRET);

    private final Double temp;

    public Netatmo() throws ParseException {
        client.login(E_MAIL, PASSWORD);

        List<String> types = Arrays.asList(Params.TYPE_TEMPERATURE, Params.TYPE_PRESSURE, Params.TYPE_HUMIDITY);
        SimpleDateFormat isoFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        isoFormat.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        Date date = isoFormat.parse(new Date().toString());

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        cal.setTime(date);
        cal.add(Calendar.DATE, -1);
        Date yourDate = cal.getTime();



        Station station = client.getStationsData(null, null).get(0);
        System.out.println("NetatmoStation: " + station.getId());
        List<Measures> measures = client.getMeasures(station, station.getModules().get(0), types, Params.SCALE_MAX, yourDate, date, null, false);
        temp = measures.get(measures.size()-1).getTemperature();
        System.out.println("NetatmoStationTemp: " + getTemp());
        System.out.println(" ");
    }

    public Double getTemp() throws NullPointerException {
        if (temp != null) {
            return temp;
        }
        throw new NullPointerException();
    }
}
