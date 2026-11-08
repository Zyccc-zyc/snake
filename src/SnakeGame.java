import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/** Dependency-free Java 8 Swing snake game. */
public final class SnakeGame extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int CELL = 24, COLS = 28, ROWS = 22, HEADER = 64;
    private final Model model = new Model(COLS, ROWS, new Random());
    private final javax.swing.Timer timer;
    private int best;

    public SnakeGame() {
        setPreferredSize(new Dimension(COLS * CELL, ROWS * CELL + HEADER));
        setBackground(new Color(15, 23, 42));
        timer = new javax.swing.Timer(140, e -> {
            model.tick();
            best = Math.max(best, model.score);
            timerDelay();
            repaint();
        });
        bind("UP", () -> model.turn(0, -1)); bind("W", () -> model.turn(0, -1));
        bind("DOWN", () -> model.turn(0, 1)); bind("S", () -> model.turn(0, 1));
        bind("LEFT", () -> model.turn(-1, 0)); bind("A", () -> model.turn(-1, 0));
        bind("RIGHT", () -> model.turn(1, 0)); bind("D", () -> model.turn(1, 0));
        bind("SPACE", () -> { if (!model.over) model.paused = !model.paused; });
        bind("ENTER", () -> { model.reset(); timerDelay(); });
        addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent e) { model.paused = true; repaint(); }
        });
        timer.start();
    }

    private void timerDelay() { timer.setDelay(Math.max(65, 140 - model.score / 10 * 5)); }
    private void bind(String key, Runnable action) {
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        getActionMap().put(key, new AbstractAction() {
            private static final long serialVersionUID = 1L;
            public void actionPerformed(ActionEvent e) { action.run(); repaint(); }
        });
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(226, 232, 240));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
        g.drawString("SNAKE   Score: " + model.score + "   Best: " + best, 16, 27);
        g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        g.drawString("Arrows / WASD: move     Space: pause     Enter: restart", 16, 49);
        g.setColor(new Color(25, 37, 55));
        for (int x = 0; x <= COLS; x++) g.drawLine(x * CELL, HEADER, x * CELL, getHeight());
        for (int y = 0; y <= ROWS; y++) g.drawLine(0, HEADER + y * CELL, getWidth(), HEADER + y * CELL);
        if (model.food != null) {
            g.setColor(new Color(251, 113, 133));
            g.fillOval(model.food.x * CELL + 4, HEADER + model.food.y * CELL + 4, CELL - 8, CELL - 8);
        }
        int index = 0;
        for (Point p : model.snake) {
            g.setColor(index++ == 0 ? new Color(190, 242, 100) : new Color(52, 211, 153));
            g.fillRoundRect(p.x * CELL + 2, HEADER + p.y * CELL + 2, CELL - 4, CELL - 4, 8, 8);
        }
        if (model.paused || model.over) {
            g.setColor(new Color(0, 0, 0, 185));
            g.fillRect(0, HEADER, getWidth(), ROWS * CELL);
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
            String title = model.over ? (model.won ? "YOU WIN!" : "GAME OVER") : "PAUSED";
            center(g, title, HEADER + ROWS * CELL / 2);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
            center(g, model.over ? "Press Enter to play again" : "Press Space to start / resume", HEADER + ROWS * CELL / 2 + 36);
        }
        g.dispose();
    }

    private void center(Graphics2D g, String text, int y) {
        g.drawString(text, (getWidth() - g.getFontMetrics().stringWidth(text)) / 2, y);
    }

    static final class Model {
        final int cols, rows;
        final Random random;
        final Deque<Point> snake = new ArrayDeque<Point>();
        Point food;
        int dx, dy, nextDx, nextDy, score;
        boolean paused, over, won, turned;
        Model(int cols, int rows, Random random) {
            if (cols < 4 || rows < 2) throw new IllegalArgumentException("Board too small");
            this.cols = cols; this.rows = rows; this.random = random; reset();
        }
        void reset() {
            snake.clear();
            for (int i = 0; i < 3; i++) snake.addLast(new Point(cols / 2 - i, rows / 2));
            dx = nextDx = 1; dy = nextDy = 0; score = 0;
            paused = true; over = won = turned = false; placeFood();
        }
        void turn(int x, int y) {
            if (paused || over || turned || Math.abs(x) + Math.abs(y) != 1 || (x == -dx && y == -dy)) return;
            nextDx = x; nextDy = y; turned = true;
        }
        void tick() {
            if (paused || over) return;
            dx = nextDx; dy = nextDy; turned = false;
            Point head = snake.peekFirst();
            Point next = new Point(head.x + dx, head.y + dy);
            boolean eats = next.equals(food);
            if (next.x < 0 || next.x >= cols || next.y < 0 || next.y >= rows) { over = true; return; }
            int i = 0;
            for (Point p : snake) {
                if (next.equals(p) && (eats || i < snake.size() - 1)) { over = true; return; }
                i++;
            }
            if (!eats) snake.removeLast();
            snake.addFirst(next);
            if (eats) { score += 10; placeFood(); }
        }
        void placeFood() {
            java.util.List<Point> empty = new ArrayList<Point>();
            for (int y = 0; y < rows; y++) for (int x = 0; x < cols; x++) {
                Point p = new Point(x, y); if (!snake.contains(p)) empty.add(p);
            }
            if (empty.isEmpty()) { food = null; won = over = true; }
            else food = empty.get(random.nextInt(empty.size()));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake · Java");
            SnakeGame game = new SnakeGame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(game); frame.pack(); frame.setResizable(false);
            frame.setLocationRelativeTo(null); frame.setVisible(true);
        });
    }
}
