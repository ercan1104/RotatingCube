import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;

public class RotatingCube extends MIDlet {
    private Display display;
    private CubeCanvas cubeCanvas;

    public RotatingCube() {
        display = Display.getDisplay(this);
        cubeCanvas = new CubeCanvas();
    }

    public void startApp() {
        display.setCurrent(cubeCanvas);
    }

    public void pauseApp() {}

    public void destroyApp(boolean unconditional) {}

    class CubeCanvas extends Canvas implements Runnable {
        private int angle = 0;
        private boolean running = true;

        // Вершины куба
        private float[][] vertices = {
                {-1, -1, -1}, {1, -1, -1}, {1, 1, -1}, {-1, 1, -1},
                {-1, -1, 1}, {1, -1, 1}, {1, 1, 1}, {-1, 1, 1}
        };

        // Ребра куба
        private int[][] edges = {
                {0,1}, {1,2}, {2,3}, {3,0},
                {4,5}, {5,6}, {6,7}, {7,4},
                {0,4}, {1,5}, {2,6}, {3,7}
        };

        private float[][] projected = new float[8][2];

        public CubeCanvas() {
            new Thread(this).start();
        }

        protected void paint(Graphics g) {
            // Очистка экрана
            g.setColor(0x000000);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Центр экрана
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int scale = Math.min(getWidth(), getHeight()) / 4;

            // Проекция вершин
            for (int i = 0; i < 8; i++) {
                float x = vertices[i][0];
                float y = vertices[i][1];
                float z = vertices[i][2];

                // Вращение вокруг осей X и Y
                float cosX = (float)Math.cos(angle * Math.PI / 180);
                float sinX = (float)Math.sin(angle * Math.PI / 180);
                float cosY = (float)Math.cos(angle * Math.PI / 180);
                float sinY = (float)Math.sin(angle * Math.PI / 180);

                // Вращение по X
                float y1 = y * cosX - z * sinX;
                float z1 = y * sinX + z * cosX;

                // Вращение по Y
                float x1 = x * cosY + z1 * sinY;
                z1 = -x * sinY + z1 * cosY;

                // Простая перспективная проекция
                float distance = 4;
                float factor = distance / (distance + z1);

                projected[i][0] = x1 * factor * scale + centerX;
                projected[i][1] = y1 * factor * scale + centerY;
            }

            // Рисование рёбер
            g.setColor(0xFFFFFF);
            for (int i = 0; i < edges.length; i++) {
                int[] edge = edges[i];
                int x1 = (int)projected[edge[0]][0];
                int y1 = (int)projected[edge[0]][1];
                int x2 = (int)projected[edge[1]][0];
                int y2 = (int)projected[edge[1]][1];
                g.drawLine(x1, y1, x2, y2);
            }
        }

        public void run() {
            while (running) {
                angle = (angle + 2) % 360;
                repaint();
                try {
                    Thread.sleep(33); // ~30 FPS
                } catch (InterruptedException e) {}
            }
        }
    }
}