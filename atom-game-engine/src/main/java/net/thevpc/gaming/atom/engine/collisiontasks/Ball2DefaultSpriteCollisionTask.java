package net.thevpc.gaming.atom.engine.collisiontasks;

import net.thevpc.gaming.atom.engine.SceneEngine;
import net.thevpc.gaming.atom.model.CollisionSides;
import net.thevpc.gaming.atom.model.Sprite;

/**
 * SpriteCollisionTask implementation for ball sprites that bounce off borders,
 * walls, and other balls with realistic physics.
 */
public class Ball2DefaultSpriteCollisionTask implements SpriteCollisionTask {

    @Override
    public void install(SceneEngine sceneEngine, Sprite sprite) {
        // Initialization logic if needed
    }

    @Override
    public void uninstall(SceneEngine sceneEngine, Sprite sprite) {
        // Cleanup logic if needed
    }

    /**
     * Handles collision with game borders by bouncing the ball back.
     */
    @Override
    public void collideWithBorder(BorderCollision borderCollision) {
        borderCollision.adjustSpritePosition();

        Sprite sprite = borderCollision.getSprite();
        double currentAngle = sprite.getDirection(); // En radians
        CollisionSides borderSides = borderCollision.getBorderCollisionSides();

        double newAngle = currentAngle;

        // Vertical reflection (North/South borders)
        if (borderSides.isNorth() || borderSides.isSouth()) {
            newAngle = -currentAngle;
        }
        // Horizontal reflection (East/West borders)
        else if (borderSides.isEast() || borderSides.isWest()) {
            newAngle = Math.PI - currentAngle;
        }

        sprite.setDirection(normalizeAngle(newAngle));
    }

    /**
     * Handles collision with another sprite (ball-to-ball collision).
     */
    @Override
    public void collideWithSprite(SpriteCollision spriteCollision) {
        Sprite sprite = spriteCollision.getSprite();
        Sprite other = spriteCollision.getOther();

        // Don't process if other is crossable
        if (other.isCrossable()) {
            return;
        }

        // Only the initiator processes the collision to avoid double processing
        if (!spriteCollision.isInitiator()) {
            return;
        }

        // Calculate positions
        double x1 = sprite.getX() + sprite.getWidth() / 2.0;
        double y1 = sprite.getY() + sprite.getHeight() / 2.0;
        double x2 = other.getX() + other.getWidth() / 2.0;
        double y2 = other.getY() + other.getHeight() / 2.0;

        // Calculate collision vector
        double dx = x2 - x1;
        double dy = y2 - y1;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Avoid division by zero
        if (distance < 0.001) {
            // Balls are on top of each other, push them apart
            double pushAngle = Math.random() * 2 * Math.PI;
            double pushDistance = 3.0;
            sprite.setLocation(
                    sprite.getX() - Math.cos(pushAngle) * pushDistance,
                    sprite.getY() - Math.sin(pushAngle) * pushDistance
            );
            other.setLocation(
                    other.getX() + Math.cos(pushAngle) * pushDistance,
                    other.getY() + Math.sin(pushAngle) * pushDistance
            );
            return;
        }

        // Normalize collision vector
        double nx = dx / distance;
        double ny = dy / distance;

        // Get current velocities (direction in radians)
        double angle1 = sprite.getDirection();
        double angle2 = other.getDirection();

        double v1x = Math.cos(angle1) * sprite.getSpeed();
        double v1y = Math.sin(angle1) * sprite.getSpeed();
        double v2x = Math.cos(angle2) * other.getSpeed();
        double v2y = Math.sin(angle2) * other.getSpeed();

        // Relative velocity
        double dvx = v1x - v2x;
        double dvy = v1y - v2y;

        // Relative velocity in collision normal direction
        double dvn = dvx * nx + dvy * ny;

        // Only bounce if balls are moving towards each other
        if (dvn <= 0) {
            return; // Balls are moving apart
        }

        // Simple elastic collision: exchange velocity components along normal
        // For equal mass balls, we just reflect along the collision normal

        // Calculate tangent vector (perpendicular to normal)
        double tx = -ny;
        double ty = nx;

        // Project velocities onto normal and tangent
        double v1n = v1x * nx + v1y * ny;
        double v1t = v1x * tx + v1y * ty;
        double v2n = v2x * nx + v2y * ny;
        double v2t = v2x * tx + v2y * ty;

        // Exchange normal components (elastic collision with equal masses)
        double v1n_new = v2n;
        double v2n_new = v1n;

        // Reconstruct velocities
        double v1x_new = v1n_new * nx + v1t * tx;
        double v1y_new = v1n_new * ny + v1t * ty;
        double v2x_new = v2n_new * nx + v2t * tx;
        double v2y_new = v2n_new * ny + v2t * ty;

        // Set new directions (keep original speeds)
        double newAngle1 = Math.atan2(v1y_new, v1x_new);
        double newAngle2 = Math.atan2(v2y_new, v2x_new);

        sprite.setDirection(normalizeAngle(newAngle1));
        other.setDirection(normalizeAngle(newAngle2));

        // Push balls apart to prevent sticking
        double overlap = (sprite.getWidth() / 2.0 + other.getWidth() / 2.0) - distance;
        if (overlap > 0) {
            double separationDistance = overlap / 2.0 + 0.5;
            sprite.setLocation(
                    sprite.getX() - nx * separationDistance,
                    sprite.getY() - ny * separationDistance
            );
            other.setLocation(
                    other.getX() + nx * separationDistance,
                    other.getY() + ny * separationDistance
            );
        }
    }

    /**
     * Handles collision with wall tiles by bouncing the ball.
     */
    @Override
    public void collideWithTile(TileCollision tileCollision) {
        tileCollision.adjustSpritePosition();

        Sprite sprite = tileCollision.getSprite();
        double currentAngle = sprite.getDirection(); // En radians
        CollisionSides tileSides = tileCollision.getTileCollisionSides();

        double newAngle = currentAngle;

        // Vertical reflection (North/South walls)
        if (tileSides.isNorth() || tileSides.isSouth()) {
            newAngle = -currentAngle;
        }
        // Horizontal reflection (East/West walls)
        else if (tileSides.isEast() || tileSides.isWest()) {
            newAngle = Math.PI - currentAngle;
        }

        sprite.setDirection(normalizeAngle(newAngle));
    }

    /**
     * Normalizes an angle to the range [0, 2π) radians.
     */
    private double normalizeAngle(double angle) {
        angle = angle % (2 * Math.PI);
        if (angle < 0) {
            angle += 2 * Math.PI;
        }
        return angle;
    }
}