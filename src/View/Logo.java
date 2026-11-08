package View;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * Shows the GoldSpice logo in a JLabel. Used by every screen so the logo
 * looks the same everywhere. Called after initComponents(), so the NetBeans
 * form designer won't overwrite it.
 */
public final class Logo {

    private static final String PATH = "/Images/goldspice_logo.png";
    private static BufferedImage source;

    private Logo() {
    }

    /**
     * Replaces the label's text with the logo, scaled to size x size pixels.
     * If the image can't be loaded the label is left as it was.
     */
    public static void apply(JLabel label, int size, int horizontalAlignment) {
        BufferedImage src = load();
        if (src == null) {
            return;
        }
        label.setText("");
        label.setIcon(new SharpIcon(src, size));
        label.setHorizontalAlignment(horizontalAlignment);
        label.setVerticalAlignment(SwingConstants.CENTER);
    }

    public static void apply(JLabel label, int size) {
        apply(label, size, SwingConstants.LEFT);
    }

    private static BufferedImage load() {
        if (source == null) {
            URL url = Logo.class.getResource(PATH);
            if (url == null) {
                return null;
            }
            try {
                source = ImageIO.read(url);
            } catch (IOException ex) {
                return null;
            }
        }
        return source;
    }

    // Downscales in steps (halving, bicubic) to the real screen pixel size,
    // so the logo stays sharp on normal and display-scaled (HiDPI) screens.
    private static class SharpIcon implements Icon {

        private final BufferedImage src;
        private final int size;
        private BufferedImage cache;
        private int cachePx = -1;

        SharpIcon(BufferedImage src, int size) {
            this.src = src;
            this.size = size;
        }

        private BufferedImage scaled(int target) {
            if (cachePx == target) {
                return cache;
            }
            BufferedImage cur = src;
            int w = cur.getWidth();
            while (w > target) {
                w = Math.max(target, w / 2);
                BufferedImage next = new BufferedImage(w, w, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = next.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g.drawImage(cur, 0, 0, w, w, null);
                g.dispose();
                cur = next;
            }
            cache = cur;
            cachePx = target;
            return cur;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            double scale = g2.getTransform().getScaleX();
            BufferedImage img = scaled((int) Math.round(size * scale));
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.drawImage(img, x, y, size, size, null);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
