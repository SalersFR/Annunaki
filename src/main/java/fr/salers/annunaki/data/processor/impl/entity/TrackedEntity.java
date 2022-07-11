package fr.salers.annunaki.data.processor.impl.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
public class TrackedEntity {

    private int serverPosX, serverPosY, serverPosZ,
            otherPlayerMPPosRotationIncrements;

    private double posX, posY, posZ,
            lastPosX, lastPosY, lastPosZ,
            otherPlayerMPX, otherPlayerMPY, otherPlayerMPZ;

    @Setter
    private boolean cloned;

    public TrackedEntity(double x, double y, double z) {
        serverPosX = doubleToInt(x);
        serverPosY = doubleToInt(y);
        serverPosZ = doubleToInt(z);

        posX = x;
        posY = y;
        posZ = z;
    }

    public TrackedEntity(int spx, int spy, int spz, int pri, double px, double py, double pz, double lpx, double lpy, double lpz, double mpx, double mpy, double mpz) {
        serverPosX = spx;
        serverPosY = spy;
        serverPosZ = spz;
        otherPlayerMPPosRotationIncrements = pri;
        posX = px;
        posY = py;
        posZ = pz;
        lastPosX = lpx;
        lastPosY = lpy;
        lastPosZ = lpz;
        otherPlayerMPX = mpx;
        otherPlayerMPY = mpy;
        otherPlayerMPZ = mpz;

        cloned = true;
    }

    public void setPosition(double x, double y, double z) {
        posX = x;
        posY = y;
        posZ = z;
    }

    public void setPositionAndRotation2(double x, double y, double z) {
        otherPlayerMPX = x;
        otherPlayerMPY = y;
        otherPlayerMPZ = z;

        otherPlayerMPPosRotationIncrements = 3;
    }

    public void onPostTick() {
        if (otherPlayerMPPosRotationIncrements > 0) {
            double d0 = posX + (otherPlayerMPX - posX) / (double) otherPlayerMPPosRotationIncrements;
            double d1 = posY + (otherPlayerMPY - posY) / (double) otherPlayerMPPosRotationIncrements;
            double d2 = posZ + (otherPlayerMPZ - posZ) / (double) otherPlayerMPPosRotationIncrements;

            --otherPlayerMPPosRotationIncrements;

            setPosition(d0, d1, d2);
        }
    }

    public TrackedEntity handleMovement(EntityMovement movement) {
        int intX = doubleToInt(movement.getX());
        int intY = doubleToInt(movement.getY());
        int intZ = doubleToInt(movement.getZ());

        if (movement.getType() == EntityMovementType.RELATIVE) {
            serverPosX += intX;
            serverPosY += intY;
            serverPosZ += intZ;

            setPositionAndRotation2(movement.getX(), movement.getY(), movement.getZ());
        } else {
            serverPosX = intX;
            serverPosY = intY;
            serverPosZ = intZ;

            double deltaX = Math.abs(movement.getX() - posX);
            double deltaY = Math.abs(movement.getY() - posY);
            double deltaZ = Math.abs(movement.getZ() - posZ);

            if (deltaX < 0.03125D && deltaY < 0.015625D && deltaZ < 0.03125D) {
                setPositionAndRotation2(posX, posY, posZ);
            } else {
                setPositionAndRotation2(movement.getX(), movement.getY(), movement.getZ());
            }
        }

        return this;
    }

    // Amazing code
    public TrackedEntity clone() {
        return new TrackedEntity(serverPosX, serverPosY, serverPosZ,
                otherPlayerMPPosRotationIncrements, posX, posY, posZ,
                lastPosX, lastPosX, lastPosZ, otherPlayerMPX,
                otherPlayerMPY, otherPlayerMPZ);
    }

    public int doubleToInt(double d) {
        return (int) (d * 32D);
    }
}
