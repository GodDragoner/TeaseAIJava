package me.goddragon.teaseai.api.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by GodDragon on 30.03.2018.
 */
public class TeaseDate {

    private final Calendar calendar;

    public TeaseDate(Date date) {
        this.calendar = Calendar.getInstance();
        this.calendar.setTime(date);
    }

    public TeaseDate(long millis) {
        this.calendar = Calendar.getInstance();
        this.calendar.setTimeInMillis(millis);
    }

    public TeaseDate setSecond(int seconds) {
        calendar.set(Calendar.SECOND, seconds);
        return this;
    }

    public TeaseDate setMinute(int minutes) {
        calendar.set(Calendar.MINUTE, minutes);
        return this;
    }

    public TeaseDate setHour(int hour) {
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        return this;
    }

    public TeaseDate setDay(int days) {
        calendar.set(Calendar.DAY_OF_MONTH, days);
        return this;
    }

    public TeaseDate setMonth(int months) {
        calendar.set(Calendar.MONTH, months);
        return this;
    }

    public TeaseDate setYear(int years) {
        calendar.set(Calendar.YEAR, years);
        return this;
    }

    public TeaseDate addSecond(int seconds) {
        calendar.add(Calendar.SECOND, seconds);
        return this;
    }

    public TeaseDate addMinute(int minutes) {
        calendar.add(Calendar.MINUTE, minutes);
        return this;
    }

    public TeaseDate addHour(int hour) {
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        return this;
    }

    public TeaseDate addDay(int days) {
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return this;
    }

    public TeaseDate addMonth(int months) {
        calendar.add(Calendar.MONTH, months);
        return this;
    }

    public TeaseDate addYear(int years) {
        calendar.add(Calendar.YEAR, years);
        return this;
    }

    public int getSecond() {
        return calendar.get(Calendar.SECOND);
    }

    public int getMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    public int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getDayOfWeek() {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH);
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    public long getTimeInMillis() {
        return calendar.getTimeInMillis();
    }

    public boolean before(TeaseDate teaseDate) {
        return calendar.before(teaseDate.getCalendar());
    }

    public boolean after(TeaseDate teaseDate) {
        return calendar.after(teaseDate.getCalendar());
    }

    public boolean sameDay(TeaseDate teaseDate) {
        return calendar.get(Calendar.DAY_OF_YEAR) == teaseDate.getCalendar().get(Calendar.DAY_OF_YEAR) && calendar.get(Calendar.YEAR) == teaseDate.getCalendar().get(Calendar.YEAR);
    }

    public boolean hasPassed() {
        return Calendar.getInstance().after(calendar);
    }

    @Override
    public String toString() {
        return toString("dd/MM/yyyy HH:mm:ss");
    }

    public String toString(String string) {
        SimpleDateFormat formatter = new SimpleDateFormat(string);
        return formatter.format(calendar.getTime());
    }

    public static TeaseDate valueOf(String string) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return new TeaseDate(formatter.parse(string));
    }

    public Calendar getCalendar() {
        return calendar;
    }
}
