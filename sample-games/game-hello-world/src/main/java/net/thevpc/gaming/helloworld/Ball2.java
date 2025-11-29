package net.thevpc.gaming.helloworld;
import net.thevpc.gaming.atom.annotations.AtomSprite;
import net.thevpc.gaming.atom.annotations.Inject;
import net.thevpc.gaming.atom.annotations.OnInit;
import net.thevpc.gaming.atom.engine.SceneEngine;
import net.thevpc.gaming.atom.engine.collisiontasks.Ball2DefaultSpriteCollisionTask;
import net.thevpc.gaming.atom.engine.maintasks.MoveSpriteMainTask;
import net.thevpc.gaming.atom.model.Sprite;

/**
 * Created by vpc on 9/23/16.
 */
@AtomSprite(
        name = "Ball2",
        kind = "Ball2",
        sceneEngine = "hello",
        x=2,
        y=2,
        direction = Math.PI/4 + Math.PI,  // Direction inverse de Ball1
        speed = 0.4,  // Deux fois plus rapide (0.2 * 2)
        width = 2,    // Deux fois plus grosse
        height = 2,
        mainTask = MoveSpriteMainTask.class,
        collisionTask = Ball2DefaultSpriteCollisionTask.class
//        collisionTask = BounceSpriteCollisionTask.class

)
public class Ball2 {
    @Inject
    Sprite sprite;
    @Inject
    SceneEngine sceneEngine;

    @OnInit
    private void init(){
        sprite.setLocation(8,8);
    }

}
