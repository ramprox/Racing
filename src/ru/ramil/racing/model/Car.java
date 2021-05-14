package ru.ramil.racing.model;

import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class Car implements Runnable {
    private static int CARS_COUNT;
    private final Race race;
    private final int speed;
    private final String name;

    private final CountDownLatch ready;
    private final CyclicBarrier waitAll;
    private final CountDownLatch finish;

    private static final Lock win = new ReentrantLock();
    private static boolean isWinExists = false;

    public String getName() {
        return name;
    }

    public int getSpeed() {
        return speed;
    }

    public Car(Race race, int speed,
               CountDownLatch ready, CyclicBarrier waitAll, CountDownLatch finish) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
        this.ready = ready;
        this.waitAll = waitAll;
        this.finish = finish;
    }

    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
            ready.countDown();
            waitAll.await();
        } catch (InterruptedException | BrokenBarrierException ex) {
            ex.printStackTrace();
        }
        for(int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);
        }
        if(win.tryLock()) {
            if(!isWinExists) {
                isWinExists = true;
                System.out.println(this.name + " - WIN");
            }
            win.unlock();
        }
        finish.countDown();
    }
}
