import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.SwingUtilities;

public final class SnakeGameTest {
    private static int checks;
    private static void check(boolean value, String message) {
        checks++; if (!value) throw new AssertionError(message);
    }
    private static SnakeGame.Model model() { return new SnakeGame.Model(8, 6, new Random(42)); }
    public static void main(String[] args) throws Exception {
        SnakeGame.Model m = model();
        Point initial = new Point(m.snake.peekFirst()); m.tick();
        check(m.snake.peekFirst().equals(initial), "paused game must not move");
        m.paused = false; m.food = new Point(initial.x + 1, initial.y); m.tick();
        check(m.score == 10 && m.snake.size() == 4, "food grows snake and scores");
        check(!m.snake.contains(m.food), "food must be outside snake");
        m.turn(-1, 0); m.tick(); check(m.dx == 1, "reverse turn rejected");
        m = model(); m.paused = false; m.turn(0, -1); m.turn(-1, 0); m.tick();
        check(m.dx == 0 && m.dy == -1, "only one turn per tick");
        m.food = new Point(0, 5); while (!m.over) m.tick();
        check(m.over, "wall collision ends game");
        m.reset(); check(m.paused && !m.over && m.score == 0 && m.snake.size() == 3, "restart resets state");
        m.snake.clear();
        m.snake.add(new Point(2,2)); m.snake.add(new Point(2,3));
        m.snake.add(new Point(1,3)); m.snake.add(new Point(1,2));
        m.paused = false; m.dx = m.nextDx = -1; m.dy = m.nextDy = 0; m.food = new Point(7,5);
        m.tick(); check(!m.over, "moving into vacated tail is legal");
        m.snake.clear();
        m.snake.add(new Point(2,2)); m.snake.add(new Point(2,1));
        m.snake.add(new Point(3,1)); m.snake.add(new Point(3,2)); m.snake.add(new Point(3,3));
        m.dx = m.nextDx = 1; m.tick(); check(m.over, "body collision ends game");
        m = new SnakeGame.Model(4, 2, new Random(1)); m.snake.clear();
        for (int y = 0; y < 2; y++) for (int x = 0; x < 4; x++) m.snake.add(new Point(x,y));
        m.placeFood(); check(m.won && m.over && m.food == null, "full board wins without infinite loop");
        SwingUtilities.invokeAndWait(() -> {
            SnakeGame game = new SnakeGame(); game.setSize(game.getPreferredSize());
            game.paint(new BufferedImage(game.getWidth(), game.getHeight(), BufferedImage.TYPE_INT_RGB).getGraphics());
            check(game.getWidth() == 672, "Swing panel renders at expected size");
        });
        System.out.println("PASS: " + checks + " checks");
        System.exit(0);
    }
}
