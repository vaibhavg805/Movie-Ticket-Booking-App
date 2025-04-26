package com.vaibhav.booking.system.util;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class SeatLockManager {
    private final ConcurrentHashMap<String, ReentrantLock> seatLocks = new ConcurrentHashMap<>();

    public boolean lockSeat(String seatNumber) {
        // Put a new ReentrantLock if it's absent for the given seat number
        seatLocks.putIfAbsent(seatNumber, new ReentrantLock());
        ReentrantLock lock = seatLocks.get(seatNumber);
        boolean isLocked = lock.tryLock();
        return isLocked;
    }

    public void unlockSeat(String seatKey) {
        ReentrantLock lock = seatLocks.get(seatKey);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
