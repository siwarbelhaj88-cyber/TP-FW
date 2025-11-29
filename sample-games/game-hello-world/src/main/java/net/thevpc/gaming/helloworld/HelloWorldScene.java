package net.thevpc.gaming.helloworld;

import net.thevpc.gaming.atom.annotations.*;
import net.thevpc.gaming.atom.debug.AdjustViewController;
import net.thevpc.gaming.atom.engine.SceneEngine;
import net.thevpc.gaming.atom.engine.SpriteFilter;
import net.thevpc.gaming.atom.model.Orientation;
import net.thevpc.gaming.atom.model.Point;
import net.thevpc.gaming.atom.model.RatioDimension;
import net.thevpc.gaming.atom.model.Sprite;
import net.thevpc.gaming.atom.presentation.*;
import net.thevpc.gaming.atom.presentation.components.SLabel;
import net.thevpc.gaming.atom.presentation.layers.Layers;
import net.thevpc.gaming.atom.engine.DefaultSceneEngine;

import java.awt.*;

/**
 * Created by vpc on 9/23/16.
 */
@AtomScene(
        id = "hello",
        title = "Hello World",
        tileWidth = 80,
        tileHeight = 80

)
@AtomSceneEngine(
        id="hello",
        columns = 10,
        rows = 10,
        fps = 25
)


public class HelloWorldScene {

    @Inject
    private Scene scene;
    @Inject
    private SceneEngine sceneEngine;

    private SLabel label;
    private SLabel viesLabel;

    @OnSceneStarted
    private void init() {
        //scene.addLayer(Layers.fillBoardImage("backgroung.jpeg"));


//        scene.addLayer(Layers.fillBoardGradient(
//                Color.GRAY,
//                Color.RED, Orientation.NORTH));
        // Task: Screen color
        scene.addLayer(Layers.fillScreen(Color.yellow));
        scene.addLayer(Layers.debug());
//        scene.addLayer(Layers.fillScreen(Color.BLUE));
        scene.addController(new SpriteController(SpriteFilter.byName("Ball1")).setArrowKeysLayout());
        scene.addController(new SpriteController2(SpriteFilter.byName("Ball2")));

        scene.addController(new AdjustViewController());
        label = new SLabel("Click CTRL-D to switch debug mode, use Arrows to move the ball")
                .setLocation(Point.ratio(0.5f, 0.5f));
        scene.addComponent(label);

        viesLabel = new SLabel("Vies restantes: 0")
                .setLocation(Point.ratio(0.1f, 0.1f));
        scene.addComponent(viesLabel);
        scene.setSpriteView(SpriteFilter.byKind("Ball"), new ImageSpriteView("/ball.png", 8, 4));
        scene.setSpriteView(SpriteFilter.byName("Ball2"), new ImageSpriteView("/ball2.png", 5, 2));
    }

    // Ball2 avec forme carrée et couleur magenta
    //3.3
//        scene.setSpriteView(SpriteFilter.byName("Ball2"), new SpriteView() {
//            @Override
//            public void draw(SpriteDrawingContext context) {
//                Graphics2D g = context.getGraphics();
//                g.setColor(Color.MAGENTA);
//                Shape shape = context.getSpriteShape();
//                Rectangle bounds = shape.getBounds();
//                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
//            }
//
//            @Override
//            public Shape getShape(Sprite sprite, Scene scene) {
//                return new Rectangle(0, 0, (int)sprite.getWidth(), (int)sprite.getHeight());
//            }
//
//            @Override
//            public SpriteViewConstraints getSpriteViewConstraints(Sprite sprite) {
//                return null;
//            }
//        });
//    }


    //3.2
    @OnNextFrame
    private void aChaqueTic() {
        Sprite ball = sceneEngine.getModel().findSpriteByName("Ball1", null, null);
        if (ball != null) {
            viesLabel.setText("Vies restantes: " + ball.getLife());
        }
    }
}
