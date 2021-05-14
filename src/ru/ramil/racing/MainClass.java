package ru.ramil.racing;

import ru.ramil.racing.model.*;
import java.util.concurrent.*;

public class MainClass {
    public static final int CARS_COUNT;
    private static final CountDownLatch ready;
    private static final CyclicBarrier waitAll;
    private static final CountDownLatch finish;

    static {
        CARS_COUNT = 4;
        ready = new CountDownLatch(CARS_COUNT);
        waitAll = new CyclicBarrier(CARS_COUNT + 1, () ->
                System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> ГОНКА НАЧАЛАСЬ!!!"));
        finish = new CountDownLatch(CARS_COUNT);
    }

    public static void main(String[] args) {
        try {
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> ПОДГОТОВКА!!!");
            Race race = new Race(new Road(60), new Tunnel(CARS_COUNT / 2), new Road(40));
            Car[] cars = new Car[CARS_COUNT];
            for (int i = 0; i < cars.length; i++) {
                cars[i] = new Car(race, 20 + (int) (Math.random() * 10), ready, waitAll, finish);
            }
            for (int i = 0; i < cars.length; i++) {
                new Thread(cars[i]).start();
            }
            ready.await();                  // Ожидать пока все приготовятся
            waitAll.await();                // Старт гонки. Важно, чтобы объявление о начале гонки было написано в главном потоке
            finish.await();                 // Ожидать пока все придут на финиш
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> ГОНКА ЗАКОНЧИЛАСЬ!!!");
        } catch (InterruptedException | BrokenBarrierException ex) {
            ex.printStackTrace();
        }
    }
}
